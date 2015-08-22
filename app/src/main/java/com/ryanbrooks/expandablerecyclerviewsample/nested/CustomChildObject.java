package com.ryanbrooks.expandablerecyclerviewsample.nested;

/**
 * /**
 * Custom child object. This is for demo purposes, although it is recommended having a separate
 * child object from your parent object.
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public class CustomChildObject {
    private String mChildText;

    public CustomChildObject(String text) {
        mChildText = text;
    }

    public String getChildText() {
        return mChildText;
    }

    public void setChildText(String childText) {
        mChildText = childText;
    }
}
