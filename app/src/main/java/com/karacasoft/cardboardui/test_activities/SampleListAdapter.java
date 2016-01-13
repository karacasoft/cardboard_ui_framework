package com.karacasoft.cardboardui.test_activities;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.view.Button3D;
import com.karacasoft.cardboardui.view.View3D;
import com.karacasoft.cardboardui.view.adapter.BaseAdapter3D;

/**
 * Basic usage of BaseAdapter3D.
 *
 * Created by Karaca on 1/13/2016.
 */
public class SampleListAdapter extends BaseAdapter3D<String> {

    OnItemClickListener itemClickListener;

    public SampleListAdapter(CardboardUIActivity context) {
        super(context);
    }

    @Override
    public View3D getView(final int position) {
        Button3D btn = new Button3D(getContext(), getItem(position));
        btn.setOnTriggerListener(new View3D.OnTriggerListener() {
            @Override
            public void onTrigger(View3D v) {
                if(itemClickListener != null) itemClickListener.onClick(position);
            }
        });
        return btn;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        public void onClick(int position);
    }
}
