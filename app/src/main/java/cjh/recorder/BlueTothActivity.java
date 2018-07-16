package cjh.recorder;

/**
 * Created by admin on 2017/10/11.
 */

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BlueTothActivity extends Activity{

    private String TAG = "BluetoothRecord";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private AudioManager mAudioManager = null;

    public BlueTothActivity(AudioManager audioManager){
        this.mAudioManager = audioManager;
    }

    //record
    public void startRecording(){

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "prepare() failed!");
        }
        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.i(TAG, "系统不支持蓝牙录音");
            return;
        }
        Log.i(TAG, "系统支持蓝牙录音");
        mAudioManager.stopBluetoothSco();
        mAudioManager.startBluetoothSco();//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);

                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    Log.i(TAG, "AudioManager.SCO_AUDIO_STATE_CONNECTED");
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    Log.i(TAG, "Routing:" + mAudioManager.isBluetoothScoOn());
                    mAudioManager.setMode(AudioManager.STREAM_MUSIC);
                    mRecorder.start();//开始录音
                    unregisterReceiver(this);  //别遗漏
                }else {//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                    Log.i(TAG, "再次startBluetoothSco()");

                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }


    public void stopRecording(){
        //mAudioManager.stopBluetoothSco();
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if (mAudioManager.isBluetoothScoOn()) {
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
        Log.i(TAG,"stopRecording");
        startPlaying(mFileName);
        Log.i(TAG,"startPlaying");
    }

    public void startPlaying(String filePath) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();//缓冲
            mediaPlayer.start();//开始或恢复播放
        } catch (Exception e) {
        }
    }
}
