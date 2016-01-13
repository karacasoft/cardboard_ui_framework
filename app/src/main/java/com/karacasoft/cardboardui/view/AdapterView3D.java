package com.karacasoft.cardboardui.view;

import android.graphics.BitmapFactory;

import com.google.vrtoolkit.cardboard.Eye;
import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.R;
import com.karacasoft.cardboardui.view.adapter.BaseAdapter3D;

/**
 * Very simple AdapterView implementation.
 *
 * <p>Can be used to display lists. An implementation of {@link BaseAdapter3D} is required.</p>
 *
 * <p>List display size can be increased/decreased by using
 * {@link AdapterView3D#setMaxItemsShown(int)}.</p>
 *
 * <p>Here's a simple AdapterView3D usage:</p>
 * <code>
 *      adView = new AdapterView3D(this);<br  />
 *      <br  />
 *      //centralizing the view<br  />
 *      adView.translate(-2.0f, -4.0f, 0.0f);<br  />
 *      adView.setWidth(4.0f);<br  />
 *      <br  />
 *      //This is a subclass of BaseAdapter3D. See BaseAdapter3D documentation.<br />
 *      final ListAdapter adapter = new ListAdapter(this);<br  />
 *      adapter.setItems(items);<br  />
 *      <br  />
 *      adView.setAdapter(adapter);<br  />
 * </code>
 *
 * Created by Karaca on 5/28/2015.
 *
 * @see BaseAdapter3D
 */
public class AdapterView3D extends View3D {

    private BaseAdapter3D adapter;

    private ImageButton3D upButton;
    private ImageButton3D downButton;

    private int maxItemsShown = 3;
    private boolean showUpButton = true;
    private boolean showDownButton = true;

    protected View3D[] views;

    private float itemHeight = 1.0f;
    private float itemWidth = 2.0f;

    private float verticalSlideOffset = 0f;

    private boolean invalidateFlag = false;

    public AdapterView3D(CardboardUIActivity context) {
        super(context);
        measure();
    }

    public View3D requestView(int position)
    {
        if(views == null) views = new View3D[adapter.getCount()];
        if(position >= views.length)
        {
            return null;
        }
        if(views[position] == null)
        {
            views[position] = adapter.getView(position);
            views[position].scale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            if(views[position].getWidth() != this.getWidth())
            {
                views[position].setWidth(this.getWidth());
            }
            if(views[position].getHeight() != itemHeight)
            {
                views[position].setHeight(itemHeight);
            }
            return views[position];
        }else{
            return views[position];
        }
    }

    protected boolean isViewVisible(int position)
    {
        View3D v;
        if(position >= views.length) return false;
        if((v = views[position]) != null)
        {
            float bottom = (this.getY() + this.getHeight()) * this.getScaleY() - ((position - 1) * itemHeight) - verticalSlideOffset;
            float top = bottom + v.getHeight() * v.getScaleY();

            if(top > (this.getY() + itemHeight) * this.getScaleY() && bottom < (this.getY() + this.getHeight() - itemHeight) * this.getScaleY())
            {
                return true;
            }
        }
        return false;
    }

    protected void updateViewPosition(int position)
    {
        View3D v;
        if(position >= views.length) return;
        if((v = views[position]) != null)
        {
            float actualX = v.getX() * v.getScaleX();
            float actualY = v.getY() * v.getScaleY();

            float viewX = this.getX() * this.getScaleX();
            float viewY = (this.getY() + this.getHeight()) * this.getScaleY() - ((position - 1) * itemHeight) - verticalSlideOffset;

            if(Math.abs(actualX - viewX) > 0.001f || Math.abs(actualY - viewY) > 0.001f)
            {
                v.translate(viewX - actualX, viewY - actualY, 0.0f);
            }
        }
    }

    protected void drawUp(Eye eye)
    {
        if(upButton == null)
        {
            upButton = new ImageButton3D(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_up));
            upButton.setWidth(this.getWidth());
            upButton.scale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            upButton.translate(this.getX() * this.getScaleX(), (this.getY() + this.getHeight() - itemHeight) * this.getScaleY(), 0.1f);
            upButton.setOnLookAtListener(new OnLookAtListener() {
                @Override
                public void onLookAt(View3D v, float x, float y) {
                    verticalSlideOffset += (y - (v.getY() * v.getScaleY())) / 10f;

                }

                @Override
                public void onHoverExit(View3D v) {}
            });
            upButton.setFocusModeFocusable(false);
        }
        upButton.draw(eye);
    }

    protected void drawDown(Eye eye)
    {
        if(downButton == null)
        {
            downButton = new ImageButton3D(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_down));
            downButton.setWidth(this.getWidth());
            downButton.scale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            downButton.translate(this.getX() * this.getScaleX(), this.getY() * this.getScaleY(), 0.1f);
            downButton.setOnLookAtListener(new OnLookAtListener() {
                @Override
                public void onLookAt(View3D v, float x, float y) {
                    verticalSlideOffset -= ((v.getHeight() + v.getY()) * v.getScaleY() - y) / 10f;
                }

                @Override
                public void onHoverExit(View3D v) {
                }
            });
            downButton.setFocusModeFocusable(false);
        }
        downButton.draw(eye);
    }

    @Override
    public void draw(Eye eye) {

        if(showUpButton) {
            drawUp(eye);
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            requestView(i);
            if(isViewVisible(i)) {
                updateViewPosition(i);
                View3D v = requestView(i);
                if(v != null) {
                    v.draw(eye);
                }
            }
        }

        if(showDownButton) {
            drawDown(eye);
        }

        if(invalidateFlag)
        {
            this.views = null;
            invalidateFlag = false;
        }
    }

    @Override
    public void update() {
        super.update();
        if(views != null) {
            for (View3D v : views) {
                if (v != null) {
                    v.update();
                }
            }
        }
        if(showUpButton) {
            if (upButton != null) upButton.update();
        }
        if(showDownButton) {
            if (downButton != null) downButton.update();
        }
    }

    @Override
    public void measure() {
        setWidth(itemWidth);
        setHeight(itemHeight * (maxItemsShown + 2));
    }

    @Override
    public void performTriggerIfLookingAt() {
        super.performTriggerIfLookingAt();
        for(View3D v : views)
        {
            v.performTriggerIfLookingAt();
        }
    }

    public void notifyDataSetChanged()
    {
        invalidateFlag = true;
    }

    public void setShowUpButton(boolean showUpButton) {
        this.showUpButton = showUpButton;
    }

    public void setShowDownButton(boolean showDownButton) {
        this.showDownButton = showDownButton;
    }

    public void setMaxItemsShown(int maxItemsShown) {
        this.maxItemsShown = maxItemsShown;
    }

    public void setAdapter(BaseAdapter3D adapter) {
        this.adapter = adapter;
        adapter.setViewContext(this);
    }

    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setVerticalScroll(float verticalSlideOffset) {
        this.verticalSlideOffset = verticalSlideOffset;
    }
}
