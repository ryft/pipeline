package uk.co.ryft.pipeline.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.co.ryft.pipeline.R;
import uk.co.ryft.pipeline.gl.Drawable;
import uk.co.ryft.pipeline.gl.FloatPoint;
import uk.co.ryft.pipeline.model.Element;
import uk.co.ryft.pipeline.model.shapes.ShapeFactory;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {

    protected PipelineSurface mPipelineView;
    protected ArrayList<Element> mElements;

    protected static final int EDIT_SCENE_REQUEST = 1;

    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupActionBar();

        mPipelineView = (PipelineSurface) findViewById(R.id.pipeline_surface);
        
        final GestureDetector gestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                boolean editMode = mPipelineView.toggleEditMode();
                if (editMode)
                    mPipelineView.setBackgroundResource(R.drawable.surface_border);
                else
                    mPipelineView.setBackgroundResource(0);
            }
        });
        
        mPipelineView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
            
        });

        // Get elements from returning activity intent or saved state, if
        // possible.
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey("elements")) {
            mElements = (ArrayList<Element>) extras.getSerializable("elements");

        } else if (savedInstanceState != null && savedInstanceState.containsKey("elements")) {
            mElements = (ArrayList<Element>) savedInstanceState.getSerializable("elements");

        } else {
            mElements = new ArrayList<Element>();
        }

        List<Drawable> scene = new LinkedList<Drawable>();
        for (Element e : mElements) {
            Drawable d = e.getDrawable();
            scene.add(d);
        }

        updateScene();
        
    }
    
//    private void printVector(float[] v) {
//        System.out.print("[ ");
//        for (int i = 0; i < v.length; i++)
//            System.out.print(v[i]+" ");
//        System.out.println("]");
//    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    protected void updateScene() {
        mPipelineView.updateScene(mElements);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure
        // that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.action_scene:
                Intent intent = new Intent(this, SceneActivity.class);
                intent.putExtra("elements", mElements);
                startActivityForResult(intent, EDIT_SCENE_REQUEST);
                break;
                
            case R.id.action_draw_axes:

                LinkedList<FloatPoint> box = new LinkedList<FloatPoint>();
                
                // Front face
                box.add(new FloatPoint(0.2f, 0.2f, -0.2f));
                box.add(new FloatPoint(0.2f, -0.2f, -0.2f));
                box.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
                box.add(new FloatPoint(-0.2f, 0.2f, -0.2f));
                box.add(new FloatPoint(0.2f, 0.2f, -0.2f));
                
                // Top
                box.add(new FloatPoint(0.2f, 0.2f, 0.2f));
                box.add(new FloatPoint(0.2f, -0.2f, 0.2f));
                
                box.add(new FloatPoint(0.2f, -0.2f, -0.2f));
                box.add(new FloatPoint(0.2f, -0.2f, 0.2f));
                
                box.add(new FloatPoint(-0.2f, -0.2f, 0.2f));
                box.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
                box.add(new FloatPoint(-0.2f, -0.2f, 0.2f));

                box.add(new FloatPoint(-0.2f, 0.2f, 0.2f));
                box.add(new FloatPoint(-0.2f, 0.2f, -0.2f));
                box.add(new FloatPoint(-0.2f, 0.2f, 0.2f));
                box.add(new FloatPoint(0.2f, 0.2f, 0.2f));
                
//                mElements.add(new Primitive(Type.GL_LINE_LOOP, box, Colour.WHITE));

                LinkedList<FloatPoint> backface = new LinkedList<FloatPoint>();
                backface.add(new FloatPoint(0.2f, 0.2f, -0.2f));
                backface.add(new FloatPoint(0.2f, -0.2f, -0.2f));
                backface.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
                backface.add(new FloatPoint(0.2f, 0.2f, -0.2f));
                backface.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
                backface.add(new FloatPoint(-0.2f, 0.2f, -0.2f));
//                mElements.add(new Primitive(Type.GL_TRIANGLES, backface, Colour.RED));

                LinkedList<FloatPoint> frontface = new LinkedList<FloatPoint>();
                frontface.add(new FloatPoint(0.2f, 0.2f, 0.2f));
                frontface.add(new FloatPoint(-0.2f, -0.2f, 0.2f));
                frontface.add(new FloatPoint(0.2f, -0.2f, 0.2f));
                frontface.add(new FloatPoint(0.2f, 0.2f, 0.2f));
                frontface.add(new FloatPoint(-0.2f, 0.2f, 0.2f));
                frontface.add(new FloatPoint(-0.2f, -0.2f, 0.2f));
//                mElements.add(new Primitive(Type.GL_TRIANGLES, frontface, Colour.GREEN));

                LinkedList<FloatPoint> sideface = new LinkedList<FloatPoint>();
                sideface.add(new FloatPoint(-0.2f, 0.2f, 0.2f));
                sideface.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
                sideface.add(new FloatPoint(-0.2f, -0.2f, 0.2f));
                sideface.add(new FloatPoint(-0.2f, 0.2f, 0.2f));
                sideface.add(new FloatPoint(-0.2f, 0.2f, -0.2f));
                sideface.add(new FloatPoint(-0.2f, -0.2f, -0.2f));
//                mElements.add(new Primitive(Type.GL_TRIANGLES, sideface, Colour.BLUE));
                
//                mElements.add(ConvexPolygon.getRegularPolygon(60, new FloatPoint(0,0,0), 1, 0, Colour.MAGENTA).rotate(90, 0, 1, 0).translate(0, -1, 0).rotate(180, 1,0,0));
//                mElements.add(ConvexPolygon.getCylinder(16, new FloatPoint(0,0,0), 1f, 0.5f, 0, Colour.CYAN));
                mElements.add(ShapeFactory.buildCamera(new FloatPoint(0,0,0), 0.5f));
                
                
                updateScene();
                break;
                
            case R.id.action_change_perspective:
                mPipelineView.toggle();
//                mPipelineView.boop();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == EDIT_SCENE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                @SuppressWarnings("unchecked")
                ArrayList<Element> newElems = (ArrayList<Element>) data.getExtras()
                        .getSerializable("elements");
                mElements = newElems;
                updateScene();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putSerializable("elements", mElements);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPipelineView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPipelineView.onResume();
    }

    public PipelineSurface getPipelineView() {
        return mPipelineView;
    }

    public void setPipelineView(PipelineSurface mPipelineView) {
        this.mPipelineView = mPipelineView;
    }

}
