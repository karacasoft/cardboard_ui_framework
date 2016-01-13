package com.karacasoft.cardboardui;

import android.util.Log;

import com.google.vrtoolkit.cardboard.Eye;
import com.karacasoft.cardboardui.view.TextView3D;
import com.karacasoft.cardboardui.view.View3D;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Karaca on 5/27/2015.
 */
public class ViewContent {

    private String name;
    private boolean showTitle = false;

    private ArrayList<View3D> views = new ArrayList<>();
    private ArrayList<View3D> addQueue = new ArrayList<>();
    private ArrayList<View3D> removeQueue = new ArrayList<>();

    private CardboardUIActivity context;

    private TextView3D titleText;

    public ViewContent(CardboardUIActivity context)
    {
        this.context = context;
    }

    public void draw(Eye eye)
    {
        if(showTitle) {
            drawTitle(eye);
        }
        drawContent(eye);
    }



    public void update()
    {
        if(showTitle) {
            if (titleText != null) {
                titleText.update();
            }
        }
        for (View3D v : views)
        {
            v.update();
        }
    }

    private void drawTitle(Eye eye)
    {
        if(titleText == null) {
            titleText = new TextView3D(context, name);
            titleText.measure();
            titleText.scale(0.4f, 0.4f, 0.4f);
            titleText.translate(-titleText.getWidth() / titleText.getHeight() / 2, 3.0f, 0.0f);
        }
        titleText.draw(eye);
    }

    private void drawContent(Eye eye)
    {
        for(View3D v : views)
        {
            if(v.isVisible()) {
                v.draw(eye);
            }
        }
        Iterator<View3D> it = addQueue.iterator();
        while(it.hasNext())
        {
            views.add(it.next());
            it.remove();
        }
        it = removeQueue.iterator();
        while(it.hasNext())
        {
            View3D v = it.next();
            if(views.contains(v))
            {
                views.remove(v);
            } else {
                Log.w("ViewContent", "View remove failed. No such view found.");
            }
            it.remove();
        }
    }

    protected void onContentSelected() {}

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void addView(View3D v)
    {
        addQueue.add(v);
    }

    public void removeView(View3D v)
    {
        removeQueue.add(v);
    }

    public String getName() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public ArrayList<View3D> getViews() {
        return views;
    }

    public CardboardUIActivity getContext() {
        return context;
    }
}
