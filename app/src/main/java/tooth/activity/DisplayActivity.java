package tooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tooth.data.ServerInterface;
import tooth.util.Constants;
import cjh.recorder.R;
import tooth.util.ServerConstant;
import weka.AudioFeature;
import weka.Constant;
import weka.MakeDecision;

/**
 * Created by admin on 2017/10/13.
 */

public class DisplayActivity extends AppCompatActivity {

    private ImageView imageView1, imageView2;

    private ImageView inner_dfi,inner_dlbi,inner_drbi,inner_ufi,inner_ulbi,inner_urbi;
    private ImageView outer_dfo,outer_dlb,outer_drb,outer_ufo,outer_ulb,outer_urb;

    private Button button, button_result;

    private String filePath;
    private Timer timer;
    private int imageFlag = 0;
    private boolean imgFlag = false;

    private boolean[] flagList = new boolean[19];

    private int time_seconds = 0;
    private int scores = 0;

    int[] count = new int[18];

    private MyThread thread_count;
    private MyThread_getCurrentState thread_getCurrentState;
    private MyThread_getLatestScore thread_getLatestScore;

    private boolean picFlag = false;


    File extDir = Environment.getExternalStorageDirectory();

    private String TAG = "BluetoothRecord";
    private AudioRecord mRecorder = null;
    private AudioManager mAudioManager = null;
    private static String mFileName = null;

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

    private int windowLength = sampleRateInHz / 2;//3000;
    private ArrayList<Byte> data = new ArrayList<>();

    private static boolean status_flag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        imageView1 = (ImageView)findViewById(R.id.img1);
        imageView2 = (ImageView)findViewById(R.id.img2);

        inner_dfi = (ImageView)findViewById(R.id.inner_dfi);
        inner_dlbi =(ImageView)findViewById(R.id.inner_dlbi);
        inner_drbi =(ImageView)findViewById(R.id.inner_drbi);
        inner_ufi = (ImageView)findViewById(R.id.inner_ufi);
        inner_ulbi =(ImageView)findViewById(R.id.inner_ulbi);
        inner_urbi =(ImageView)findViewById(R.id.inner_urbi);

        outer_dfo = (ImageView)findViewById(R.id.outer_dfo);
        outer_dlb = (ImageView)findViewById(R.id.outer_dlb);
        outer_drb = (ImageView)findViewById(R.id.outer_drb);
        outer_ufo = (ImageView)findViewById(R.id.outer_ufo);
        outer_ulb = (ImageView)findViewById(R.id.outer_ulb);
        outer_urb = (ImageView)findViewById(R.id.outer_urb);


        button = (Button)findViewById(R.id.audiorecorder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!status_flag) {
                    button.setText("数据采集中");
                    //startRecording();
                    status_flag = true;
                    thread_count = new MyThread();         // start thread
                    thread_count.start();
                    thread_getCurrentState = new MyThread_getCurrentState();
                    thread_getCurrentState.start();

                } else {
                    button.setText("开始采集");
                    //stopRecording();
                    //thread_count.interrupt();
                    status_flag = false;
                    thread_getLatestScore = new MyThread_getLatestScore();
                    thread_getLatestScore.start();
                    //count_scores();
                }
            }
        });

        button_result = (Button)findViewById(R.id.display_result);
        button_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this,BrushResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.TOTAL_TIME,time_seconds);
                bundle.putInt(Constants.SCORES,scores);
                bundle.putBooleanArray(Constants.STRINGS,flagList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button_result.setClickable(false);

    }

    private void count_scores() {
        for(int i=3;i<19;i++){
            if(flagList[i]){
                scores++;
            }
        }
        scores = (int) (scores/16.0 * 100);
    }

    class BlueRecordThread implements Runnable {
        @Override
        public void run() {
            //startRecording();
            writeDateTOARFF();
            System.out.println("writeDataToFile");
        }
    }

    private void writeDateTOARFF() {
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
        while (status_flag) {

            //Constants.audioDeviceInfo_Blu = mRecorder.getRoutedDevice();
            //System.out.println("audioDeviceInfo Blu:" + Constants.audioDeviceInfo_Blu.getType());

            final int readsize = mRecorder.read(audiodata, 0, bufferSizeInBytes);
            //data.addAll(audiodata);
            //System.out.println("blue readsize:"+readsize);
            System.out.println("blue adudioData:"+audiodata.toString());

            for(int i =0;i<audiodata.length;i++){
                data.add(audiodata[i]);
                //System.out.println("audiodata"+i+":"+audiodata[i]);
            }

            while(data.size() > windowLength){
                List<Byte> datalist = data.subList(0,windowLength);
                System.out.println("windowLength:"+windowLength);
                System.out.println("datalistsize:"+datalist.size());
                /*for(int i=0;i<datalist.size();i++){
                    System.out.println("data"+i+": "+data.get(i));
                    System.out.println("datalist"+i+": "+datalist.get(i));



                }*/
                 /*
                 * By Y4
                 */
                double[] temp = new double[datalist.size()];
                for(int i=0; i<datalist.size(); i++)
                    temp[i] = Double.valueOf(datalist.get(i));
                double t_ret[] = AudioFeature.timedomain(temp);
                double f_ret[] = AudioFeature.freqdomain(temp);
                String[] attr = new String[23];
                for (int i=0; i<t_ret.length; i++)
                    attr[i] = String.valueOf(t_ret[i]);

                for (int i=0; i<f_ret.length; i++)
                    attr[10+i] = String.valueOf(f_ret[i]);
                        //tmp = tmp + String.valueOf(f_ret[i]) + ",";
                attr[22] = "?";

                //分类结果
                int class_type = MakeDecision.makedecision(attr);
                Log.e("====================", String.valueOf(class_type));
                count[class_type % 18]++;
                if (count[class_type % 18] >= 2) {
                    Log.e("!!!!!!!!!!!!!!!!", "!!!!!!!!!!");
                    switch (class_type % 18 + 1) {
                        case 3: runOnUiThread(new Runnable() {public void run() {switchPictures(3);}}); break;
                        case 4: runOnUiThread(new Runnable() {public void run() {switchPictures(4);}}); break;
                        case 5: runOnUiThread(new Runnable() {public void run() {switchPictures(5);}}); break;
                        case 6: runOnUiThread(new Runnable() {public void run() {switchPictures(6);}}); break;
                        case 7: runOnUiThread(new Runnable() {public void run() {switchPictures(7);}}); break;
                        case 8: runOnUiThread(new Runnable() {public void run() {switchPictures(8);}}); break;
                        case 9: runOnUiThread(new Runnable() {public void run() {switchPictures(9);}}); break;
                        case 10: runOnUiThread(new Runnable() {public void run() {switchPictures(10);}}); break;
                        case 11: runOnUiThread(new Runnable() {public void run() {switchPictures(11);}}); break;
                        case 12: runOnUiThread(new Runnable() {public void run() {switchPictures(12);}}); break;
                        case 13: runOnUiThread(new Runnable() {public void run() {switchPictures(13);}}); break;
                        case 14: runOnUiThread(new Runnable() {public void run() {switchPictures(14);}}); break;
                        case 15: runOnUiThread(new Runnable() {public void run() {switchPictures(15);}}); break;
                        case 16: runOnUiThread(new Runnable() {public void run() {switchPictures(16);}}); break;
                        case 17: runOnUiThread(new Runnable() {public void run() {switchPictures(17);}}); break;
                        case 18: runOnUiThread(new Runnable() {public void run() {switchPictures(18);}}); break;
                    }


                    count[class_type % 18] = 0;
                }
                for(int i=0;i<windowLength;i++){
                    data.remove(0);
                }

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
        }
        /*try {
            fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void startRecording(){

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";
        mRecorder = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);

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
                    //Constants.audioDeviceInfo_Blu = mRecorder.getRoutedDevice();
                    //System.out.println("audioDeviceInfo Blu" + Constants.audioDeviceInfo_Blu.getType());
                    new Thread(new DisplayActivity.BlueRecordThread()).start();
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

    private void switchPictures(int flag) {
        //text.setText(""+flag);
        switch (flag){
            case 2:
                if(flagList[2]){
                    //第二次漱口
                    flagList[2] = false;
                }
                else{
                    inner_dfi.setImageDrawable(null);
                    inner_dlbi.setImageDrawable(null);
                    inner_drbi.setImageDrawable(null);
                    inner_ufi.setImageDrawable(null);
                    inner_ulbi.setImageDrawable(null);
                    inner_urbi.setImageDrawable(null);
                    outer_dfo.setImageDrawable(null);
                    outer_dlb.setImageDrawable(null);
                    outer_drb.setImageDrawable(null);
                    outer_ufo.setImageDrawable(null);
                    outer_ulb.setImageDrawable(null);
                    outer_urb.setImageDrawable(null);

                    flagList[2] = true;
                    for(int i=3;i<19;i++){
                        flagList[i]=false;
                    }
                }
                break;
            case 3:
                if(flagList[6]){
                    outer_ulb.setImageResource((R.mipmap.outer_ulb));
                }
                else{
                    outer_ulb.setImageResource(R.mipmap.outer_ulbo);
                }
                flagList[3]=true;
                break;
            case 4:
                outer_ufo.setImageResource(R.mipmap.outer_ufo);
                flagList[4]=true;
                break;
            case 5:
                if(flagList[7]){
                    outer_urb.setImageResource(R.mipmap.outer_urb);
                }
                else{
                    outer_urb.setImageResource(R.mipmap.outer_urbo);
                }
                flagList[5]=true;
                break;
            case 6:
                if(flagList[3]){
                    outer_ulb.setImageResource(R.mipmap.outer_ulb);
                }
                else{
                    outer_ulb.setImageResource(R.mipmap.outer_ulbm);
                }
                flagList[6]=true;
                break;
            case 7:
                if(flagList[5]){
                    outer_urb.setImageResource(R.mipmap.outer_urb);
                }
                else{
                    outer_urb.setImageResource(R.mipmap.outer_urbm);
                }
                flagList[7]=true;
                break;
            case 8:
                inner_ulbi.setImageResource(R.mipmap.inner_ulbi);
                flagList[8]=true;
                break;
            case 9:
                inner_ufi.setImageResource(R.mipmap.inner_ufi);
                flagList[9]=true;
                break;
            case 10:
                inner_urbi.setImageResource(R.mipmap.inner_urbi);
                flagList[10]=true;
                break;
            case 11:
                if(flagList[14]){
                    outer_dlb.setImageResource(R.mipmap.outer_dlb);
                }
                else{
                    outer_dlb.setImageResource(R.mipmap.outer_dlbo);
                }
                flagList[11]=true;
                break;
            case 12:
                outer_dfo.setImageResource(R.mipmap.outer_dfo);
                flagList[12]=true;
                break;
            case 13:
                if(flagList[15]){
                    outer_drb.setImageResource(R.mipmap.outer_drb);
                }
                else{
                    outer_drb.setImageResource(R.mipmap.outer_drbo);
                }
                flagList[13]=true;
                break;
            case 14:
                if(flagList[11]){
                    outer_dlb.setImageResource(R.mipmap.outer_dlb);
                }
                else{
                    outer_dlb.setImageResource(R.mipmap.outer_dlbm);
                }
                flagList[14]=true;
                break;
            case 15:
                if(flagList[13]){
                    outer_drb.setImageResource(R.mipmap.outer_drb);
                }
                else{
                    outer_drb.setImageResource(R.mipmap.outer_drbm);
                }
                flagList[15]=true;
                break;
            case 16:
                inner_dlbi.setImageResource(R.mipmap.inner_dlbi);
                flagList[16]=true;
                break;
            case 17:
                inner_dfi.setImageResource(R.mipmap.inner_dfi);
                flagList[17]=true;
                break;
            case 18:
                inner_drbi.setImageResource(R.mipmap.inner_drbi);
                flagList[18]=true;
                break;
            default:
        }

    }

    final Handler handler = new Handler() {          // handle
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                time_seconds++;
            }
            super.handleMessage(msg);
        }
    };

    class MyThread extends Thread{      // thread

        @Override
        public void run() {
            while (status_flag) {
                try {
                    Thread.sleep(1000);     // sleep 1000ms
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                    //Thread.sleep(200);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    final Handler handler_getCurrentState = new Handler() {          // handle
        public void handleMessage(Message msg) {
            switchPictures(msg.what+1);
            Log.i(Constants.TAG,"getCurrentState_thread result :"+ msg.what);
        }
    };

    class MyThread_getCurrentState extends Thread{      // thread

        @Override
        public void run() {
            while (status_flag) {
                Log.i(Constants.TAG,"getCurrentState_thread runing ...");
                try {
                    Thread.sleep(1000);     // sleep 1000ms

                    Message message = new Message();
                    message.what = ServerInterface.getCurrentState();
                    handler_getCurrentState.sendMessage(message);

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    final Handler handler_getLatestScore = new Handler() {          // handle
        public void handleMessage(Message msg) {
            scores = msg.what;
            button_result.setClickable(true);
            Toast.makeText(DisplayActivity.this,"已结束",Toast.LENGTH_SHORT).show();
        }
    };

    class MyThread_getLatestScore extends Thread{      // thread

        @Override
        public void run() {
            Log.i(Constants.TAG,"getLastScore_thread runing ...");
            try {
                Message message = new Message();
                message.what = ServerInterface.getLatestScore();
                handler_getLatestScore.sendMessage(message);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
