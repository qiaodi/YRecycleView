package com.youku.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youku.phone.R;
import com.youku.util.Logger;

/**
 * Created by qiaodi on 16/5/18.
 */
public class HintRefreshHeader extends LinearLayout implements BaseRefreshHeader{

    private static final int ROTATE_ANIM_DURATION = 400;
    private int mState = STATE_NORMAL;
    private LinearLayout mContainer;
    private Animation mRotateCircleAnim;
    private ImageView mProgressCircle;
    private int mRefreshingHeight;

    public HintRefreshHeader(Context context) {
        super(context);
        initView();
    }

    public HintRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public void onMove(float delta) {
        if(mState < STATE_REFRESHING){
            if(getVisibleHeight() > 0 || delta > 0) {
                setVisibleHeight((int) delta + getVisibleHeight());
            }
            if(getVisibleHeight() >= mRefreshingHeight){
                setState(STATE_RELEASE_TO_REFRESH);
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() >= mRefreshingHeight &&  mState < STATE_REFRESHING){ // 当滑动超过预定高度时，松手即刷新
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height < mRefreshingHeight) {
            setState(STATE_NORMAL);
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mRefreshingHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    @Override
    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                reset();
            }
        }, 200);
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    @Override
    public void setState(int state) {
        Logger.d("XRecycle","state "+state);
        if (state == mState) return ;
        switch (state){
            case STATE_NORMAL:
                mProgressCircle.clearAnimation();
                break;
            case STATE_RELEASE_TO_REFRESH:
            case STATE_REFRESHING:
                mProgressCircle.startAnimation(mRotateCircleAnim);
                break;
            case STATE_DONE:
                break;
        }
        mState = state;
    }

    @Override
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getRefreshingHeight() {
        return mRefreshingHeight;
    }

    private void initView() {
        mRefreshingHeight = getResources().getDimensionPixelOffset(R.dimen.homepage_refreshing_height);

        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.personalized_page_header, null);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mProgressCircle = (ImageView)mContainer.findViewById(R.id.progress_circle);

        mRotateCircleAnim = new RotateAnimation(0.0f, 359.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateCircleAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateCircleAnim.setRepeatCount(-1);
        mRotateCircleAnim.setRepeatMode(Animation.INFINITE);
        LinearInterpolator lir = new LinearInterpolator();
        mRotateCircleAnim.setInterpolator(lir);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
