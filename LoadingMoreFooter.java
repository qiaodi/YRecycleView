package com.youku.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youku.gamecenter.widgets.ImagesGallery;
import com.youku.phone.R;

public class LoadingMoreFooter extends LinearLayout {

    private SimpleViewSwitcher progressCon;
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    private static final int ROTATE_ANIM_DURATION = 400;

    private TextView mText;
    private View mProgress;
    private RotateAnimation mRotateCircleAnim;

    public LoadingMoreFooter(Context context) {
		super(context);
		initView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LoadingMoreFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
    public void initView(){
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.home_card_item_box_title_layout_height)));
        progressCon = new SimpleViewSwitcher(getContext());
        progressCon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addView(progressCon);
        mText = new TextView(getContext());
        mText.setText(R.string.listview_loading);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.home_card_item_text_size));

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins( (int)getResources().getDimension(R.dimen.textandiconmargin),0,0,0 );

        mText.setLayoutParams(layoutParams);
        addView(mText);

        mRotateCircleAnim = new RotateAnimation(0.0f, 359.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateCircleAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateCircleAnim.setRepeatCount(-1);
        mRotateCircleAnim.setRepeatMode(Animation.INFINITE);
        LinearInterpolator lir = new LinearInterpolator();
        mRotateCircleAnim.setInterpolator(lir);
    }

    public void setProgressStyle() {
        mProgress = new ImageView(getContext());
        ((ImageView)mProgress).setImageResource(R.drawable.header_refresh_loading);
        progressCon.setView(mProgress);
    }

    public void  setState(int state) {
        switch(state) {
            case STATE_LOADING:
                ((ImageView)mProgress).setImageResource(R.drawable.header_refresh_loading);
                mProgress.startAnimation(mRotateCircleAnim);
                progressCon.setVisibility(View.VISIBLE);
                mText.setText(getContext().getText(R.string.listview_loading));
                mText.setTextColor(Color.parseColor("#666666"));
                this.setVisibility(View.VISIBLE);
                    break;
            case STATE_COMPLETE:
                mProgress.clearAnimation();
                ((ImageView)mProgress).setImageResource(R.drawable.header_refresh_loading);
                mText.setText(getContext().getText(R.string.listview_loading));
                mText.setTextColor(Color.parseColor("#666666"));
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText(getContext().getText(R.string.nomore_loading));
                mProgress.clearAnimation();
                ((ImageView)mProgress).setImageResource(R.drawable.personalized_no_more);
                mText.setTextColor(Color.parseColor("#d4d4d4"));
                progressCon.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
        }

    }
}
