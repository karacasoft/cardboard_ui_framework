package com.karacasoft.cardboardui.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.R;
import com.karacasoft.cardboardui.Util;

/**
 * A subclass of Button3D that is used to show a custom image on a normal Button3D.
 *
 * If you want to display an image right next to the Button. You might want to check
 * {@link Button3D#setDrawableLeft(int, int)} and {@link Button3D#setDrawableRight(int, int)}.
 *
 * Created by Karaca on 6/3/2015.
 */
public class ImageButton3D extends Button3D {

    private Bitmap bitmap;

    @Override
    protected void createTexture() {
        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(b);

        c.drawBitmap(this.bitmap, 0, 0, null);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.button3d_bg);
        if(drawable != null) {
            drawable.setBounds(0, 0, b.getWidth(), b.getHeight());
            drawable.draw(c);
        }

        setTextureHandle(Util.loadTexture(b));
        textureHandleNormal = getTextureHandle();

        Drawable drawableHover;

        if(getContext().isFocusModeOn() && isFocusModeFocusable())
        {
            drawableHover = ContextCompat.getDrawable(getContext(), R.drawable.button3d_bg_hover);
        } else {
            drawableHover = ContextCompat.getDrawable(getContext(), R.drawable.imagebutton3d_hover);
        }
        if(drawableHover != null) {
            if(getContext(). isFocusModeOn() && isFocusModeFocusable()) {
                drawableHover.setBounds(0, (int) (b.getHeight() - (b.getHeight() * ((float) getFocusAnimTime() / 1500f))), b.getWidth(), b.getHeight());
                setFocusModeTextureUsed(true);
            } else {
                drawableHover.setBounds(0, 0, b.getWidth(), b.getHeight());
            }
            drawableHover.draw(c);
        }

        textureHandleHover = Util.loadTexture(b);
        b.recycle();
        textureReady = true;
    }

    public ImageButton3D(CardboardUIActivity context, Bitmap bitmap) {
        super(context);
        this.setBitmap(bitmap);
        measure();
        this.valid = false;
    }

    @Override
    public void measure() {
        setWidth(bitmap.getWidth() / bitmap.getHeight());
        setHeight(1.0f);
    }

    public void setBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(output);
        Paint p = new Paint();

        Rect r = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rect = new RectF(r);
        p.setAntiAlias(true);

        c.drawARGB(0, 0, 0, 0);
        p.setColor(Color.BLACK);
        c.drawRoundRect(rect, 10, 10, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, r, r, p);

        this.bitmap = output;
        textureReady = false;
    }
}
