package com.ryanbrooks.expandablerecyclerviewsample.nested;


import java.util.List;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

/**
 * Custom parent object that holds a string and an int for displaying data in the parent item. You
 * can use any Object here as long as it implements ParentObject and sets the list to a private
 * variable.
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public class CustomParentObject implements ParentObject {

    // A List<Object> or subclass of List must be added for the object to work correctly
    private List<?> mChildObjectList;

    private String mParentText;
    private int mNestedDepth;

    public CustomParentObject() { }

    public CustomParentObject(String text, int depth) {
        mParentText = text;
        mNestedDepth = depth;
    }

    public String getParentText() {
        return mParentText;
    }

    public void setParentText(String parentText) {
        mParentText = parentText;
    }

    public int getDepth() {
        return mNestedDepth;
    }

    public void setDepth(int depth) {
        mNestedDepth = depth;
    }

    /**
     * Getter method for the list of children associated with this parent object
     *
     * @return list of all children associated with this specific parent object
     */
    @Override
    public List<?> getChildObjectList() {
        return mChildObjectList;
    }

    /**
     * Setter method for the list of children associated with this parent object
     *
     * @param childObjectList the list of all children associated with this parent object
     */
    @Override
    public void setChildObjectList(List<?> childObjectList) {
        mChildObjectList = childObjectList;
    }
}
