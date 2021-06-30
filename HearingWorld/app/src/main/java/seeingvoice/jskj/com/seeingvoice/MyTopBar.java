package seeingvoice.jskj.com.seeingvoice;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import seeingvoice.jskj.com.seeingvoice.base.OnMenuClickListener;
import seeingvoice.jskj.com.seeingvoice.base.util.ActivityStackManager;

/**
 * Date:2021/1/23
 * auther: LeoReny@hypech.com
 */

public abstract class MyTopBar extends MyBaseActivity {

    private String menuStr,menuStr1;
    private int menuResId,menuResId1;
    private TextView tvTitle;
    private FrameLayout viewContent;
    private Toolbar mToolBar;
    private OnClickRightListener onClickRightListener;
    private OnClickRightListener onClickRightListener2;
    private MenuItem  gMenuItem = null;
    private MenuItem  gMenuItem1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_toolbar);

        //1、设置支出，并不显示项目的title文字
        mToolBar = findViewById(R.id.l_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //2、将子类的布局解析到 FrameLayout 里面
        viewContent = findViewById(R.id.toolbar_frame_ViewContent);
        LayoutInflater.from(this).inflate(getContentView_sv(), viewContent);

        //3、初始化操作（此方法必须放在最后执行位置）
        init(savedInstanceState);
    }

    /**
     * 设置页面标题
     *
     * @param title 标题文字
     */
    protected void setToolbarTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle = findViewById(R.id.toolbar_title);
            tvTitle.setText(title);
        }
    }

    /**
     * 设置显示返回按钮
     */
    protected void setToolbarBack(boolean visible) {
        if (visible) {
            mToolBar.setNavigationIcon(R.mipmap.return_icon);//设置返回按钮
        }
    }

    /** * 设置布局资源*/
    protected abstract int getContentView_sv();
    /** 初始化操作  * @param savedInstanceState */
    protected abstract void init(Bundle savedInstanceState);

    /** menu 右侧监听接口*/
    public interface OnClickRightListener {
        void onClick(MenuItem v);
    }

    /**
     * 设置标题栏右键按钮事件
     * @param menuStr 文字
     * @param menuResId 图片icon
     * @param onClickListener 事件响应
     */
    protected void setToolBarMenuOne(String menuStr, int menuResId, OnMenuClickListener onClickListener) {
        this.onClickRightListener = onClickListener;
        this.menuStr = menuStr;
        this.menuResId = menuResId;
    }

    protected void setToolBarMenuTwo(String menuStr, int menuResId, OnMenuClickListener onClickListener) {
        this.onClickRightListener2 = onClickListener;
        this.menuStr1 = menuStr;
        this.menuResId1 = menuResId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (null != onClickRightListener && null != onClickRightListener2){
            getMenuInflater().inflate(R.menu.toolbar, menu);
            gMenuItem = menu.findItem(R.id.menu_item_one);
            gMenuItem1 = menu.findItem(R.id.menu_item_two);
            gMenuItem.setIcon(menuResId);
            gMenuItem1.setIcon(menuResId1);
        }else if (null != onClickRightListener){
            getMenuInflater().inflate(R.menu.toolbar1, menu);
            gMenuItem = menu.findItem(R.id.menu_item_one);
            gMenuItem.setIcon(menuResId);
        }else if (null != onClickRightListener2){
            getMenuInflater().inflate(R.menu.toolbar2, menu);
            gMenuItem1 = menu.findItem(R.id.menu_item_two);
            gMenuItem1.setIcon(menuResId1);
        }else {
            return true;
        }
        return true;
    }

    /**
     * 设置拦截事件处理业务逻辑
     * @param item 自定义菜单项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityStackManager.getActivityStackManager().popActivity(this);
                break;
            case R.id.menu_item_one:
                this.onClickRightListener.onClick(item);
                break;
            case R.id.menu_item_two:
                this.onClickRightListener2.onClick(item);
                break;
        }
        return true;//拦截系统处理事件
    }
}