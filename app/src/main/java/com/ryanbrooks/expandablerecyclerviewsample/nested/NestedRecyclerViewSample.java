package com.ryanbrooks.expandablerecyclerviewsample.nested;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;

import java.util.ArrayList;

import butterknife.*;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.ryanbrooks.expandablerecyclerviewsample.R;

/**
 * Sample Activity for the vertical linear RecyclerView sample.
 * Uses ButterKnife to inject view resources.
 *
 * @author Ryan Brooks, Nick Fedesna
 * @version 1.0
 * @since 5/27/2015
 */
public class NestedRecyclerViewSample extends AppCompatActivity {

    @InjectView(R.id.nested_sample_toolbar)          Toolbar      mToolbar;
    @InjectView(R.id.nested_recyclerview)            RecyclerView mRecyclerView;

    private MyExpandableAdapter mExpandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_recyclerview_sample);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create a new adapter with 20 test data items
        mExpandableAdapter = new MyExpandableAdapter(this, setUpTestData(20));

        // Set the RecyclerView's adapter to the ExpandableAdapter we just created
        mRecyclerView.setAdapter(mExpandableAdapter);
        // Set the layout manager to a LinearLayout manager for vertical list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Demonstrate ability to have a parent expanded on creation.
        mExpandableAdapter.expandFirstParent();
    }

    /**
     * Save the instance state of the adapter to keep expanded/collapsed states when rotating or
     * pausing the activity.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mExpandableAdapter.onSaveInstanceState(outState);
    }

    /**
     * Load the expanded/collapsed states of the adapter back into the view when done rotating or
     * resuming the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mExpandableAdapter.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Check changed listener for the single parent expanded checkbox.
     *
     * @param isChecked
     */
    @OnCheckedChanged(R.id.nested_sample_toolbar_checkbox)
    void onCheckChanged(boolean isChecked) {
        mExpandableAdapter.setSingleParentExpanded(isChecked);
    }

    /**
     * Method to set up test data used in the RecyclerView.
     *
     *
     * @param numItems
     * @return an ArrayList of Objects that contains all parent items. Expansion of children are handled in the adapter
     */
    private ArrayList<ParentObject> setUpTestData(int numItems) {
        ArrayList<ParentObject> parentObjectList = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            ArrayList<CustomParentObject> nestedParentList = new ArrayList<>();

            // Every parent gets a few nested-parents
            // (we set their depth to 2 in order to facilitate the single expanded parent feature)
            for (int j = 0; j < 3; j++) {
                CustomParentObject nestedParent = new CustomParentObject(String.format("Sub-Category %d", j + 1), 2);
                nestedParentList.add(nestedParent);

                // Every nested parent gets some children
                ArrayList<CustomChildObject> children = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    children.add(new CustomChildObject(String.format("Item %d", k + 1)));
                }

                nestedParent.setChildObjectList(children);
            }

            CustomParentObject customParentObject = new CustomParentObject(String.format("Category %d", i + 1), 1);
            customParentObject.setChildObjectList(nestedParentList);
            parentObjectList.add(customParentObject);
        }
        return parentObjectList;
    }
}
