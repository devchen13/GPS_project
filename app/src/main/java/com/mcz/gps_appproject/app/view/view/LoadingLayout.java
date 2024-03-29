package com.mcz.gps_appproject.app.view.view;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 *
 * @author mcz
 *
 */
public abstract class LoadingLayout extends FrameLayout {

    protected final boolean mHeaderOrFooter;

    protected final PullToRefreshBase.Orientation mScrollDirection;

    private View mContentView;
    private Runnable mPostRenderRunnable;

    public LoadingLayout(Context context, final boolean headerOrFooter, final PullToRefreshBase.Orientation scrollDirection) {
        super(context);
        mHeaderOrFooter = headerOrFooter;
        mScrollDirection = scrollDirection;
    }

    protected void setContentView(int layoutResID) {
        mContentView = LayoutInflater.from(getContext()).inflate(layoutResID, this, false);
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        if (mHeaderOrFooter)
            lp.gravity = mScrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;
        else
            lp.gravity = mScrollDirection == PullToRefreshBase.Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;
        addView(mContentView, lp);
    }

    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    public int getContentSize(PullToRefreshBase.Orientation orientation) {
        switch (orientation) {
            case HORIZONTAL:
                return mContentView.getWidth();
            case VERTICAL:
            default:
                return mContentView.getHeight();
        }
    }

    /**
     * 开始上拉或下拉去刷新时的状态处理
     */
    public abstract void pullToRefresh();

    /**
     * 正在刷新时的状态处理
     */
    public abstract void refreshing();

    /**
     * 释放即可刷新时的状态处理
     */
    public abstract void releaseToRefresh();

    /**
     * 正在在上拉或下拉时的状态处理
     *
     * @param scaleOfLayout
     *            拉动距离相较于本身高（宽）度的比例
     */
    public abstract void onPull(float scaleOfLayout);

    /**
     * 状态初始化处理
     */
    public abstract void reset();

    /**
     * 停止刷新状态变化
     */
    public abstract void disableRefresh();

    public final void showInvisibleViews() {
        if (View.INVISIBLE == mContentView.getVisibility()) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    public final void hideAllViews() {
        if (View.VISIBLE == mContentView.getVisibility()) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }

    public void postRenderRunnable(Runnable r) {
        mPostRenderRunnable = r;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        boolean result = mScrollDirection == PullToRefreshBase.Orientation.VERTICAL ? (bottom - top) > 0 : (right - left) > 0;
        if (result && mPostRenderRunnable != null) {
            mPostRenderRunnable.run();
            mPostRenderRunnable = null;
        }
    }
}