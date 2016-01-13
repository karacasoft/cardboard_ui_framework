package com.karacasoft.cardboardui.view.adapter;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.view.AdapterView3D;
import com.karacasoft.cardboardui.view.View3D;

import java.util.ArrayList;

/**
 * <p>Base AdapterView Adapter class.</p>
 *
 * <p>Subclasses must implement {@link BaseAdapter3D#getView(int)} method.
 * You may want to include your onTrigger callbacks in your implementation.</p>
 *
 * <p>Check {@link com.karacasoft.cardboardui.test_activities.SampleListAdapter} example.</p>
 *
 * Created by Karaca on 5/28/2015.
 */
public abstract class BaseAdapter3D<T> {

    private ArrayList<T> items;

    private CardboardUIActivity context;

    private AdapterView3D viewContext;

    public BaseAdapter3D(CardboardUIActivity context)
    {
        this.context = context;
    }

    public T getItem(int position)
    {
        return items.get(position);
    }

    public int getCount()
    {
        if(items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public abstract View3D getView(int position);

    public void setItems(ArrayList<T> items) {
        this.items = items;
        if(viewContext != null) {
            viewContext.notifyDataSetChanged();
        }
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public CardboardUIActivity getContext() {
        return context;
    }

    public void setViewContext(AdapterView3D viewContext) {
        this.viewContext = viewContext;
    }
}
