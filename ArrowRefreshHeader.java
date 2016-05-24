package com.youku.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youku.phone.R;
import com.youku.phone.Youku;
import com.youku.util.Logger;

/**
 * 首页下拉刷新
 */
public class ArrowRefreshHeader extends LinearLayout implements BaseRefreshHeader {

	private FrameLayout mContainer;
	private ImageView mArrowImageView;
	private SimpleViewSwitcher mProgressBar;
	private TextView mStatusTextView;
	private int mState = STATE_NORMAL;

	private TextView mHeaderTimeView;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

    private ImageView mBgImage;

	private static final int ROTATE_ANIM_DURATION = 400;

	public int mMaxPullDownDistance;  // 超过这个值，就自动弹回，进入loading动画

    public int mBgImageMaxHeight; // 背景图片最大高度

    public int mArrowRotateHeight; // 箭头方向变化的时刻  必须大于图片最大高度

    public int mRefreshingHeight; // loading时回弹高度

    private Animation mRotateCircleAnim;

    public ArrowRefreshHeader(Context context) {
		super(context);
		initView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ArrowRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
        mMaxPullDownDistance = getResources().getDimensionPixelOffset(R.dimen.homepage_max_pulldown_distance);
        mBgImageMaxHeight = getResources().getDimensionPixelOffset(R.dimen.homepage_max_bg_image_height);
        mArrowRotateHeight = getResources().getDimensionPixelOffset(R.dimen.homepage_arrow_rotate_distance);
        mRefreshingHeight = getResources().getDimensionPixelOffset(R.dimen.homepage_refreshing_height);

		// 初始情况，设置下拉刷新view高度为0
		mContainer = (FrameLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.listview_header, null);
        mBgImage = (ImageView) mContainer.findViewById(R.id.bg_image);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
		this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

		addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.listview_header_arrow);

		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
        mRotateCircleAnim = new RotateAnimation(0.0f, 359.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateCircleAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateCircleAnim.setRepeatCount(-1);
        mRotateCircleAnim.setRepeatMode(Animation.INFINITE);
        LinearInterpolator lir = new LinearInterpolator();
        mRotateCircleAnim.setInterpolator(lir);

		measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
	}


    public void setArrowImageView(int resid){
        mArrowImageView.setImageResource(resid);
    }

    public void setBgImage(){
        Logger.d("Home","homeRefreshBgImage " + Youku.homeRefreshBgImage);
        if(mBgImage != null && !Youku.homeRefreshBgImage.equals("")){
            Glide.with(mBgImage.getContext()).load(Youku.homeRefreshBgImage).into(mBgImage);
        }
    }

    public ImageView getBgImageView(){return mBgImage;}

	public void setState(int state) {
		if (state == mState) return ;

		switch(state){
            case STATE_NORMAL:
                mArrowImageView.setImageResource(R.drawable.header_arrowdown);
                mArrowImageView.setVisibility(View.VISIBLE);
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                }
                break;
            case STATE_REFRESHING:
                mArrowImageView.setImageResource(R.drawable.header_refresh_loading);
                mArrowImageView.startAnimation(mRotateCircleAnim);
                break;
            case STATE_DONE:
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.GONE);
                break;
            default:
		}
		
		mState = state;
	}

    public int getState() {
        return mState;
    }

    @Override
	public void refreshComplete(){
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                reset();
            }
        }, 200);
	}

	public void setVisibleHeight(int height) {
		if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
	}

	public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		return lp.height;
	}

    @Override
    public void onMove(float delta) {
        if(getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
//                if (getVisibleHeight() > mArrowRotateHeight) {
//                    setState(STATE_RELEASE_TO_REFRESH);
//                }else {
//                    if(getVisibleHeight() > mBgImageMaxHeight){
//                     ViewGroup.LayoutParams layoutParams = mBgImage.getLayoutParams();
//                        layoutParams.height = mBgImageMaxHeight;
//                        mBgImage.setLayoutParams(layoutParams);
//                    }
//                    setState(STATE_NORMAL);
//                }

                if (getVisibleHeight() > mArrowRotateHeight && getVisibleHeight() < mBgImageMaxHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else if(getVisibleHeight() >= mBgImageMaxHeight){
                    ViewGroup.LayoutParams layoutParams = mBgImage.getLayoutParams();
                    layoutParams.height = mBgImageMaxHeight;
                    mBgImage.setLayoutParams(layoutParams);
                }else{
                    setState(STATE_NORMAL);
                }

            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() >= mArrowRotateHeight &&  mState < STATE_REFRESHING){ // 当滑动超过背景图的高度时，松手即刷新
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMaxPullDownDistance) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mRefreshingHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setBgImage();
                setState(STATE_NORMAL);
            }
        }, 500);
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

    public void setProgressStyle(int style) {
    }

    public int getRefreshingHeight() {
        return mRefreshingHeight;
    }
}