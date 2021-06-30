package seeingvoice.jskj.com.seeingvoice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.util.List;
import java.util.Objects;

import seeingvoice.jskj.com.seeingvoice.base.AntiShakeUtils;
import seeingvoice.jskj.com.seeingvoice.base.util.ActivityStackManager;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L4_AudiometryT4;
import seeingvoice.jskj.com.seeingvoice.l_audiometry.L5_Thread_StaticWAV;
import seeingvoice.jskj.com.seeingvoice.l_drawer.About;
import seeingvoice.jskj.com.seeingvoice.l_drawer.AccountSecurity;
import seeingvoice.jskj.com.seeingvoice.l_drawer.L_SPLMeterL;
import seeingvoice.jskj.com.seeingvoice.l_drawer.MeProfile;
import seeingvoice.jskj.com.seeingvoice.l_drawer.SVQRCode;
import seeingvoice.jskj.com.seeingvoice.util.AlertDialogUtil;
import seeingvoice.jskj.com.seeingvoice.util.ToastUtil;
import seeingvoice.jskj.com.seeingvoice.wxapi.mIUiListener;

import static seeingvoice.jskj.com.seeingvoice.MyData.ALEART_DIALOG_REQUEST_CODE;

public class MainActivity extends MyBaseActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getName();

    private final int[] mIcons = new int[]{R.mipmap.ic_noise, R.mipmap.ic_hear_assist, R.mipmap.ic_history_icon,  R.mipmap.about_us_icon, R.mipmap.feedback_icon, R.mipmap.quit_icon};
//    private final String[] mContents = new String[]{"噪音检测",    "好友分享",       "在线帮助",               "关于我们",                  "查看权限",             "用户协议",           "隐私政策",              "退出登陆",     "remove"};
    private final String[] mContents = MyApp.getAppContext().getResources().getStringArray(R.array.sliding_menu);

    private DrawerLayout mDrawerLayout;
    LinearLayout mMeLayout;
    private Toolbar mToolbar;
    private ListView mLvItem;
    private DrawerItemAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    ShapeableImageView imageViewAvatar;

    private List<Integer> dataList;
    private TextView textViewName, textViewSignature,tv_Version_Code, tv_User_Agreement,tv_Privacy_Policy;
    private ImageView mDelImgView,ImgHeader;
    private IWXAPI iwxapi;

    private AlertDialogUtil alertDialog;
    private ProgressBar mProgressBar;
    private Intent mIntent;

    private final int login_type = -1;
    private final int user_id = -1;
    private String user_name;
    private Bitmap headBitmap;

    private Button mBtnPureTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_drawer);

        mBtnPureTest = findViewById(R.id.btn_start_pure_test);
        mBtnPureTest.setOnClickListener(this);


        // 左侧抽屉
        mDrawerLayout   = findViewById(R.id.drawer_layout);
        mLvItem         = findViewById(R.id.lv_item);
        mAdapter        = new DrawerItemAdapter();
        mLvItem.setAdapter(mAdapter);

        mToolbar         = findViewById(R.id.tool_bar);

        textViewName    = findViewById(R.id.text_view_name);
        imageViewAvatar = findViewById(R.id.iv_avatar);
        textViewSignature = findViewById(R.id.text_view_signature);

        initMe();

        findViewById(R.id.layout_me).setOnClickListener(this);

        InitToolbar();                              //初始化标题栏
        leftDrawerFunction();                       //设置左侧抽屉点击事件
    }

    //初始化标题栏
    private void InitToolbar() {
         // 注意，设置 Toolbar 及相关点击事件最好放在 setSupportActionBar 后，否则很可能无效
         // 设置 Navigation 图标和点击事件必须放在 setSupportActionBar 后，否则无效
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        TextView tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText(getString(R.string.app_name));

        //抽屉开关
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        //通过设置toolbar进行监听,在setSupportActionBar(Toolbar toolbar)之前设置可能会失效.
        // 菜单和 标题栏显示可能会冲突
        mToolbar.setNavigationIcon(R.mipmap.ic_gerencenter);
        // toolbar 结束
    }

    private void leftDrawerFunction() {
        //    噪音检测",    "好友分享",       "在线帮助",  "关于我们", account secruity
        //    "查看权限",  "用户协议", "隐私政策",  "退出登陆", "remove"};
        mLvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:     //    噪音检测"
                        Bundle bundle1 = new Bundle();
                        bundle1.putBoolean("fromIndex",true);
                        toNextActivity(bundle1, MainActivity.this, L_SPLMeterL.class);
                        break;
                    case 1:     //    好友分享
                        if (!AntiShakeUtils.isInvalidClick(view,800)) {
                            allShare(getApplicationContext().getString(R.string.topbar_share));
                        }
                        break;
                    case 2:     //       "在线帮助
                        Intent intent2 = new Intent(MainActivity.this, SVQRCode.class);
                        intent2.putExtra("from", "complain");
                        startActivity(intent2);
                        break;
                    case 3:     //                  account security
                        Intent intent4 = new Intent(MainActivity.this, AccountSecurity.class);
                        intent4.putExtra("from", "complain");
                        startActivity(intent4);
                        break;
                    case 4:     //"关于我们
                            toNextActivity(null, MainActivity.this, About.class);
                        break;
                    case 5:     //  "退出登陆",
                        if (ActivityStackManager.getActivityStackManager().checkActivity(Login.class))
                            ActivityStackManager.getActivityStackManager().popActivity(MainActivity.this);
                        else{
                            toNextActivity(null, MainActivity.this, Login.class);
                        }
                        MySP.getInstance().setLogin(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /** 左侧抽屉的点击事件 end*/

    @Override
    public void onClick(View v) {
        L5_Thread_StaticWAV tThread=null;
        if (v.getId() == R.id.btn_start_pure_test) {
            alertDialog = new AlertDialogUtil(MainActivity.this,
                    getString(R.string.global_remind),  getResources().getString(R.string.pt_reminder),
                    getString(R.string.sure),     getString(R.string.cancel),
                    ALEART_DIALOG_REQUEST_CODE, mDialogListenner);
            alertDialog.show();
        }else if (v.getId() == R.id.layout_me) {
            startActivity(new Intent(this, MeProfile.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        initMe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMe();
    }

    /**
     * 抽屉列表适配器
     */
    class DrawerItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mIcons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_drawer, null);
                vh.ivIcon =  convertView.findViewById(R.id.iv_icon);
                vh.tvContent = convertView.findViewById(R.id.tv_content1);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            vh.ivIcon.setImageResource(mIcons[position]);
            vh.tvContent.setText(mContents[position]);
            return convertView;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvContent;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIUiListener myListener = new mIUiListener();
        Tencent.onActivityResultData(requestCode,resultCode,data,myListener);
    }

    private final AlertDialogUtil.OnDialogButtonClickListener mDialogListenner = (requestCode, isPositive) -> {
        if (isPositive){
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromIndex",false);
            toNextActivity(bundle, MainActivity.this, L4_AudiometryT4.class);      //sv
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//显示菜单
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            //ShareWXQQ.getInstance().shareFunction(L3_MainActivity.this);
        allShare(getApplicationContext().getString(R.string.topbar_share));
        return false;
    }

    //广播监听接口回调 end

    private long exittime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exittime < 2000){//小于2000ms则认为是用户确实希望关闭程序-调用System.exit()方法进行退出
            finishAffinity();
        }else {
            ToastUtil.showShortToast(getString(R.string.global_exit));
            exittime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initMe(){
        textViewName.setText(MySP.getInstance().getUNickName());
        if (null != MySP.getInstance().getUAvatar()){
            imageViewAvatar.setImageURI(Uri.parse(MySP.getInstance().getUAvatar()));
        }
        textViewSignature.setText(MySP.getInstance().getUSignature());
    }
}