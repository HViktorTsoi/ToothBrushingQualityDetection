package cjh.recorder;

import android.media.MediaPlayer;

/**
 * Created by chenjiahuan on 16/5/8.
 */
public class MediaPlayerUtil {


    public static void startPlaying(String filePath) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();//缓冲
            mediaPlayer.start();//开始或恢复播放
        } catch (Exception e) {
        }
    }

}
