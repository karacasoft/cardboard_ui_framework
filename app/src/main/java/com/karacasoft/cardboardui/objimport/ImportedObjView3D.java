package com.karacasoft.cardboardui.objimport;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.view.View3D;

/**
 * Experimental.
 *
 * Created by Karaca on 5/24/2015.
 */
public class ImportedObjView3D extends View3D {

    public ImportedObjView3D(CardboardUIActivity context)
    {
        super(context);
    }

    @Override
    public void update() {
        valid = true;
    }

    @Override
    public void measure() {}
}
