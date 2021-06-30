package com.seeingvoice.www.svhearing.heartests.openslmediaplayer.test.widget;

public interface IPlayInfo {
    /*用于更新标题栏变化*/
    public void onMusicInfoChanged(String musicName, String musicAuthor);
    /*用于更新背景图片*/
    public void onMusicPicChanged(int musicPicRes);
    /*用于更新音乐播放状态*/
    public void onMusicChanged(NewDiscView.MusicChangedStatus musicChangedStatus);
}
