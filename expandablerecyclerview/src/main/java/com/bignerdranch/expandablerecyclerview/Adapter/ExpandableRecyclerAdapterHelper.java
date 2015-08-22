package com.bignerdranch.expandablerecyclerview.Adapter;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Brooks on 6/11/15.
 */
public class ExpandableRecyclerAdapterHelper {

    private final List<Object> mHelperItemList = new ArrayList<>();

    private static int sCurrentId;

    public ExpandableRecyclerAdapterHelper(List<Object> itemList) {
        generateHelperItemList(itemList);
    }

    public List<Object> getHelperItemList() {
        return mHelperItemList;
    }

    public Object getHelperItemAtPosition(int position) {
        if (mHelperItemList.get(position) instanceof ParentWrapper) {
            return mHelperItemList.get(position);
        } else {
            return mHelperItemList.get(position);
        }
    }

    private void generateHelperItemList(List<Object> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            addItem(i, itemList.get(i));
        }
    }

    public void addItem(int position, Object item) {
        if (item instanceof ParentObject) {
            ParentWrapper parentWrapper = new ParentWrapper((ParentObject) item);
            mHelperItemList.add(position, parentWrapper);
        } else {
            mHelperItemList.add(position, item);
        }
    }
}
