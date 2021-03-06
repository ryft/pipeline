package uk.co.ryft.pipeline.model.element.drawable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.co.ryft.pipeline.model.lighting.LightingModel;

public class GL_Composite implements Drawable {

    protected final List<Drawable> mComponents;

    public GL_Composite(Collection<? extends Drawable> drawables) {
        
        mComponents = new LinkedList<Drawable>();
        mComponents.addAll(drawables);
    }

    @Override
    public void draw(LightingModel lighting, float[] mvMatrix, float[] mvpMatrix) {
        for (Drawable d : mComponents)
            d.draw(lighting, mvMatrix, mvpMatrix);
    }
}