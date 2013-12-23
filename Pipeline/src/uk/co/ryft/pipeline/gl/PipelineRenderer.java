package uk.co.ryft.pipeline.gl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.co.ryft.pipeline.model.Camera;
import uk.co.ryft.pipeline.model.Element;
import uk.co.ryft.pipeline.model.Transformation;
import uk.co.ryft.pipeline.model.shapes.Composite;
import uk.co.ryft.pipeline.model.shapes.Primitive;
import uk.co.ryft.pipeline.model.shapes.ShapeFactory;
import uk.co.ryft.pipeline.model.shapes.Primitive.Type;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class PipelineRenderer implements Renderer {

    private static final String TAG = "PipelineRenderer";

    // OpenGL matrices stored in float arrays (column-major order)
    private final float[] mModelMatrix = new float[16];
    private final float[] mCameraModelMatrix = new float[16];

    private final float[] mViewMatrix = new float[16];
    private final float[] mCameraViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    
    private final float[] mVPMatrix = new float[16];
    
    private final float[] mMVMatrix = new float[16];
    private final float[] mCVMatrix = new float[16];
    
    private final float[] mMVPMatrix = new float[16];
    private final float[] mCVPMatrix = new float[16];

    private final Map<Element, Drawable> mElements = new LinkedHashMap<Element, Drawable>();

    private final List<Transformation> mModelTransformations = new LinkedList<Transformation>();
    
    static Camera mActualCamera = new Camera(new Float3(2, 2, 2), new Float3(0, 0, 0), new Float3(0, 1, 0), -1, 1, -1, 1, 1, 7);
    static Camera mVirtualCamera = new Camera(new Float3(-1f, 0.5f, 0.5f), new Float3(0, 0, 1), new Float3(0, 1, 0), -0.25f, 0.25f, -0.25f, 0.25f, 0.5f, 1.5f);

    // For touch events
    // TODO: Implement synchronised block for this.
    public volatile float mAngle;
    private final float[] mModelRotationMatrix = new float[16];

    private static Composite sAxes;
    private static Composite sCamera;
    static {
        LinkedList<Element> axes = new LinkedList<Element>();
        LinkedList<Element> camera = new LinkedList<Element>();

        LinkedList<Float3> lineCoords = new LinkedList<Float3>();
        // XXX i < 1.1 is required to draw the edge lines
        for (float i = -1; i < 1.1; i += 0.1) {
            lineCoords.add(new Float3(i, 0, -1));
            lineCoords.add(new Float3(i, 0, 1));
            lineCoords.add(new Float3(-1, 0, i));
            lineCoords.add(new Float3(1, 0, i));
        }
        axes.add(new Primitive(Type.GL_LINES, lineCoords, Colour.GREY));
        
        LinkedList<Float3> points = new LinkedList<Float3>();
        points.add(new Float3(0, 0, 0));
        points.add(new Float3(1, 0, 0));
        points.add(new Float3(0, 0, 0));
        points.add(new Float3(0, 1, 0));
        points.add(new Float3(0, 0, 0));
        points.add(new Float3(0, 0, 1));
        axes.add(new Primitive(Type.GL_LINES, points, Colour.WHITE));

        LinkedList<Float3> arrowX = new LinkedList<Float3>();
        arrowX.add(new Float3(0.8f, 0.1f, -0.1f));
        arrowX.add(new Float3(1, 0, 0));
        arrowX.add(new Float3(0.8f, -0.1f, 0.1f));
        arrowX.add(new Float3(0.9f, 0, 0));
        axes.add(new Primitive(Type.GL_LINE_LOOP, arrowX, Colour.RED));

        LinkedList<Float3> arrowY = new LinkedList<Float3>();
        arrowY.add(new Float3(-0.1f, 0.8f, 0.1f));
        arrowY.add(new Float3(0, 1, 0));
        arrowY.add(new Float3(0.1f, 0.8f, -0.1f));
        arrowY.add(new Float3(0, 0.9f, 0));
        axes.add(new Primitive(Type.GL_LINE_LOOP, arrowY, Colour.GREEN));

        LinkedList<Float3> arrowZ = new LinkedList<Float3>();
        arrowZ.add(new Float3(0.1f, -0.1f, 0.8f));
        arrowZ.add(new Float3(0, 0, 1));
        arrowZ.add(new Float3(-0.1f, 0.1f, 0.8f));
        arrowZ.add(new Float3(0, 0, 0.9f));
        axes.add(new Primitive(Type.GL_LINE_LOOP, arrowZ, Colour.BLUE));
        
        // Add a camera located at the origin pointing along the negative z-axis
        // to be transformed into place by the virtual camera model matrix
        // FIXME This is proving to be problematic in terms of render time
        camera.add(ShapeFactory.buildCamera(mVirtualCamera, 0.25f));
        camera.add(ShapeFactory.buildFrustum(mVirtualCamera));

        sAxes = new Composite(Composite.Type.CUSTOM_SHAPE, axes);
        sCamera = new Composite(Composite.Type.CUSTOM_SHAPE, camera);
    }

    public void interact() {
//        mModelTransformations.add(new Translation(new FloatPoint(0, 1, 0), 100));
//        mModelTransformations.add(new Rotation(180, new FloatPoint(0, 0, 1), 100));
//        mCameraEyeTransformations.add(new Translation(new FloatPoint(0, 0.5f, 0), 100));
//        mActualCamera.transformTo(mVirtualCamera, 100);
    }

    // TODO Should these belong here?
    public static final String VERTEX_SHADER_EMPTY =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // the order must be matrix * vector as the matrix is in col-major order.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String FRAGMENT_SHADER_EMPTY =
                    "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame colour
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        
        // Enable depth buffer and set parameters
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        
        // Enable face culling and set parameters
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);
        
        // For touch events
        Matrix.setIdentityM(mModelRotationMatrix, 0);
    }

    

    @Override
    public void onDrawFrame(GL10 unused) {

        // Clear background colour and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set up the model (world transformation) matrix
        Matrix.setIdentityM(mModelMatrix, 0);

        // Get the current camera view matrices
        mActualCamera.setViewMatrix(mViewMatrix, 0);
        mVirtualCamera.setViewMatrix(mCameraViewMatrix, 0);
        
        // The camera model matrix transforms the camera to its correct position and orientation in world space
        Matrix.invertM(mCameraModelMatrix, 0, mCameraViewMatrix, 0);

        // Apply all transformations to the world, in order, in their current state
//        for (Transformation t : mModelTransformations)
//            Matrix.multiplyMM(mModelMatrix, 0, t.next(), 0, mModelMatrix, 0);
        
        // Combine the current rotation matrix with the projection and camera view for touch-rotation
        Matrix.setRotateM(mModelRotationMatrix, 0, mAngle, 0, 1, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mModelRotationMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mCameraModelMatrix, 0, mModelRotationMatrix, 0, mCameraModelMatrix, 0);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mCVMatrix, 0, mViewMatrix, 0, mCameraModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        Matrix.multiplyMM(mCVPMatrix, 0, mProjectionMatrix, 0, mCVMatrix, 0);

        // Ignore model (world) coord transformation when drawing axes
        sAxes.getDrawable().draw(mMVPMatrix);
        sCamera.getDrawable().draw(mCVPMatrix);
        
        // Draw world objects in the scene
        for (Element e : mElements.keySet()) {
            if (mElements.get(e) == null)
                mElements.put(e, e.getDrawable());
            Drawable d = mElements.get(e);
            if (d != null)
                d.draw(mMVPMatrix);
            else
                // Occasionally happens when app is quitting
                // TODO: Investigate turning off continuous rendering when quitting
                System.out.println("Ruh-roh, null drawable!");
        }

    }
    
    protected void printMatrix(float[] m, int cols, int rows) {
        for (int i = 0; i < rows; i++) {
            if (i == 0)
                System.out.print("[");
            else
                System.out.print(" ");
            for (int j = 0; j < cols; j++) {
                System.out.print(m[i + (j * 4)] + " ");
            }
            if (i == rows - 1)
                System.out.println("]");
            else
                System.out.println();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        
        mActualCamera.setProjectionMatrix(mProjectionMatrix, 0, width, height);

    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call just after making it:
     * 
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
     * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
     * </pre>
     * 
     * If the operation is not successful, the check throws an error.
     * 
     * @param glOperation
     *            - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void addToScene(Element e) {
        mElements.put(e, null);
    }

    public void updateScene(List<Element> elements) {
        mElements.clear();
        for (Element e : elements)
            mElements.put(e, null);
    }

}
