package com.karacasoft.cardboardui.test_activities;

import android.os.Bundle;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.R;
import com.karacasoft.cardboardui.ViewContent;
import com.karacasoft.cardboardui.view.Button3D;
import com.karacasoft.cardboardui.view.TextView3D;

public class MainActivity extends CardboardUIActivity {

    private TextView3D txtDeneme;
    private Button3D btnTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtDeneme = new TextView3D(this, "Test deneme");

        btnTest = new Button3D(this, "Button test");
        btnTest.translate(0, -1, 0);

        ViewContent content = new ViewContent(this);
        content.addView(txtDeneme);
        content.addView(btnTest);
        setCurrentContent(content);
    }



}
