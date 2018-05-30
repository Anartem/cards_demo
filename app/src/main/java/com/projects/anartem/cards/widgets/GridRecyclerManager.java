package com.projects.anartem.cards.widgets;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class GridRecyclerManager extends RecyclerView.LayoutManager {
    //temp cache for visible view
    private final SparseArray<View> mViewCache = new SparseArray<>();

    //bounds of visible views
    private int mFirstPosition = 0;
    private int mLastPosition = 0;
    private final Rect mBounds = new Rect();

    // Consistent size applied to all child views
    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;

    //bounds of anchor
    private int mAnchorPosition = 0;
    private final Rect mAnchorBounds = new Rect();

    //grid settings
    private final int mColumnCount;

    public GridRecyclerManager(int columnCount) {
        mColumnCount = columnCount;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy,
                                  RecyclerView.Recycler recycler,
                                  RecyclerView.State state) {
        int scroll = getMaxVerticalScroll(dy);
        if (scroll != 0) {
            scrollVerticallyBy(scroll, recycler);
        }

        return scroll;
    }

    private void scrollVerticallyBy(int dy,
                                    RecyclerView.Recycler recycler) {
        offsetChildrenVertical(-dy);
        fill(recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx,
                                    RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        int scroll = getMaxHorizontalScroll(dx);
        if (scroll != 0) {
            scrollHorizontallyBy(scroll, recycler);
        }

        return scroll;
    }

    private void scrollHorizontallyBy(int dx,
                                      RecyclerView.Recycler recycler) {
        offsetChildrenHorizontal(-dx);
        fill(recycler);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        //scrap measure one child
        if (getChildCount() == 0) {
            View scrap = recycler.getViewForPosition(0);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);

            //compute size of view
            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);

            detachAndScrapView(scrap, recycler);
        }

        detachAndScrapAttachedViews(recycler);
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {
        final View anchorView = getAnchorView();

        mViewCache.clear();

        if (anchorView != null) {
            //update view bounds
            mFirstPosition = getPosition(anchorView);
            mLastPosition = getPosition(anchorView);
            getDecoratedBoundsWithMargins(anchorView, mBounds);

            //update anchor bounds
            mAnchorPosition = mFirstPosition;
            getDecoratedBoundsWithMargins(anchorView, mAnchorBounds);
        }

        //put views to cache
        for (int i = 0, max = getChildCount(); i < max; i++) {
            View view = getChildAt(i);

            int pos = getPosition(view);
            mViewCache.put(pos, view);
        }

        //remove view from layout
        for (int i = 0, max = mViewCache.size(); i < max; i++) {
            detachView(mViewCache.valueAt(i));
        }

        fillVertical(recycler);

        //recycle invisible views
        for (int i = 0, max = mViewCache.size(); i < max; i++) {
            recycler.recycleView(mViewCache.valueAt(i));
        }
    }

    //get first visible view
    private View getAnchorView() {
        for (int i = 0, max = getChildCount(); i < max; i++) {
            View view = getChildAt(i);

            if (isViewPartiallyVisible(view, true, false)) {
                return view;
            }
        }

        return null;
    }

    private void fillVertical(RecyclerView.Recycler recycler) {
        fillUp(recycler);
        fillDown(recycler);
    }

    private void fillHorizontal(@NonNull View anchorView,
                                RecyclerView.Recycler recycler) {
        int minPosition = getPosition(anchorView) / mColumnCount * mColumnCount;
        int maxPosition = Math.min(getItemCount(), minPosition + mColumnCount);

        fillLeft(anchorView, recycler, minPosition);
        fillRight(anchorView, recycler, maxPosition);
    }

    private void fillUp(RecyclerView.Recycler recycler) {
        int position = mAnchorPosition - mColumnCount;
        int left = mAnchorBounds.left;
        int top = mAnchorBounds.top;

        View view = null;

        while (top > 0 && position >= 0) {
            view = mViewCache.get(position);

            if (view == null) {
                view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);

                layoutDecorated(
                        view,
                        left,
                        top - mDecoratedChildHeight,
                        left + mDecoratedChildWidth,
                        top);
            } else {
                attachView(view);
                mViewCache.remove(position);
            }

            top = getDecoratedTop(view);
            position -= mColumnCount;

            fillHorizontal(view, recycler);
        }

        updatePosition(view, position + mColumnCount);
    }

    private void fillDown(RecyclerView.Recycler recycler) {
        int position = mAnchorPosition;
        int left = mAnchorBounds.left;
        int bottom = mAnchorBounds.top;

        View view = null;

        while (bottom <= getHeight() && position < getItemCount()) {
            view = mViewCache.get(position);

            if (view == null) {
                view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);

                layoutDecorated(
                        view,
                        left,
                        bottom,
                        left + mDecoratedChildWidth,
                        bottom + mDecoratedChildHeight);
            } else {
                attachView(view);
                mViewCache.remove(position);
            }

            bottom = getDecoratedBottom(view);
            position += mColumnCount;

            fillHorizontal(view, recycler);
        }

        updatePosition(view, position - mColumnCount);
    }

    private void fillLeft(@NonNull View anchorView,
                          RecyclerView.Recycler recycler,
                          int minPosition) {
        int position = getPosition(anchorView) - 1;
        int top = getDecoratedTop(anchorView);
        int left = getDecoratedLeft(anchorView);

        View view = null;

        while (left > 0 && position >= minPosition) {
            view = mViewCache.get(position);

            if (view == null) {
                view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);

                layoutDecorated(
                        view,
                        left - mDecoratedChildWidth,
                        top,
                        left,
                        top + mDecoratedChildHeight);
            } else {
                attachView(view);
                mViewCache.remove(position);
            }

            left = getDecoratedLeft(view);
            position--;
        }

        updatePosition(view, position + 1);
    }

    private void fillRight(@NonNull View anchorView,
                           RecyclerView.Recycler recycler,
                           int maxPosition) {
        int position = getPosition(anchorView) + 1;
        int top = getDecoratedTop(anchorView);
        int right = getDecoratedRight(anchorView);

        View view = null;

        while (right <= getWidth() && position < maxPosition) {
            view = mViewCache.get(position);

            if (view == null) {
                view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);

                layoutDecorated(
                        view,
                        right,
                        top,
                        right + mDecoratedChildWidth,
                        top + mDecoratedChildHeight);
            } else {
                attachView(view);
                mViewCache.remove(position);
            }

            right = getDecoratedRight(view);
            position++;
        }

        updatePosition(view, position - 1);
    }

    private void updateTopLeftBounds(View view) {
        mBounds.top = Math.min(mBounds.top, getDecoratedTop(view));
        mBounds.left = Math.min(mBounds.left, getDecoratedLeft(view));
    }

    private void updateBottomRightBounds(View view) {
        mBounds.bottom = Math.max(mBounds.bottom, getDecoratedBottom(view));
        mBounds.right = Math.max(mBounds.right, getDecoratedRight(view));
    }

    private void updatePosition(@Nullable View view, int position) {
        if (view == null) {
            return;
        }

        if (mLastPosition < position) {
            mLastPosition = position;
            updateBottomRightBounds(view);

        } else if (mFirstPosition > position) {
            mFirstPosition = position;
            updateTopLeftBounds(view);
        }
    }

    private int getMaxVerticalScroll(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();

        //no views
        if (childCount == 0 || dy == 0) {
            return 0;
        }

        //all views are visible
        if (mBounds.bottom - mBounds.top <= getHeight()) {
            return 0;
        }

        int delta;
        //scroll down
        if (dy < 0) {
            //top view is not the first
            if (mFirstPosition / mColumnCount > 0) {
                delta = dy;
            //top view is the first
            } else {
                delta = Math.max(mBounds.top, dy);
            }
        //scroll up
        } else {
            //bottom view is not the last
            if (mLastPosition / mColumnCount < (itemCount - 1) / mColumnCount) {
                delta = dy;
            //bottom view is the last
            } else {
                delta = Math.min(mBounds.bottom - getHeight(), dy);
            }
        }
        return delta;
    }

    private int getMaxHorizontalScroll(int dx) {
        int maxCount = getItemCount() - 1;
        int childCount = getChildCount();

        if (childCount == 0 || dx == 0) {
            return 0;
        }

        //all views are visible
        if (mBounds.right - mBounds.left <= getWidth()) {
            return 0;
        }

        int delta;
        //scroll left
        if (dx < 0) {
            //left view is not the first
            if (mFirstPosition % mColumnCount > 0) {
                delta = dx;
            //left view is the first
            } else {
                delta = Math.max(mBounds.left, dx);
            }
        //scroll right
        } else {
            //right view is not the last
            if (mLastPosition < maxCount
                    && mLastPosition % mColumnCount < mColumnCount - 1) {
                delta = dx;
            //right view is the last
            } else {
                delta = Math.min(mBounds.right - getWidth(), dx);
            }
        }
        return delta;
    }
}
