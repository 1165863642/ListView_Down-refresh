package com.a11658.android_listview_down_refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * @author Qiang
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class ReFlashListView extends ListView implements AbsListView.OnScrollListener {
    View header; //顶部布局文件
    int headerHeight;   //顶部布局文件的高度
    int firstVisibleItem; //当前第一个可见的item的位置

    int scrollState;
    boolean isRemark; // 标记, 当前是在listiew的最顶端 按下的
    int startY;//按下时的Y

    int state; //当前的状态
    final int NONE = 0; //正常状态
    final int PULL = 1; //提示下拉状态
    final int RELESE = 2; //提示释放状态
    final int REFLASHING = 3; //提示正在刷新状态
    IReflashListener iReflashListener; //刷新数据接口

    public ReFlashListView(Context context) {
        super(context);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化界面, 添加顶部布局文件到listview
     *
     * @param context
     */
    private void initView(Context context) {
        header = LayoutInflater.from(context).inflate(R.layout.header_layout, null);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        Log.i("TAG", "headerHeight = " + headerHeight);
        topPadding(-headerHeight);
        this.addHeaderView(header);

        this.setOnScrollListener(this);
    }


    /**
     * 设置header布局的上边距
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    /**
     * 通知父布局,占用的宽,高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);

    }

    //滚动监听事件
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    //滚动监听事件
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    //如果状态提示释放状态,弹起, 切换到刷新状态
                    state = REFLASHING;
                    reflashViewByState();
                    topPadding(headerHeight);
                    iReflashListener.onReflash();
                } else if (state == PULL) {
                    //加载最新数据
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程中的操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state) {
            case NONE: //正常状态
                //移动距离大于0 切换到下拉状态
                if (space > 0) {
                    state = PULL;
                    reflashViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                //移动距离大于一定值并且当前滚动状态是滚动的, 切换到释放状态
                if (space > headerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    //如果距离小于一定高度 变成下拉状态
                    state = PULL;
                    reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
            case REFLASHING:
                break;
        }
    }

    /**
     * 根据当前状态改变界面显示
     */
    private void reflashViewByState() {
        TextView tip = header.findViewById(R.id.tip);
        ProgressBar progressBar = header.findViewById(R.id.progress);
        switch (state) {
            case NONE:
                topPadding(-headerHeight);
                break;
            case PULL:
                progressBar.setVisibility(GONE);
                tip.setText("下拉可以刷新");
                break;
            case RELESE:
                tip.setText("松开可以刷新");
                progressBar.setVisibility(GONE);
                break;
            case REFLASHING:
                tip.setText("正在刷新");
                progressBar.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 获取完数据;
     */
    public void reflashComplete() {
        state = NONE;
        isRemark = false;
        reflashViewByState();
        TextView lastupdatetime = header.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        String format1 = format.format(System.currentTimeMillis());
        lastupdatetime.setText(format1);
    }


    public void setInterface(IReflashListener iReflashListener) {
        this.iReflashListener = iReflashListener;
    }

    /**
     * 刷新数据接口
     */
    public interface IReflashListener{
        public void onReflash();
    }

}
