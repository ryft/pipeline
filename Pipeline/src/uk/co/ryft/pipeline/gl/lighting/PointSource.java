package uk.co.ryft.pipeline.gl.lighting;

import android.opengl.GLES20;
import uk.co.ryft.pipeline.gl.PipelineRenderer;
import uk.co.ryft.pipeline.gl.shapes.GL_Primitive;

public class PointSource extends LightingModel {
    
    protected PointSource() {
        super(Model.POINT_SOURCE);
    }

    @Override
    protected String[] getVertexShaderAttributes() {
        return new String[] {"a_Position"};
    }

    @Override
    public String getVertexShader(int primitiveType) {
        return   "uniform mat4 u_MVPMatrix;      \n"
                + "attribute vec4 a_Position;     \n"
                + "                               \n"
                + "void main() {                  \n"
                + "                               \n"
                + "    gl_Position = u_MVPMatrix  \n"
                + "                * a_Position;  \n"
                + "    gl_PointSize = 10.0;        \n"
                + "}                              \n";
    }

    @Override
    public String getFragmentShader() {
        return   "precision mediump float;       \n"
                + "void main() {                  \n"
                + "                               \n"
                + "    gl_FragColor = vec4(1.0,   \n"
                + "    1.0, 1.0, 1.0);            \n"
                + "}                              \n";
    }

    @Override
    public void draw(GL_Primitive primitive, float[] mvMatrix, float[] mvpMatrix) {

        // Add our shader program to the OpenGL ES environment
        int glProgram = getGLProgram(primitive.mPrimitiveType);
        GLES20.glUseProgram(glProgram);

        // Set program handles for drawing
        int mMVPMatrixHandle;
        int mPositionHandle;

        // Get handle to vertex shader's position member
        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "a_Position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, primitive.mVertexStride,
                primitive.mVertexBuffer);

        // Get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "u_MVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the primitive
        GLES20.glDrawArrays(primitive.mPrimitiveType, 0, primitive.mVertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        
        PipelineRenderer.checkGlError();
    }
}
