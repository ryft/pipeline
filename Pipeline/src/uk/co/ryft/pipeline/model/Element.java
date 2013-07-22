
package uk.co.ryft.pipeline.model;

import uk.co.ryft.pipeline.R;
import uk.co.ryft.pipeline.gl.Colour;
import uk.co.ryft.pipeline.gl.FloatPoint;
import uk.co.ryft.pipeline.gl.shapes.Polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element implements Comparable<Element>, Serializable, Cloneable {

    private static final long serialVersionUID = 5661009688352125290L;

    public static enum Type {
        GL_POINTS, GL_LINES, GL_LINE_STRIP, GL_LINE_LOOP, GL_TRIANGLES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_QUADS, GL_QUAD_STRIP, GL_POLYGON
    };

    private static final Map<Type, String> mDescriptionMap;
    static {
        Map<Type, String> descriptionMap = new HashMap<Type, String>();
        descriptionMap.put(Type.GL_POINTS, "Single points");
        descriptionMap.put(Type.GL_LINES, "Distinct lines");
        descriptionMap.put(Type.GL_LINE_STRIP, "Line strip");
        descriptionMap.put(Type.GL_LINE_LOOP, "Line loop");
        descriptionMap.put(Type.GL_TRIANGLES, "Distinct triangles");
        descriptionMap.put(Type.GL_TRIANGLE_STRIP, "Triangle strip");
        descriptionMap.put(Type.GL_TRIANGLE_FAN, "Triangle fan");
        descriptionMap.put(Type.GL_QUADS, "Distinct quads");
        descriptionMap.put(Type.GL_QUAD_STRIP, "Quad strip");
        descriptionMap.put(Type.GL_POLYGON, "Convex Polygon");
        mDescriptionMap = Collections.unmodifiableMap(descriptionMap);
    }

    protected Type mType;
    protected final List<FloatPoint> mVertices = new ArrayList<FloatPoint>();
    protected Colour mColour = Colour.GREEN;

    public Element(Type type) {
        mType = type;
    }

    public Element(Type type, List<FloatPoint> vertices) {
        mType = type;
        mVertices.addAll(vertices);
    }

    public Element(Type type, List<FloatPoint> vertices, Colour colour) {
        mType = type;
        mVertices.addAll(vertices);
        mColour = colour;
    }

    public Type getType() {
        return mType;
    }

    public List<FloatPoint> getVertices() {
        // TODO this is unsafe -- passing a reference to a list.
        return mVertices;
    }

    public String getTitle() {
        return mDescriptionMap.get(mType) + "\n(" + mType.toString() + ")";
    }

    public String getSummary() {
        String summary = "Consists of " + mVertices.size();
        if (mVertices.size() == 1)
            summary += " vertex.";
        else
            summary += " vertices.";
        return summary;
    }

    public int getIconRef() {
        return R.drawable.ic_element;
    }

    /**
     * Instantiates a new Drawable object which represents this element, and is
     * ready to draw in the renderer.
     * 
     * @return Drawable representation of this element.
     */
    public Drawable getDrawable() {

        if (mType == Type.GL_POLYGON) {

            // Convert vertices to float array
            float[] coords = new float[mVertices.size() * 3];
            int i = 0;
            for (FloatPoint fp : mVertices) {
                coords[i] = fp.getX();
                coords[i + 1] = fp.getY();
                coords[i + 2] = fp.getZ();
                i += 3;
            }
            return new Polygon(coords, mColour.getColour(), mVertices.size());

        } else
            return null;
    }

    @Override
    public Object clone() {
        return new Element(mType, mVertices, mColour);
    }

    @Override
    public int compareTo(Element arg0) {
        // Compare z-orders. TODO: cache these min/max values for faster
        // sorting.
        return 0;
    }

}
