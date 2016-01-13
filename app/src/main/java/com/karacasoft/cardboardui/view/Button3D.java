package com.karacasoft.cardboardui.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.R;
import com.karacasoft.cardboardui.Util;

/**
 * A button implementation. Displays text and highlights when user moves over it.
 *
 * Can display drawables next to the text. Normal and highlighted versions of the drawable must
 * be used in {@link Button3D#setDrawableLeft(int, int)} or {@link Button3D#setDrawableRight(int, int)}.
 * (I wasn't aware of color filters before. So the implementation may look bad, but it works.)
 *
 * Created by Karaca on 5/23/2015.
 */
public class Button3D extends View3D {

    private String text;

    private int textColor = Color.LTGRAY;
    private float textSize = 60f;

    protected int textureHandleNormal = -1;
    protected int textureHandleHover = -1;

    protected boolean textureReady = false;

    private int drawableLeft = -1;
    private int drawableLeftHover = -1;
    private boolean hasDrawableLeft = false;
    private int drawableRight = -1;
    private int drawableRightHover = -1;
    private boolean hasDrawableRight = false;

    public Button3D(CardboardUIActivity context) {
        super(context);
    }

    public Button3D(CardboardUIActivity context, String text) {
        super(context);
        this.text = text;
        measure();
        this.valid = false;
    }

    protected void createTexture()
    {
        Bitmap b = Bitmap.createBitmap((int) (getWidth() * 100), 100, Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(b);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.button3d_bg);
        if(drawable != null) {
            drawable.setBounds(0, 0, b.getWidth(), b.getHeight());
            drawable.draw(c);
        }

        Paint p = new Paint();
        p.setColor(textColor);
        p.setTextSize(textSize);
        p.setTextAlign(Paint.Align.CENTER);

        c.drawText(text, (getWidth() * 50), textSize, p);

        Bitmap cpy = b.copy(b.getConfig(), true);
        Canvas cpyCanvas = new Canvas(cpy);
        Drawable drawableHover;

        drawableHover = ContextCompat.getDrawable(getContext(), R.drawable.button3d_bg_hover);

        if(drawableHover != null) {
            if(getContext().isFocusModeOn() && isFocusModeFocusable()) {
                drawableHover.setBounds(0, (int) (b.getHeight() - (b.getHeight() * ((float) getFocusAnimTime() / 1500f))), b.getWidth(), b.getHeight());
                setFocusModeTextureUsed(true);
            } else {
                drawableHover.setBounds(0, 0, b.getWidth(), b.getHeight());
            }
            drawableHover.draw(cpyCanvas);
        }

        if(hasDrawableLeft)
        {
            Drawable leftDrawable = ContextCompat.getDrawable(getContext(), drawableLeft);
            Drawable leftDrawableHover = ContextCompat.getDrawable(getContext(), drawableLeftHover);

            if(leftDrawable != null) {

                int realWidth = leftDrawable.getIntrinsicWidth();
                int realHeight = leftDrawable.getIntrinsicHeight();

                float ratio = realWidth / (10f * getWidth());
                float lastHeight = realHeight / ratio;

                leftDrawable.setBounds((int) (10 * getWidth()),
                        (int) (50 - lastHeight / 2),
                        (int) (20 * getWidth()),
                        (int) (50 + lastHeight / 2));
                leftDrawable.draw(c);
            }
            if(leftDrawableHover != null) {
                int realWidth = leftDrawableHover.getIntrinsicWidth();
                int realHeight = leftDrawableHover.getIntrinsicHeight();

                float ratio = realWidth / (10f * getWidth());
                float lastHeight = realHeight / ratio;

                leftDrawableHover.setBounds((int) (10 * getWidth()),
                        (int) (50 - lastHeight / 2),
                        (int) (20 * getWidth()),
                        (int) (50 + lastHeight / 2));
                leftDrawableHover.draw(cpyCanvas);
            }
        }
        if(hasDrawableRight)
        {
            Drawable rightDrawable = ContextCompat.getDrawable(getContext(), drawableRight);
            Drawable rightDrawableHover = ContextCompat.getDrawable(getContext(), drawableRightHover);

            if(rightDrawable != null) {

                int realWidth = rightDrawable.getIntrinsicWidth();
                int realHeight = rightDrawable.getIntrinsicHeight();

                float ratio = realWidth / (10f * getWidth());
                float lastHeight = realHeight / ratio;

                rightDrawable.setBounds((int) (10 * getWidth()),
                        (int) (50 - lastHeight / 2),
                        (int) (20 * getWidth()),
                        (int) (50 + lastHeight / 2));
                rightDrawable.draw(c);
            }
            if(rightDrawableHover != null) {
                int realWidth = rightDrawableHover.getIntrinsicWidth();
                int realHeight = rightDrawableHover.getIntrinsicHeight();

                float ratio = realWidth / (10f * getWidth());
                float lastHeight = realHeight / ratio;

                rightDrawableHover.setBounds((int) (10 * getWidth()),
                        (int) (50 - lastHeight / 2),
                        (int) (20 * getWidth()),
                        (int) (50 + lastHeight / 2));
                rightDrawableHover.draw(cpyCanvas);
            }
        }
        setTextureHandle(Util.loadTexture(b));
        textureHandleNormal = getTextureHandle();

        p.setColor(invertColor(textColor));
        cpyCanvas.drawText(text, (getWidth() * 50), textSize, p);

        textureHandleHover = Util.loadTexture(cpy);
        b.recycle();
        textureReady = true;
    }

    public static int invertColor(int color)
    {
        return Color.rgb(255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color));
    }

    private int textureUpdateCounter = 0;

    @Override
    public void update() {
        if(!textureReady) createTexture();
        super.update();
        if(isLookingAt)
        {
            if(getContext().isFocusModeOn() && isFocusModeFocusable()) {
                if(textureUpdateCounter > 6) {
                    createTexture();
                    textureUpdateCounter = 0;
                } else {
                    textureUpdateCounter++;
                }
            } else {
                if(isFocusModeTextureUsed())
                {
                    createTexture();
                }
            }
            setTextureHandle(textureHandleHover);
        }else{
            setTextureHandle(textureHandleNormal);
        }
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
        measure();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setDrawableLeft(int drawableLeft, int drawableLeftHover) {
        this.drawableLeft = drawableLeft;
        this.drawableLeftHover = drawableLeftHover;
        this.hasDrawableLeft = true;
        textureReady = false;
    }

    public void setDrawableRight(int drawableRight, int drawableRightHover) {
        this.drawableRight = drawableRight;
        this.drawableRightHover = drawableRightHover;
        this.hasDrawableRight = true;
        textureReady = false;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
