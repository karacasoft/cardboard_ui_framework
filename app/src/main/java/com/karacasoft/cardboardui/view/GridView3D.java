package com.karacasoft.cardboardui.view;

import com.google.vrtoolkit.cardboard.Eye;
import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.view.adapter.BaseAdapter3D;

/**
 * A subclass of AdapterView3D that shows View3D items in a grid.
 *
 * Created by Karaca on 6/8/2015.
 */
public class GridView3D extends AdapterView3D {
    public static final int FIT_METHOD_SCALE = 1;
    public static final int FIT_METHOD_CHANGE_WIDTH_HEIGHT = 2;

    private BaseAdapter3D adapter;

    private float itemWidth = 1.0f;
    private float itemHeight = 1.0f;

    private int fitMethod = FIT_METHOD_SCALE;

    private int horizontalTiles = 4;
    private int verticalTiles = 3;

    private Button3D leftButton;
    private Button3D rightButton;

    private float horizontalSlideOffset = 0;


    public GridView3D(CardboardUIActivity context) {
        super(context);
        measure();
    }

    private void drawLeft(Eye eye)
    {
        if(leftButton == null)
        {
            leftButton = new Button3D(getContext(), "<");

            leftButton.setWidth(itemWidth);
            leftButton.setHeight(itemHeight * verticalTiles);
            leftButton.invalidate();

            leftButton.scale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            leftButton.translate(this.getX() * this.getScaleX() - leftButton.getWidth() * leftButton.getScaleX(), -itemHeight * (verticalTiles - 1), 0.1f);
            leftButton.setOnLookAtListener(new View3D.OnLookAtListener() {
                @Override
                public void onLookAt(View3D v, float x, float y) {
                    slideHorizontal(0.03f);
                }

                @Override
                public void onHoverExit(View3D v) {}
            });
            leftButton.setFocusModeFocusable(false);
        }
        leftButton.draw(eye);
    }

    private void drawRight(Eye eye)
    {
        if(rightButton == null)
        {
            rightButton = new Button3D(getContext(), ">");

            rightButton.setWidth(itemWidth);
            rightButton.setHeight(itemHeight * verticalTiles);
            rightButton.invalidate();

            rightButton.scale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            rightButton.translate(this.getX() * this.getScaleX() + this.getWidth() * this.getScaleX(), -itemHeight * (verticalTiles - 1), 0.1f);
            rightButton.setOnLookAtListener(new View3D.OnLookAtListener() {
                @Override
                public void onLookAt(View3D v, float x, float y) {
                    slideHorizontal(-0.03f);
                }

                @Override
                public void onHoverExit(View3D v) {
                }
            });
            rightButton.setFocusModeFocusable(false);
        }
        rightButton.draw(eye);
    }

    @Override
    public View3D requestView(int position)
    {
        if(views[position] != null)
        {
            return views[position];
        }
        View3D v = adapter.getView(position);

        if(fitMethod == FIT_METHOD_CHANGE_WIDTH_HEIGHT)
        {
            v.setWidth(itemWidth);
            v.setHeight(itemHeight);
        }else if(fitMethod == FIT_METHOD_SCALE)
        {
            if(v.getWidth() != itemWidth)
            {
                v.scale(itemWidth / v.getWidth(), itemWidth / v.getWidth(), itemWidth / v.getWidth());
                if(v.getHeight() > itemHeight)
                {
                    v.scale(itemHeight / v.getHeight(), itemHeight / v.getHeight(), itemHeight / v.getHeight());
                    v.setWidth(itemWidth);
                }else{
                    v.setHeight(itemHeight);
                }
            }
        }
        views[position] = v;
        return v;
    }

    @Override
    protected boolean isViewVisible(int position)
    {
        float viewLeftPos = (this.getX() - horizontalSlideOffset) * this.getScaleX() + (position / verticalTiles) * itemWidth;
        float viewRightPos = viewLeftPos + requestView(position).getWidth() * requestView(position).getScaleX();

        if(viewLeftPos < (this.getX() + this.getWidth()) * this.getScaleX() &&
                viewRightPos > (this.getX()) * this.getScaleX())
        {
            return true;
        }
        return false;
    }

    @Override
    protected void updateViewPosition(int position)
    {
        View3D v = requestView(position);

        float actualX = v.getX() * v.getScaleX();
        float actualY = v.getY() * v.getScaleY();

        float viewX = (this.getX() - horizontalSlideOffset) * this.getScaleX() + (position / verticalTiles) * itemWidth;
        float viewY = this.getY() * this.getScaleY() - (position % verticalTiles) * itemHeight;

        if(Math.abs(viewX - actualX) > 0.001f || Math.abs(viewY - actualY) > 0.001f)
        {
            v.translate(viewX - actualX, viewY - actualY, 0);
        }
    }

    @Override
    public void update() {
        super.update();
        if(leftButton != null) leftButton.update();
        if(rightButton != null) rightButton.update();
        for(int i = 0; i < adapter.getCount(); i++)
        {
            requestView(i).update();
        }
    }

    @Override
    public void draw(Eye eye) {
        drawLeft(eye);
        drawRight(eye);
        for(int i = 0; i < adapter.getCount(); i++)
        {
            if(isViewVisible(i)) {
                updateViewPosition(i);
                requestView(i).draw(eye);
            }
        }
    }

    @Override
    public void scale(float x, float y, float z) {
        super.scale(x, y, z);
//        itemWidth *= x;
//        itemHeight *= y;
    }

    @Override
    public void measure() {
        setWidth(itemWidth * horizontalTiles);
        setHeight(itemHeight * verticalTiles);
    }

    @Override
    public void performTriggerIfLookingAt() {
        super.performTriggerIfLookingAt();
        for(View3D v : views)
        {
            v.performTriggerIfLookingAt();
        }
    }

    public void notifyViewSetChanged()
    {
        this.views = new View3D[this.adapter.getCount()];
    }

    @Override
    public void setAdapter(BaseAdapter3D adapter) {
        this.adapter = adapter;
        this.views = new View3D[this.adapter.getCount()];
    }

    public void setItemWidth(float itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setHorizontalTiles(int horizontalTiles) {
        this.horizontalTiles = horizontalTiles;
    }

    public void setVerticalTiles(int verticalTiles) {
        this.verticalTiles = verticalTiles;
    }

    public void slideHorizontal(float amount)
    {
        horizontalSlideOffset -= amount;
    }
}