package com.youku.widget;

/**
 * Created by jianghejie on 15/11/22.
 */
interface BaseRefreshHeader {

	int STATE_NORMAL = 0;
	int STATE_RELEASE_TO_REFRESH = 1;
	int STATE_REFRESHING = 2;
	int STATE_DONE = 3;

	void onMove(float delta);

	boolean releaseAction();

	void refreshComplete();

	void setState(int stateRefreshing);

	int getVisibleHeight();

	void setVisibleHeight(int height);

	int getState();

    int getRefreshingHeight();
}