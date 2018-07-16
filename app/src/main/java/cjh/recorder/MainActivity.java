package cjh.recorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import tooth.util.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    File extDir = Environment.getExternalStorageDirectory();

    public static MainActivity instance;
    public static final String MEDIARECORDER = "mediarecorder";
    public static final String AUDIORECORDER = "audiorecorder";
    public static final String BLUERECORDER  = "bluerecorder";

    public TextView duration, voice;
    private Button audiorecorder, mediarecorder, play;
    private ImageView imageView1, imageView2;

    public boolean recordering = false;

    private long originalTime = -1l;
    private String filePath;
    private Timer timer;

    private int flag = 0;
    private AudioManager mAudioManager = null;

    private String getTime() {
        long time = System.currentTimeMillis() - originalTime;
        long second = time / 1000l;
        long mSecond = time - second * 1000;
        return second + "." + mSecond;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        duration = (TextView) findViewById(R.id.duration);
        voice = (TextView) findViewById(R.id.voice);
        play = (Button) findViewById(R.id.play);
        audiorecorder = (Button) findViewById(R.id.audiorecorder);
        mediarecorder = (Button) findViewById(R.id.mediarecorder);
        imageView1 = (ImageView)findViewById(R.id.img1);
        imageView2 = (ImageView)findViewById(R.id.img2);

        audiorecorder.setOnClickListener(this);
        mediarecorder.setOnClickListener(this);
        play.setOnClickListener(this);

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

    }

    public void mediarecorder() {
        if (!recordering) {
            recordering = true;
            audiorecorder.setVisibility(View.INVISIBLE);
            filePath = new File(extDir, MEDIARECORDER + "_" + System.currentTimeMillis() + "").getAbsolutePath() + ".amr";
            filePath = new File(extDir, AUDIORECORDER + "_" + System.currentTimeMillis() + "").getAbsolutePath() + ".pcm";
            /*startTimer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            duration.setText(getTime());
                        }
                    });
                }
            }, 0, 1);*/

            /*timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double ratio = MediaRecorderUtil.recorder.getMaxAmplitude();
                            if (ratio > test1)
                                ratio = 20 * Math.log10(ratio);
                            voice.setText("当前音量：" + ratio);
                        }
                    });
                }
            }, 0, 1000);*/
            AudioRecorderUtil.startRecordering(filePath);
            //MediaRecorderUtil.startRecordering(filePath);
            //startRecording();
        } else {
            //MediaRecorderUtil.stopRecordering();
            AudioRecorderUtil.stopRecording();
            recordering = false;
            //stopRecording();
            //AudioRecorderUtil.startRecordering(filePath);
            audiorecorder.setVisibility(View.VISIBLE);
            //stopTimer();
        }

    }

    public void audiorecorder() {

        if (!recordering) {
            recordering = true;
            mediarecorder.setVisibility(View.INVISIBLE);
            filePath = new File(extDir, AUDIORECORDER + "_" + System.currentTimeMillis() + "").getAbsolutePath() + ".pcm";
            /*startTimer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            duration.setText(getTime());
                            //System.out.println("flag1:"+flag);
                            flag ++;
                            //System.out.println("flag2:"+flag);
                            switchPictures(flag % 16);
                        }
                    });
                }
            }, 0, 2000);*/
            //AudioRecorderUtil.startRecordering(filePath);
            //Constants.audioDeviceInfo_Mic =
            startRecording();
        } else {
            //stopTimer();
            //AudioRecorderUtil.stopRecording();
            recordering = false;
            stopRecording();
            mediarecorder.setVisibility(View.VISIBLE);
        }

    }

    private void switchPictures(int flag) {
        switch (flag){
            case 1:
                imageView1.setImageResource(R.drawable.test11);
                break;
            case 2:
                imageView1.setImageResource(R.drawable.test12);
                break;
            case 3:
                imageView1.setImageResource(R.drawable.test21);
                break;
            case 4:
                imageView1.setImageResource(R.drawable.test22);
                break;
            case 5:
                imageView1.setImageResource(R.drawable.test31);
                break;
            case 6:
                imageView1.setImageResource(R.drawable.test32);
                break;
            case 7:
                imageView1.setImageResource(R.drawable.test33);
                break;
            case 8:
                imageView1.setImageResource(R.drawable.test41);
                break;
            case 9:
                imageView1.setImageResource(R.drawable.test42);
                break;
            case 10:
                imageView1.setImageResource(R.drawable.test43);
                break;
            case 11:
                imageView1.setImageResource(R.drawable.test51);
                break;
            case 12:
                imageView1.setImageResource(R.drawable.test52);
                break;
            case 13:
                imageView1.setImageResource(R.drawable.test53);
                break;
            case 14:
                imageView1.setImageResource(R.drawable.test61);
                break;
            case 15:
                imageView1.setImageResource(R.drawable.test62);
                break;
            case 0:
                imageView1.setImageResource(R.drawable.test63);
                break;
            default:
                imageView1.setImageResource(R.mipmap.ic_launcher);

        }

    }

    private void startTimer() {
        originalTime = System.currentTimeMillis();
        recordering = true;
        if (!new File(filePath).exists()) {
            try {
                new File(filePath).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void stopTimer() {
        timer.cancel();
        recordering = false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mediarecorder:
                System.out.println("click mediarecorder");
                mediarecorder();
                break;

            case R.id.audiorecorder:
                System.out.println("click audiorecorder");
                audiorecorder();
                break;

            case R.id.play:
                //startPlaying();
                System.out.println("click play");
                //Constants.flag2 = ! Constants.flag2;
                mediarecorder();
                audiorecorder();
                break;

            default:
                break;
        }
    }

    private void startPlaying() {
        if (!TextUtils.isEmpty(filePath))
            MediaPlayerUtil.startPlaying(filePath.contains(".pcm") ? filePath.replace(".pcm", ".wav") : filePath);
        startPlaying(mFileName);
        Log.i(TAG,"startPlaying");
    }

    public void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    private String TAG = "BluetoothRecord";
    private static String mFileName = null;
    private AudioRecord mRecorder = null;

    class BlueRecordThread implements Runnable {
        @Override
        public void run() {
            //startRecording();
            writeDateTOFile();
            System.out.println("writeDataToFile");
        }
    }

    // 音频获取源
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准
    private static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private static int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
            channelConfig, audioFormat);

    private static boolean first = true;
    //record
    public void startRecording(){

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";
        mRecorder = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        if(Constants.audioDeviceInfo_Mic!=null){
            System.out.println("Mic type:"+Constants.audioDeviceInfo_Mic.getType());

            mRecorder.setPreferredDevice(Constants.audioDeviceInfo_Mic);
        }
        /*if(Constants.flag2){
        }*/
        //AudioDeviceInfo audioDeviceInfo = new AudioDeviceInfo();
        //mRecorder.setPreferredDevice(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
        /*mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);*/
        /*try {
            mRecorder.prepare();
        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "prepare() failed!");
        }*/
        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.i(TAG, "系统不支持蓝牙录音");
            System.out.println("系统不支持蓝牙录音");
            return;
        }
        Log.i(TAG, "系统支持蓝牙录音");
        System.out.println("系统支持蓝牙录音");
        mAudioManager.stopBluetoothSco();
        mAudioManager.startBluetoothSco();//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);

                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    Log.i(TAG, "AudioManager.SCO_AUDIO_STATE_CONNECTED");
                    System.out.println("AudioManager.SCO_AUDIO_STATE_CONNECTED");
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    Log.i(TAG, "Routing:" + mAudioManager.isBluetoothScoOn());
                    System.out.println("Routing:" + mAudioManager.isBluetoothScoOn());
                    mAudioManager.setMode(AudioManager.STREAM_MUSIC);
                    //mRecorder.start();//开始录音
                    mRecorder.startRecording();
                    Constants.audioDeviceInfo_Blu = mRecorder.getRoutedDevice();
                    //System.out.println("audioDeviceInfo Blu" + Constants.audioDeviceInfo_Blu.getType());
                    new Thread(new BlueRecordThread()).start();
                    unregisterReceiver(this);  //别遗漏
                }else {//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                    Log.i(TAG, "再次startBluetoothSco()");
                    System.out.println("再次startBluetoothSco()");
                    //startRecording();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }


    public void stopRecording(){
        System.out.println("stopRecording");
        //mAudioManager.stopBluetoothSco();
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if (mAudioManager.isBluetoothScoOn()) {
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
        Log.i(TAG,"stopRecording");
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

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private void writeDateTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        final byte[] audiodata = new byte[bufferSizeInBytes];
        /*FileOutputStream fos = null;
        try {
            File file = new File(mFileName);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        while (MainActivity.instance.recordering == true) {

            //Constants.audioDeviceInfo_Blu = mRecorder.getRoutedDevice();
            //System.out.println("audioDeviceInfo Blu:" + Constants.audioDeviceInfo_Blu.getType());

            final int readsize = mRecorder.read(audiodata, 0, bufferSizeInBytes);
            System.out.println("blue readsize:"+readsize);
            System.out.println("blue adudioData:"+audiodata.toString());
            for(int i =0;i<audiodata.length;i++){
                System.out.println("audiodata"+i+":"+audiodata[i]);
            }
            /*if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Timer timer = new Timer();
            if (first)
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        long v = 0;
                        // 将 buffer 内容取出，进行平方和运算
                        for (int i = 0; i < audiodata.length; i++) {
                            v += audiodata[i] * audiodata[i];
                        }
                        // 平方和除以数据总长度，得到音量大小。
                        double mean = v / (double) readsize;
                        final double volume = 10 * Math.log10(mean);
                        MainActivity.instance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.instance.voice.setText(volume + "");
                            }
                        });
                    }
                }, 0, 1000);*/
            first = false;

        }
        /*try {
            fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
