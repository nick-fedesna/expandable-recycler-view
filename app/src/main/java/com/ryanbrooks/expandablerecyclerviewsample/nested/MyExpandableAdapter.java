package com.ryanbrooks.expandablerecyclerviewsample.nested;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.*;

import java.util.List;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ClickListeners.ExpandCollapseListener;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.ryanbrooks.expandablerecyclerviewsample.R;

/**
 * An example custom implementation of the ExpandableRecyclerAdapter.
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public class MyExpandableAdapter extends ExpandableRecyclerAdapter<CustomParentViewHolder, CustomChildViewHolder>
        implements ExpandCollapseListener {

    private LayoutInflater mInflater;

    private boolean mSingleParentExpanded;

    private float mParentTextSize;
    private float mNestedTextSize;

    /**
     * Public primary constructor.
     *
     * @param context        for inflating views
     * @param parentItemList the list of parent items to be displayed in the RecyclerView
     */
    public MyExpandableAdapter(Context context, List<ParentObject> parentItemList) {
        super(parentItemList);
        init(context);
    }

    /**
     * Public secondary constructor. This constructor adds the ability to add a custom triggering
     * view when the adapter is created without having to set it later. This is here for demo
     * purposes.
     *
     * @param context               for inflating views
     * @param parentItemList        the list of parent items to be displayed in the RecyclerView
     * @param customClickableViewId the id of the view that triggers the expansion
     */
    public MyExpandableAdapter(Context context, List<ParentObject> parentItemList,
                               int customClickableViewId) {
        super(parentItemList, customClickableViewId);
        init(context);
    }

    /**
     * Public secondary constructor. This constructor adds the ability to add a custom triggering
     * view and a custom animation duration when the adapter is created without having to set them
     * later. This is here for demo purposes.
     *
     * @param context               for inflating views
     * @param parentItemList        the list of parent items to be displayed in the RecyclerView
     * @param customClickableViewId the id of the view that triggers the expansion
     * @param animationDuration     the duration (in ms) of the rotation animation
     */
    public MyExpandableAdapter(Context context, List<ParentObject> parentItemList,
                               int customClickableViewId, long animationDuration) {
        super(parentItemList, customClickableViewId, animationDuration);
        init(context);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(context);
        addExpandCollapseListener(this);
        Resources res = context.getResources();
        mParentTextSize = res.getDimension(R.dimen.parent_text_size);
        mNestedTextSize = res.getDimension(R.dimen.nested_parent_text_size);
    }

    public void expandFirstParent() {
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i) instanceof CustomParentObject) {
                expandParent((ParentObject) mItemList.get(i), i);
                break;
            }
        }
    }

    public void setSingleParentExpanded(boolean singleParentExpanded) {
        this.mSingleParentExpanded = singleParentExpanded;

        // we will collapse all top-level parents when this setting is enabled
        //  (sub-parents will be collapsed automatically by parent class)
        //
        // NOTE: always walk backwards through items since indexes may change after collapsing an item
        if (singleParentExpanded) {
            for (int i = mItemList.size() - 1; i >= 0; i--) {
                if (mItemList.get(i) instanceof CustomParentObject) {
                    CustomParentObject parent = (CustomParentObject) mItemList.get(i);
                    if (parent.getDepth() == 1) {
                        collapseParent(parent, i);
                    }
                }
            }
        }
    }

    @Override
    public void onRecyclerViewItemExpanded(ParentObject parent, int position) {
        if (mSingleParentExpanded) {
            for (int i = mItemList.size() - 1; i >= 0; i--) {
                Object item = mItemList.get(i);
                if (item instanceof CustomParentObject && item != parent) {
                    CustomParentObject category = (CustomParentObject) item;
                    // nested categories will be collapsed automatically if parent is collapsed
                    // so we only collapse if we are at the same depth of nesting
                    // this also prevents collapsing our parent if we are a child
                    if (category.getDepth() == ((CustomParentObject) parent).getDepth())
                        collapseParent(category, i);
                }
            }
        }
    }

    @Override
    public void onRecyclerViewItemCollapsed(ParentObject parent, int position) {
        // we don't need to do anything for this sample
    }

    /**
     * OnCreateViewHolder implementation for parent items. The desired ParentViewHolder should
     * be inflated here
     *
     * @param parent for inflating the View
     * @return the user's custom parent ViewHolder that must extend ParentViewHolder
     */
    @Override
    public CustomParentViewHolder onCreateParentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.recycler_item_layout_parent, parent, false);
        return new CustomParentViewHolder(view);
    }

    /**
     * OnCreateViewHolder implementation for child items. The desired ChildViewHolder should
     * be inflated here
     *
     * @param parent for inflating the View
     * @return the user's custom parent ViewHolder that must extend ParentViewHolder
     */
    @Override
    public CustomChildViewHolder onCreateChildViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.recycler_item_layout_child, parent, false);
        return new CustomChildViewHolder(view);
    }

    /**
     * OnBindViewHolder implementation for parent items. Any data or view modifications of the
     * parent view should be performed here.
     *
     * @param parentViewHolder the ViewHolder of the parent item created in OnCreateParentViewHolder
     * @param position         the position in the RecyclerView of the item
     */
    @Override
    public void onBindParentViewHolder(CustomParentViewHolder parentViewHolder, int position, Object parentObject) {
        CustomParentObject customParentObject = (CustomParentObject) parentObject;
        parentViewHolder.dataText.setText(customParentObject.getParentText());
        float textSize = customParentObject.getDepth() > 1 ? mNestedTextSize : mParentTextSize;
        parentViewHolder.dataText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * OnBindViewHolder implementation for child items. Any data or view modifications of the
     * child view should be performed here.
     *
     * @param childViewHolder the ViewHolder of the child item created in OnCreateChildViewHolder
     * @param position        the position in the RecyclerView of the item
     */
    @Override
    public void onBindChildViewHolder(CustomChildViewHolder childViewHolder, int position, Object childObject) {
        CustomChildObject customChildObject = (CustomChildObject) childObject;
        childViewHolder.dataText.setText(customChildObject.getChildText());
    }
}