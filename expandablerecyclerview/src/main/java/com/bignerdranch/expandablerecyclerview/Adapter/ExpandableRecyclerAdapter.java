package com.bignerdranch.expandablerecyclerview.Adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.bignerdranch.expandablerecyclerview.ClickListeners.ExpandCollapseListener;
import com.bignerdranch.expandablerecyclerview.ClickListeners.ParentItemClickListener;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

/**
 * The Base class for an Expandable RecyclerView Adapter
 * <p/>
 * Provides the base for a user to implement binding custom views to a Parent ViewHolder and a
 * Child ViewHolder
 *
 * @author Ryan Brooks
 * @version 1.0
 * @since 5/27/2015
 */
public abstract class ExpandableRecyclerAdapter<PVH extends ParentViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ParentItemClickListener {

    static final String TAG = ExpandableRecyclerAdapter.class.getClass().getSimpleName();

    public static final int  CUSTOM_ANIMATION_VIEW_NOT_SET     = -1;
    public static final long DEFAULT_ROTATE_DURATION_MS        = 200l;
    public static final long CUSTOM_ANIMATION_DURATION_NOT_SET = -1l;

    private static final String EXPANDED_POSITION_LIST = "ExpandableRecyclerAdapter.ExpandedPositionList";
    private static final int    TYPE_PARENT            = 0;
    private static final int    TYPE_CHILD             = 1;

    protected List<Object>       mItemList;
    protected List<ParentObject> mParentItemList;

    private ExpandableRecyclerAdapterHelper mHelper;
    private ExpandCollapseListener          mListener;

    private boolean mParentAndIconClickable      = false;
    private int     mCustomParentAnimationViewId = CUSTOM_ANIMATION_VIEW_NOT_SET;
    private long    mAnimationDuration           = CUSTOM_ANIMATION_DURATION_NOT_SET;

    /**
     * Public default constructor for the base ExpandableRecyclerView. It is expected you will setup
     * the list after construction by calling {@link #setupList(List)}. Also, if you do not call
     * {@link #setupAnimation(int, long)} a click of the parent item will trigger the expansion.
     */
    public ExpandableRecyclerAdapter() { }

    /**
     * Public constructor for the base ExpandableRecyclerView. This constructor takes in no
     * extra parameters for custom clickable views and animation durations. This means a click of
     * the parent item will trigger the expansion.
     *
     * @param parentItemList
     */
    public ExpandableRecyclerAdapter(List<ParentObject> parentItemList) {
        setupList(parentItemList);
    }

    /**
     * Public constructor for a more robust ExpandableRecyclerView. This constructor takes in an
     * id for a custom clickable view that will trigger the expansion or collapsing of the child.
     * By default, a parent item click is the trigger for the expanding/collapsing.
     *
     * @param parentItemList
     * @param customParentAnimationViewId
     */
    public ExpandableRecyclerAdapter(List<ParentObject> parentItemList,
                                     int customParentAnimationViewId) {
        setupList(parentItemList);
        setupAnimation(customParentAnimationViewId, CUSTOM_ANIMATION_DURATION_NOT_SET);
    }

    /**
     * Public constructor for even more robust ExpandableRecyclerView. This constructor takes in
     * both an id for a custom clickable view that will trigger the expansion or collapsing of the
     * child along with a long for a custom duration in MS for the rotation animation.
     *
     * @param parentItemList
     * @param customParentAnimationViewId
     * @param animationDuration
     */
    public ExpandableRecyclerAdapter(List<ParentObject> parentItemList,
                                     int customParentAnimationViewId,
                                     long animationDuration) {
        setupList(parentItemList);
        setupAnimation(customParentAnimationViewId, animationDuration);
    }

    /**
     * Allows for setting up the list after construction of this adapter.
     *
     * @param parentItemList
     */
    public void setupList(List<ParentObject> parentItemList) {
        mParentItemList = parentItemList;
        mItemList = generateObjectList(parentItemList);
        mHelper = new ExpandableRecyclerAdapterHelper(mItemList);
    }

    /**
     * Allows for setting the animation view and duration after construction of this adapter.
     *
     * @param customParentAnimationViewId
     * @param animationDuration
     */
    public void setupAnimation(int customParentAnimationViewId, long animationDuration) {
        mCustomParentAnimationViewId = customParentAnimationViewId;
        mAnimationDuration = animationDuration;
    }

    /**
     * Override of RecyclerView's default onCreateViewHolder.
     * <p/>
     * This implementation determines if the item is a child or a parent view and will then call
     * the respective onCreateViewHolder method that the user must implement in their custom
     * implementation.
     *
     * @param viewGroup
     * @param viewType
     * @return the ViewHolder that corresponds to the item at the position.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_PARENT) {
            PVH pvh = onCreateParentViewHolder(viewGroup);
            pvh.setParentItemClickListener(this);
            return pvh;
        } else if (viewType == TYPE_CHILD) {
            return onCreateChildViewHolder(viewGroup);
        } else {
            throw new IllegalStateException("Incorrect ViewType found");
        }
    }

    /**
     * Override of RecyclerView's default onBindViewHolder
     * <p/>
     * This implementation determines first if the ViewHolder is a ParentViewHolder or a
     * ChildViewHolder. The respective onBindViewHolders for ParentObjects and ChildObject are then
     * called.
     * <p/>
     * If the item is a ParentObject, setting the ParentViewHolder's animation settings are then handled
     * here.
     *
     * @param holder
     * @param position
     * @throws IllegalStateException if the item in the list is neither a ParentObject or ChildObject
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mHelper.getHelperItemAtPosition(position) instanceof ParentWrapper) {
            PVH parentViewHolder = (PVH) holder;

            if (mParentAndIconClickable) {
                if (mCustomParentAnimationViewId != CUSTOM_ANIMATION_VIEW_NOT_SET
                        && mAnimationDuration != CUSTOM_ANIMATION_DURATION_NOT_SET) {
                    parentViewHolder.setCustomClickableViewAndItem(mCustomParentAnimationViewId);
                    parentViewHolder.setAnimationDuration(mAnimationDuration);
                } else if (mCustomParentAnimationViewId != CUSTOM_ANIMATION_VIEW_NOT_SET) {
                    parentViewHolder.setCustomClickableViewAndItem(mCustomParentAnimationViewId);
                    parentViewHolder.cancelAnimation();
                } else {
                    parentViewHolder.setMainItemClickToExpand();
                }
            } else {
                if (mCustomParentAnimationViewId != CUSTOM_ANIMATION_VIEW_NOT_SET
                        && mAnimationDuration != CUSTOM_ANIMATION_DURATION_NOT_SET) {
                    parentViewHolder.setCustomClickableViewOnly(mCustomParentAnimationViewId);
                    parentViewHolder.setAnimationDuration(mAnimationDuration);
                } else if (mCustomParentAnimationViewId != CUSTOM_ANIMATION_VIEW_NOT_SET) {
                    parentViewHolder.setCustomClickableViewOnly(mCustomParentAnimationViewId);
                    parentViewHolder.cancelAnimation();
                } else {
                    parentViewHolder.setMainItemClickToExpand();
                }
            }

            parentViewHolder.setExpanded(((ParentWrapper) mHelper.getHelperItemAtPosition(position)).isExpanded());
            onBindParentViewHolder(parentViewHolder, position, mItemList.get(position));
        } else if (mItemList.get(position) == null) {
            throw new IllegalStateException("Incorrect ViewHolder found");
        } else {
            onBindChildViewHolder((CVH) holder, position, mItemList.get(position));
        }
    }

    /**
     * Creates the Parent ViewHolder. Called from onCreateViewHolder when the item is a ParenObject.
     *
     * @param parentViewGroup
     * @return ParentViewHolder that the user must create and inflate.
     */
    public abstract PVH onCreateParentViewHolder(ViewGroup parentViewGroup);

    /**
     * Creates the Child ViewHolder. Called from onCreateViewHolder when the item is a ChildObject.
     *
     * @param childViewGroup
     * @return ChildViewHolder that the user must create and inflate.
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup childViewGroup);

    /**
     * Binds the data to the ParentViewHolder. Called from onBindViewHolder when the item is a
     * ParentObject
     *
     * @param parentViewHolder
     * @param position
     */
    public abstract void onBindParentViewHolder(PVH parentViewHolder, int position, Object parentObject);

    /**
     * Binds the data to the ChildViewHolder. Called from onBindViewHolder when the item is a
     * ChildObject
     *
     * @param childViewHolder
     * @param position
     */
    public abstract void onBindChildViewHolder(CVH childViewHolder, int position, Object childObject);

    /**
     * Returns the size of the list that contains Parent and Child objects
     *
     * @return integer value of the size of the Parent/Child list
     */
    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    /**
     * Returns the type of view that the item at the given position is.
     *
     * @param position
     * @return TYPE_PARENT (0) for ParentObjects and TYPE_CHILD (1) for ChildObjects
     * @throws IllegalStateException if the item at the given position in the list is null
     */
    @Override
    public int getItemViewType(int position) {
        if (mItemList.get(position) instanceof ParentObject) {
            return TYPE_PARENT;
        } else if (mItemList.get(position) == null) {
            throw new IllegalStateException("Null object added");
        } else {
            return TYPE_CHILD;
        }
    }

    /**
     * On click listener implementation for the ParentObject. This is called from ParentViewHolder.
     * See OnClick in ParentViewHolder
     *
     * @param position
     */
    @Override
    public void onParentItemClickListener(int position) {
        if (mItemList.get(position) instanceof ParentObject) {
            ParentObject parentObject = (ParentObject) mItemList.get(position);
            toggleParentExpansion(parentObject, position);
        }
    }

    /**
     * Setter for the Default rotation duration (200 MS)
     */
    public void setParentClickableViewAnimationDefaultDuration() {
        mAnimationDuration = DEFAULT_ROTATE_DURATION_MS;
    }

    /**
     * Setter for a custom rotation animation duration in MS
     *
     * @param animationDuration in MS
     */
    public void setParentClickableViewAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    /**
     * Setter for a custom clickable view to expand or collapse the item. This should be passed
     * as a reference to the View's R.id
     *
     * @param customParentAnimationViewId
     */
    public void setCustomParentAnimationViewId(int customParentAnimationViewId) {
        mCustomParentAnimationViewId = customParentAnimationViewId;
    }

    /**
     * Set the ability to be able to click both the whole parent view and the custom button to trigger
     * expanding and collapsing
     *
     * @param parentAndIconClickable
     */
    public void setParentAndIconExpandOnClick(boolean parentAndIconClickable) {
        mParentAndIconClickable = parentAndIconClickable;
    }

    /**
     * Call this when removing the animtion. This will set the parent item to be the expand/collapse
     * trigger. It will also disable the rotation animation.
     */
    public void removeAnimation() {
        mCustomParentAnimationViewId = CUSTOM_ANIMATION_VIEW_NOT_SET;
        mAnimationDuration = CUSTOM_ANIMATION_DURATION_NOT_SET;
    }

    public void addExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
        mListener = expandCollapseListener;
    }

    /**
     * Convenience method to expand a ParentObject if it is collapsed.
     *
     * @param parentObject
     * @param position
     */
    public void expandParent(ParentObject parentObject, int position) {
        Object helperItem = mHelper.getHelperItemAtPosition(position);
        if (helperItem instanceof ParentWrapper) {
            ParentWrapper parentWrapper = (ParentWrapper) helperItem;
            if (parentWrapper.getParentObject().equals(parentObject) && !parentWrapper.isExpanded()) {
                toggleParentExpansion(parentObject, position);
            }
        }
    }

    /**
     * Convenience method to collapse a ParentObject if it is expanded.
     *
     * @param parentObject
     * @param position
     */
    public void collapseParent(ParentObject parentObject, int position) {
        Object helperItem = mHelper.getHelperItemAtPosition(position);
        if (helperItem instanceof ParentWrapper) {
            ParentWrapper parentWrapper = (ParentWrapper) helperItem;
            if (parentWrapper.getParentObject().equals(parentObject) && parentWrapper.isExpanded()) {
                toggleParentExpansion(parentObject, position);
            }
        }
    }

    /**
     * Method called to expand a ParentObject when clicked. This handles saving state, adding the
     * corresponding child objects to the list (the recyclerview list) and updating that list.
     * It also calls the appropriate ExpandCollapseListener methods, if it exists
     *
     * @param parentObject
     * @param position
     */
    private void toggleParentExpansion(ParentObject parentObject, int position) {
        ParentWrapper parentWrapper = (ParentWrapper) mHelper.getHelperItemAtPosition(position);
        if (parentWrapper == null) {
            return;
        }
        if (parentWrapper.isExpanded()) {
            parentWrapper.setExpanded(false);

            List<?> childObjectList = ((ParentObject) parentWrapper.getParentObject()).getChildObjectList();
            if (childObjectList != null) {
                for (int i = childObjectList.size() - 1; i >= 0; i--) {
                    Object parentChild = childObjectList.get(i);
                    if (parentChild instanceof ParentObject) {
                        for (int j = mItemList.size() - 1; j >= 0; j--) {
                            Object item = mItemList.get(j);
                            if (item == parentChild) {
                                collapseParent((ParentObject) parentChild, j);
                            }
                        }
                    }
                }
                List<Object> helperList = mHelper.getHelperItemList();
                for (int i = childObjectList.size() - 1; i >= 0; i--) {
                    int childPos = position + i + 1;
                    mItemList.remove(childPos);
                    helperList.remove(childPos);
                    notifyItemRemoved(childPos);
                }
            }
            if (mListener != null) {
                int expandedCountBeforePosition = getExpandedItemCount(position);
                mListener.onRecyclerViewItemCollapsed(parentObject,
                                                                    position - expandedCountBeforePosition);
            }
        } else {
            parentWrapper.setExpanded(true);

            List<?> childObjectList = ((ParentObject) parentWrapper.getParentObject()).getChildObjectList();
            if (childObjectList != null) {
                for (int i = 0; i < childObjectList.size(); i++) {
                    int newPos = position + i + 1;
                    Object child = childObjectList.get(i);
                    mHelper.addItem(newPos, child);
                    mItemList.add(newPos, child);
                    notifyItemInserted(newPos);
                }
            }
            if (mListener != null) {
                int expandedCountBeforePosition = getExpandedItemCount(position);
                mListener.onRecyclerViewItemExpanded(parentObject, position - expandedCountBeforePosition);
            }
        }
    }

    /**
     * Method to get the number of expanded children before the specified position.
     *
     * @param position
     * @return number of expanded children before the specified position
     */
    private int getExpandedItemCount(int position) {
        if (position == 0) {
            return 0;
        }

        int expandedCount = 0;
        for (int i = 0; i < position; i++) {
            Object object = mItemList.get(i);
            if (!(object instanceof ParentObject)) {
                expandedCount++;
            }
        }
        return expandedCount;
    }

    /**
     * Generates an ArrayList of type Object for keeping track of all objects including children
     * that are added to the RV. Takes in a list of parents so the user doesn't have to pass in
     * a list of objects.
     *
     * @param parentObjectList the list of all parent objects provided by the user
     * @return ArrayList of type Object that handles the items in the RV
     */
    private ArrayList<Object> generateObjectList(List<ParentObject> parentObjectList) {
        ArrayList<Object> objectList = new ArrayList<>();
        for (ParentObject parentObject : parentObjectList) {
            objectList.add(parentObject);
        }
        return objectList;
    }

    /**
     * Should be called from onSaveInstanceState of Activity that holds the RecyclerView.
     * This will make sure to add the generated HashMap as an extra
     * to the bundle to be used in OnRestoreInstanceState().
     *
     * @param savedInstanceStateBundle
     */
    public void onSaveInstanceState(Bundle savedInstanceStateBundle) {
        ArrayList<Integer> expanded = new ArrayList<>();
        for (int i = 0; i < mItemList.size(); i++) {
            Object helper = mHelper.getHelperItemAtPosition(i);
            if (helper instanceof ParentWrapper) {
                ParentWrapper wrapper = (ParentWrapper) helper;
                if (wrapper.isExpanded()) {
                    expanded.add(i);
                }
            }
        }
        savedInstanceStateBundle.putIntegerArrayList(EXPANDED_POSITION_LIST, expanded);
    }

    /**
     * Should be called from onRestoreInstanceState of Activity that contains the ExpandingRecyclerView.
     * This will fetch the HashMap that was saved in onSaveInstanceState() and use it to restore
     * the expanded states before the rotation or onSaveInstanceState was called.
     *
     * @param savedInstanceStateBundle
     */
    public void onRestoreInstanceState(Bundle savedInstanceStateBundle) {
        if (savedInstanceStateBundle == null || !savedInstanceStateBundle.containsKey(EXPANDED_POSITION_LIST)) {
            return;
        }

        ArrayList<Integer> expanded = savedInstanceStateBundle.getIntegerArrayList(EXPANDED_POSITION_LIST);
        if (expanded == null) {
            return;
        }

        int i = 0;
        while (i < mHelper.getHelperItemList().size()) {
            if (expanded.contains(i)) {
                Object item = mHelper.getHelperItemAtPosition(i);
                if (item instanceof ParentWrapper) {
                    ParentWrapper parentWrapper = (ParentWrapper) item;
                    if (!parentWrapper.isExpanded()) {
                        parentWrapper.setExpanded(true);
                        List<?> childObjectList = parentWrapper.getParentObject().getChildObjectList();
                        if (childObjectList != null) {
                            for (int j = 0; j < childObjectList.size(); j++) {
                                Object child = childObjectList.get(j);
                                int nextPos = i + j + 1;
                                mItemList.add(nextPos, child);
                                mHelper.addItem(nextPos, child);
                            }
                        }
                    }
                }
            }
            i++;
        }
        notifyDataSetChanged();
    }
}
