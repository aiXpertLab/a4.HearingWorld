package com.seeingvoice.www.svhearing.heartests.openslmediaplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.seeingvoice.www.svhearing.R;
import com.seeingvoice.www.svhearing.base.OnMenuClickListener;
import com.seeingvoice.www.svhearing.base.TopBarBaseActivity;
import com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.activities.musicPlayActivity;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicListActivity extends TopBarBaseActivity implements AdapterView.OnItemClickListener {

    //用于存储从系统数据库查询的出结果
    private Cursor mCursor ;
    //用于将mCursor的数据导入到List对象中，再作为Adapater参数传入
    private List<Map<String , String>> List_map  ;
    //歌曲列表为空时
    private TextView MusicListEmptyView;
    //指定布局文件的ListView
    private ListView MusicListView ;
    //申明ContentResolver对象，用于访问系统数据库
    private ContentResolver contentResolver ;
    //用于装载MusicInfo对象
    private ArrayList<MusicInfo> musicInfos ;
    //指定SimpleAdapter对象
    private SimpleAdapter simpleAdapter ;
    //权限申请码requestCode
    private final static int STORGE_REQUEST = 1 ;
    //是否处于播放状态
    private boolean isPlyer = false ;
    //用于启动服务的Intent
    private Intent intent ;
    //定义左右耳助听参数  数组大小：5
    private float[] LEQArray = null ,REQArray = null;

    @Override
    protected int getConentView() {
        return R.layout.activity_music_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        MusicListView = findViewById(R.id.MusicListView) ;
        MusicListEmptyView = findViewById(R.id.noMusicData);

        initTitle();
        //已有权限的情况下可以直接初始化程序
        initList();
    }

    private void initTitle() {
        setTitle("音乐列表");
        setTitleBack(true);
        setToolBarMenuOne("", R.mipmap.share_icon, null);
        setToolBarMenuTwo("", R.mipmap.set, null);
    }

    /* 界面列表初始化 */
    private void initList(){
        //获取系统的ContentResolver
        contentResolver = getContentResolver() ;

        //从数据库中获取指定列的信息
        mCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                new String[] {MediaStore.Audio.Media._ID ,
                        MediaStore.Audio.Media.TITLE , MediaStore.Audio.Media.ALBUM ,
                        MediaStore.Audio.Media.ARTIST , MediaStore.Audio.Media.DURATION ,
                        MediaStore.Audio.Media.DISPLAY_NAME , MediaStore.Audio.Media.SIZE ,
                        MediaStore.Audio.Media.DATA , MediaStore.Audio.Media.ALBUM_ID } , null ,null ,null) ;//9个关于音频文件的属性

        List_map = new ArrayList<Map<String, String>>() ;
        musicInfos = new ArrayList<>() ;
        if (mCursor.getCount() < 1){
            MusicListEmptyView.setText("在您的手机上没有搜索到音频文件！");
            return;
        }
        for (int i = 0 ; i < mCursor.getCount() ; i++)
        {
            Map<String , String> map = new HashMap<>() ;//哈希键值对
            MusicInfo musicInfo = new MusicInfo() ;

            //列表移动
            mCursor.moveToNext() ;

            //将数据装载到List<MusicInfo>中
            musicInfo.set_id(mCursor.getInt(0));//音乐的ID
            musicInfo.setTitle(mCursor.getString(1));//
            musicInfo.setAlbum(mCursor.getString(2));
            musicInfo.setArtist(mCursor.getString(3));
            musicInfo.setDuration(mCursor.getInt(4));
            musicInfo.setMusicName(mCursor.getString(5));
            musicInfo.setSize(mCursor.getInt(6));
            musicInfo.setData(mCursor.getString(7));
            //将数据装载到List<Map<String ,String>>中
            //获取本地音乐专辑图片
            String MusicImage = getAlbumArt(mCursor.getInt(8)) ;//得到专辑图片
            //判断本地专辑的图片是否为空
            if (MusicImage == null)//本地数据库中没有专辑封面图片
            {
                //为空，用默认图片
                map.put("image" , String.valueOf(R.mipmap.timg)) ;
                musicInfo.setAlbum_id(String.valueOf(R.mipmap.timg));//设置专辑封面图片ID
            }else//本地数据库中有专辑封面图片
            {
                //不为空，设定专辑图片为音乐显示的图片
                map.put("image" , MusicImage) ;
                musicInfo.setAlbum_id(MusicImage);
            }
            // musicInfo.setAlbum_id(mCursor.getInt(8));
            musicInfos.add(musicInfo) ;


            map.put("name" , mCursor.getString(5)) ;
            //将获取的音乐大小由Byte转换成mb 并且用float个数的数据表示
            Float size = (float) (mCursor.getInt(6) * 1.0 / 1024 / 1024)  ;
            //对size这个Float对象进行保留两位小数处理
            BigDecimal b  =   new  BigDecimal(size);
            Float  f1  =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();//浮点数 保留两位
            map.put("size" , f1.toString() + "mb") ;
            List_map.add(map) ;
        }
        //SimpleAdapter实例化
        simpleAdapter = new SimpleAdapter(this , List_map ,R.layout.music_adapte_view ,
                new String[] {"image" , "name" , "size"} ,new int[]{R.id.MusicImage ,
                R.id.MusicName , R.id.MusicSize}) ;
        //为ListView对象指定adapter
        MusicListView.setAdapter(simpleAdapter);
        //绑定item点击事件
        MusicListView.setOnItemClickListener(this);
    }

    /* 获取本地音乐专辑的图片*/
    private String getAlbumArt(int album_id)
    {
        String UriAlbum = "content://media/external/audio/albums" ;//URI文件地址
        String projecttion[] =  new String[] {"album_art"} ;//字符串数组  专辑
        Cursor cursor = contentResolver.query(Uri.parse(UriAlbum + File.separator +Integer.toString(album_id)) ,
                projecttion , null , null , null);//File.separator 文件分隔符  获取获得专辑 游标
        String album = null ;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0)//得到了数据  行不为0，列不为零
        {
            cursor.moveToNext() ;
            album = cursor.getString(0) ;//得到专辑名称
        }
        //关闭资源数据
        cursor.close();//关闭游标资源
        return album ;
    }
    /*
    申请权限处理结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORGE_REQUEST :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //完成程序的初始化
                    initList();
                    System.out.println("程序申请权限成功，完成初始化") ;
                } else {
                    System.out.println("程序没有获得相关权限，请处理");
                }
                break;
        }
    }

    /* item点击实现 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String MusicData = musicInfos.get(position).getData();//得到position位置上音乐获取文件的完整路径
        System.out.println("THE MUSIC DATA IS " + MusicData);

        //将点击位置传递给播放界面，在播放界面获取相应的音乐信息再播放。
        Bundle bundle = new Bundle() ;
        bundle.putInt("position",position);
        bundle.putSerializable("musicinfo", (Serializable) getMusicInfos());
        Intent intent = new Intent() ;

        Log.e("position is==========", "position is================== " +bundle.getInt("position"));

        //绑定需要传递的参数
        intent.putExtras(bundle);
        intent.setClass(this,musicPlayActivity.class);
        setResult(RESULT_OK,intent);
        finish();
    }
    //播放Activity调用方法来获取MusicMediainfo数据
    public ArrayList<MusicInfo> getMusicInfos()
    {
        return musicInfos ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //程序退出时，终止服务
        if (null != intent){
            try {
                stopService(intent) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}