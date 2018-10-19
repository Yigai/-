package com.anjoyo.cnblog.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.R;

public class PullToRefreshListView extends ListView implements OnScrollListener {

	private Context context;

	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;

	private OnRefreshListener mOnRefreshListener;

	private OnScrollListener mOnScrollListener;
	private LayoutInflater mInflater;

	private RelativeLayout mRefreshView;
	private TextView mRefreshViewText;
	private ImageView mRefreshViewImage;
	private ProgressBar mRefreshViewProgress;
	private TextView mRefreshViewLastUpdated;

	private int mCurrentScrollState;

	private int mRefreshState;

	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;

	private int mRefreshViewHeight;
	private int mRefreshOriginalTopPadding;
	private int mLastMotionY;

	private boolean mBounceHack;

	private int MaxDateNum;
	private View moreView;
	public Button bt;
	private ProgressBar pg;
	private int lastVisibleIndex;
	public boolean isclick;

	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
		this.context = context;
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		this.context = context;
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		this.context = context;
	}
/**
 * 初始化控件和箭头动画
 * @param context
 */
	private void init(Context context) {
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRefreshView = (RelativeLayout) mInflater.inflate(
				R.layout.pull_to_refresh_header, this, false);
		mRefreshViewText = (TextView) mRefreshView
				.findViewById(R.id.pull_to_refresh_text);
		mRefreshViewImage = (ImageView) mRefreshView
				.findViewById(R.id.pull_to_refresh_image);
		mRefreshViewProgress = (ProgressBar) mRefreshView
				.findViewById(R.id.pull_to_refresh_progress);
		mRefreshViewLastUpdated = (TextView) mRefreshView
				.findViewById(R.id.pull_to_refresh_updated_at);

		mRefreshViewImage.setMinimumHeight(50);
		mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();

		mRefreshState = TAP_TO_REFRESH;
		//将上述布局文件以及动画效果 加入ListView的头部
		addHeaderView(mRefreshView);

		super.setOnScrollListener(this);

		measureView(mRefreshView);
		mRefreshViewHeight = mRefreshView.getMeasuredHeight();

		//给ListView加载一个FooterView
		MaxDateNum = 20;
		moreView = LayoutInflater.from(context)
				.inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		addFooterView(moreView);
		//给底部的按钮实现一个监听事件
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//点这个按钮 让进度条可见 按钮自身隐藏
				pg.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
				isclick = true;
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setSelection(1);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);

		setSelection(1);
	}
	/**
	 * 设置一个滚动(滑动)监听器
	 */
	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		mOnScrollListener = l;
	}
	/**
	 * 当ListView的列表需要刷新的时候 重新回调的一个监听器
	 * @param onRefreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}
/**
 * 设置文字标题显示  例如可以显示最近刷新时间等等
 * @param lastUpdated
 */
	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
			mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshViewLastUpdated.setText(lastUpdated);
		} else {
			mRefreshViewLastUpdated.setVisibility(View.GONE);
		}
	}
/**
 * 重写的一个触摸事件处理
 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int y = (int) event.getY();
		mBounceHack = false;

		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:
			if (!isVerticalScrollBarEnabled()) {
				setVerticalScrollBarEnabled(true);
			}
			if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
				//拖动距离达到一定距离的时候 （需要刷新）
				if ((mRefreshView.getBottom() >= mRefreshViewHeight || mRefreshView
						.getTop() >= 0) && mRefreshState == RELEASE_TO_REFRESH) {
					//将状态设置为 正在刷新
					mRefreshState = REFRESHING;
					//准备刷新
					prepareForRefresh();
					//刷新
					onRefresh();
					//如果取消拖动 或者 拖的距离不够
				} else if (mRefreshView.getBottom() < mRefreshViewHeight
						|| mRefreshView.getTop() <= 0) {
					//终止刷新
					resetHeader();
					setSelection(1);
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			//获取按下的y轴的位置
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			//计算边距
			applyHeaderPadding(event);
			break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * 获取headerView的边距
	 * @param ev
	 */
	private void applyHeaderPadding(MotionEvent ev) {

		int pointerCount = ev.getHistorySize();

		for (int p = 0; p < pointerCount; p++) {
			if (mRefreshState == RELEASE_TO_REFRESH) {
				if (isVerticalFadingEdgeEnabled()) {
					setVerticalScrollBarEnabled(false);
				}

				int historicalY = (int) ev.getHistoricalY(p);
				//控制下拉的程度 拉动效果
				int topPadding = (int) (((historicalY - mLastMotionY) - mRefreshViewHeight) / 1.7);

				mRefreshView.setPadding(mRefreshView.getPaddingLeft(),
						topPadding, mRefreshView.getPaddingRight(),
						mRefreshView.getPaddingBottom());
			}
		}
	}
/**
 * 将HeaderView的边距 重置为初始的数值
 */
	private void resetHeaderPadding() {
		mRefreshView.setPadding(mRefreshView.getPaddingLeft(),
				mRefreshOriginalTopPadding, mRefreshView.getPaddingRight(),
				mRefreshView.getPaddingBottom());
	}
/**
 * 将整个HeaderView重置为下拉之前的状态
 */
	private void resetHeader() {
		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;

			resetHeaderPadding();
			//将图片重新换成箭头
			mRefreshViewImage
					.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			//清除动画效果
			mRefreshViewImage.clearAnimation();
			//隐藏图标以及进度条
			mRefreshViewImage.setVisibility(View.GONE);
			mRefreshViewProgress.setVisibility(View.GONE);
		}
	}
/**
 * 估算headView的宽和高
 * @param child
 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		//在headerView完全可见的时候 将文字设置为"松开加载..." 同时翻转箭头
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& mRefreshState != REFRESHING) {
			if (firstVisibleItem == 0) {
				mRefreshViewImage.setVisibility(View.VISIBLE);
				if ((mRefreshView.getBottom() >= mRefreshViewHeight + 20 || mRefreshView
						.getTop() >= 0) && mRefreshState != RELEASE_TO_REFRESH) {
					mRefreshViewText.setText("松开加载...");
					mRefreshViewImage.clearAnimation();
					mRefreshViewImage.startAnimation(mFlipAnimation);
					mRefreshState = RELEASE_TO_REFRESH;
				} else if (mRefreshView.getBottom() < mRefreshViewHeight + 20
						&& mRefreshState != PULL_TO_REFRESH) {
					mRefreshViewText.setText("下拉刷新...");
					if (mRefreshState != TAP_TO_REFRESH) {
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mReverseFlipAnimation);
					}
					mRefreshState = PULL_TO_REFRESH;
				}
			} else {
				mRefreshViewImage.setVisibility(View.GONE);
				resetHeader();
			}
		} else if (mCurrentScrollState == SCROLL_STATE_FLING
				&& firstVisibleItem == 0 && mRefreshState != REFRESHING) {
			setSelection(1);
			mBounceHack = true;
		} else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING) {
			setSelection(1);
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}

		//
		if (mCurrentScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			//获取最后一条数据的索引
			lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
			//当所有的数据 都已经加载出来了(所有的条目数等于最大条目数) 移除掉底部的footerView
			if (totalItemCount == MaxDateNum + 1) {
				removeFooterView(moreView);
				Toast.makeText(context, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/**
	 * 滚动变化监听器
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;

		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
		//当滑动到底部的时候  执行自动加载功能
		if (mCurrentScrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == this.getAdapter().getCount()) {
			//、、、、、、、、、 异步加载数据的代码
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
		}
	}
	/**
	 * 准备刷新
	 */
	public void prepareForRefresh() {
		resetHeaderPadding();//恢复HeaderView的边距
		//将图片隐藏
		mRefreshViewImage.setVisibility(View.GONE);
		//将图片置为null
		mRefreshViewImage.setImageDrawable(null);
		mRefreshViewProgress.setVisibility(View.VISIBLE);

		mRefreshViewText.setText("加载中...");

		mRefreshState = REFRESHING;
	}

	public void onRefresh() {

		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}
	/**
	 *  当ListView加载完 可以调用该方法 设置最后更新时间
	 * @param lastUpdated
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {
		setLastUpdated(lastUpdated);
		onRefreshComplete();
	}
	/**
	 *当ListView加载完 可以调用该方法 但是没有设置最后更新时间
	 */
	public void onRefreshComplete() {

		resetHeader();

		// 如果refreshview在加载结束后可见，下滑到下一个条目
		if (mRefreshView.getBottom() > 0) {
			invalidateViews();
			setSelection(1);
		}
	}
/**
 * 刷新监听器接口
 * @author ChnAdo
 *
 */
	public interface OnRefreshListener {
		/**
		 * 需要刷新时调用该方法
		 */
		public void onRefresh();
	}

	public void onMoreComplete() {
		// TODO Auto-generated method stub
		bt.setVisibility(View.GONE);
		pg.setVisibility(View.VISIBLE);
		isclick = false;
	}
	//结束进度条
	public void dismissProgress() {
		bt.setVisibility(View.GONE);
		pg.setVisibility(View.INVISIBLE);

	}
}