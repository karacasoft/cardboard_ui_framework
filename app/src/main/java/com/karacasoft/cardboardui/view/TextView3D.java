package com.karacasoft.cardboardui.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.Util;

/**
 * Simple View implementation that shows text on it.
 *
 * Created by Karaca on 5/23/2015.
 */
public class TextView3D extends View3D {

    private String text;

    private int backgroundColor = Color.WHITE;
    private int textColor = Color.BLACK;

    private boolean textureReady = false;

    public TextView3D(CardboardUIActivity context, String text)
    {
        super(context);
        this.text = text;
        measure();
        valid = false;
    }

    private void createTexture()
    {
        Bitmap b = Bitmap.createBitmap((int) (getWidth() * 100), 100, Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(b);
        c.drawColor(backgroundColor);

        Paint p = new Paint();
        p.setColor(textColor);
        p.setTextSize(90f);
        p.setTextAlign(Paint.Align.CENTER);

        c.drawText(text, (getWidth() * 50), 90, p);

        setTextureHandle(Util.loadTexture(b));
        b.recycle();
        textureReady = true;
    }

    public void alignToCenter()
    {
        this.translate(-this.getWidth() / 2, 0.0f, 0.0f);
    }

    @Override
    public void update() {
        super.update();
        if(!textureReady) createTexture();
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
        Paint p = new Paint();
        p.setTextSize(90f);
        Rect bounds = new Rect();
        p.getTextBounds(this.text, 0, this.text.length(), bounds);
        setWidth((float) bounds.width() / bounds.height());
        setHeight(1.0f);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textureReady = false;
        valid = false;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        textureReady = false;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textureReady = false;
        invalidate();
    }
}
