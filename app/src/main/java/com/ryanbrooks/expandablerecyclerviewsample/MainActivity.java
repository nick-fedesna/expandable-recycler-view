package com.ryanbrooks.expandablerecyclerviewsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.*;
import com.ryanbrooks.expandablerecyclerviewsample.nested.NestedRecyclerViewSample;
import com.ryanbrooks.expandablerecyclerviewsample.VerticalLinearRecyclerViewSample.VerticalLinearRecyclerViewSample;

/**
 * Main Activity that contains navigation for sample application.
 * Uses ButterKnife to inject view resources.
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.activity_main_toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.main_vertical_linear_button)
    public void onVerticalSampleClicked() {
        startActivity(new Intent(this, VerticalLinearRecyclerViewSample.class));
    }

    @OnClick(R.id.main_nested_button)
    public void onNestedSampleClicked() {
        startActivity(new Intent(this, NestedRecyclerViewSample.class));
    }

    @OnClick({R.id.main_horizontal_linear_button, R.id.main_grid_button})
    public void onSampleComingSoonClicked() {
        Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
    }
}
