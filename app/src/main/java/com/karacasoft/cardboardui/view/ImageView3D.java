package com.karacasoft.cardboardui.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.Util;

/**
 * Simple View implementation to show images on 3D UI.
 *
 * Created by Karaca on 6/11/2015.
 */
public class ImageView3D extends View3D {

    private Bitmap bitmap;
    private boolean textureReady = false;
    private void createTexture() {
        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(b);

        c.drawBitmap(this.bitmap, 0, 0, null);

        setTextureHandle(Util.loadTexture(b));

        b.recycle();
        textureReady = true;
    }

    public ImageView3D(CardboardUIActivity context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
        createTexture();
        measure();
        valid = false;
    }

    @Override
    public void update() {
        if(!textureReady) createTexture();
        super.update();
        if(!valid)
        {
            float[] vertices = {
                    0.0f, 0.0f ,0.0f,
                    0.0f, getHeight(), 0.0f,
                    getWidth(), 0.0f, 0.0f,
                    getWidth(), getHeight(), 0.0f
            };
            getViewData().setVerticesData(vertices);

            short[] verticesDrawOrder = {
                    0, 1, 2, 2, 1, 3
            };

            getViewData().setVerticesDrawOrder(verticesDrawOrder);

            float[] colors = {
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f
            };

            float[] normals = {
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f
            };

            float[] textures = {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f
            };

            getViewData().setTextureData(textures);
            getViewData().setColorData(colors);
            getViewData().setNormalData(normals);

            initializeBuffers();

            valid = true;
        }
    }

    @Override
    public void measure() {
        setWidth((float) bitmap.getWidth() / bitmap.getHeight());
        setHeight(1.0f);
    }

}
