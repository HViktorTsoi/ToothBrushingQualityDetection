package tooth.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import tooth.data.ServerInterface;
import tooth.util.Constants;
import cjh.recorder.R;
import tooth.util.ParseUtil;
import weka.AudioFeature;
import weka.MakeArffFile;
import weka.MakeDecision;

import static tooth.util.ParseUtil.rawAudioDataToShort;

/**
 * Created by admin on 2017/10/13.
 * Edited By Cui jiahe 2017/8/4.
 */

public class DisplayActivity extends AppCompatActivity {

    private ImageView imageView1, imageView2;

    private ImageView inner_dfi, inner_dlbi, inner_drbi, inner_ufi, inner_ulbi, inner_urbi;
    private ImageView outer_dfo, outer_dlb, outer_drb, outer_ufo, outer_ulb, outer_urb;

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
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private static int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
            channelConfig, audioFormat);

    private int windowLength = sampleRateInHz / 2;//3000;
    private int overlapPercentage = 50;//3000;
    private ArrayList<Byte> data = new ArrayList<>();

    private static boolean isRecording = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        imageView1 = (ImageView) findViewById(R.id.img1);
        imageView2 = (ImageView) findViewById(R.id.img2);

        inner_dfi = (ImageView) findViewById(R.id.inner_dfi);
        inner_dlbi = (ImageView) findViewById(R.id.inner_dlbi);
        inner_drbi = (ImageView) findViewById(R.id.inner_drbi);
        inner_ufi = (ImageView) findViewById(R.id.inner_ufi);
        inner_ulbi = (ImageView) findViewById(R.id.inner_ulbi);
        inner_urbi = (ImageView) findViewById(R.id.inner_urbi);

        outer_dfo = (ImageView) findViewById(R.id.outer_dfo);
        outer_dlb = (ImageView) findViewById(R.id.outer_dlb);
        outer_drb = (ImageView) findViewById(R.id.outer_drb);
        outer_ufo = (ImageView) findViewById(R.id.outer_ufo);
        outer_ulb = (ImageView) findViewById(R.id.outer_ulb);
        outer_urb = (ImageView) findViewById(R.id.outer_urb);


        button = (Button) findViewById(R.id.audiorecorder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    button.setText("数据采集中");
                    isRecording = true;
//                    thread_count = new MyThread();         // start thread
//                    thread_count.start();
//                    thread_getCurrentState = new MyThread_getCurrentState();
//                    thread_getCurrentState.start();
                    new Thread(new RecordThread()).start();

                } else {
                    button.setText("开始采集");
                    //stopRecording();
                    //thread_count.interrupt();
                    isRecording = false;
//                    thread_getLatestScore = new MyThread_getLatestScore();
//                    thread_getLatestScore.start();
                    //count_scores();
                }
            }
        });

        button_result = (Button) findViewById(R.id.display_result);
        button_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, BrushResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.TOTAL_TIME, time_seconds);
                bundle.putInt(Constants.SCORES, scores);
                bundle.putBooleanArray(Constants.STRINGS, flagList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button_result.setClickable(false);

    }

    private void count_scores() {
        for (int i = 3; i < 19; i++) {
            if (flagList[i]) {
                scores++;
            }
        }
        scores = (int) (scores / 16.0 * 100);
    }

    class RecordThread implements Runnable {
        @Override
        public void run() {
            try {
                recordAndRecognize();
                System.out.println("writeDataToFile");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void countAndProcess(int class_type) {
        count[class_type % 18]++;
        if (count[class_type % 18] >= 2) {
            switchPictures(class_type % 18 + 1);
            count[class_type % 18] = 0;
        }
    }

    private void recordAndRecognize() throws Exception {
        // 初始化预测模型
        MakeDecision predictModel = new MakeDecision();
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        final byte[] inputSignal = new byte[bufferSizeInBytes];
//        ArrayList<Byte> totalSignal = new ArrayList<>();
        ArrayList<Double> signalBuffer = new ArrayList<>();
        System.out.println("windowLength:" + windowLength);
        // 舍弃前面若干帧
        int ignoreFrameCounter = 5;
        int windowStart = 0;
        // 实例化录音 大小为接近windowLength的read整数
        AudioRecord mRecorder = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, 4 * bufferSizeInBytes);
        mRecorder.startRecording();
        while (isRecording) {
            final int readsize = mRecorder.read(inputSignal, 0, bufferSizeInBytes);
            if (ignoreFrameCounter > 0) {
                ignoreFrameCounter--;
                continue;
            }
            for (int i = 0; i < inputSignal.length; i += 2) {
//                totalSignal.add(anInputSignal);
                signalBuffer.add((double) ParseUtil.rawAudioDataToShort(inputSignal[i], inputSignal[i + 1]));
            }
//            // 清除噪声
//            List<Short> inputSignal_16bit = new ArrayList<>();
//            // 计算真实数值
//            for (int i = 0; i < inputSignal.length; i += 2) {
//                inputSignal_16bit.add((short) rawAudioDataToShort(inputSignal[i], inputSignal[i + 1]));
//            }
//            assert spectralSubstraction != null;
//            spectralSubstraction.setSignal(inputSignal_16bit);
//            // 对inputSignal进行降噪
//            short[] inputSignal_16bit_denoise = spectralSubstraction.noiseSubtraction();
//            for (short anInputSignal_16bit_denoise : inputSignal_16bit_denoise) {
//                signalBuffer.addAll(shortToRawAudioData(anInputSignal_16bit_denoise));
//            }
            while (windowLength < signalBuffer.size()) {
                List<Double> rawDatalist = signalBuffer.subList(0, windowLength);
                // 存储数值型数据
                // 极其重要：numericalDatalist的长度是inputSignal的一半 否则会出现一半窗口的数值都是0
                double[] numericalDatalist = new double[rawDatalist.size()];
//             将原始数据转换成double型数值数据 为了防止rawdata长度是奇数 需要判断时i+1
                for (int i = 0; i < rawDatalist.size(); ++i) {
                    numericalDatalist[i] = rawDatalist.get(i);
                }
                // 在这里处理
                String[] features = MakeArffFile.buildFeatureVector(numericalDatalist);
                int class_type = predictModel.predict(features);
//                this.countAndProcess(class_type);
                System.out.println("预测结果" + class_type);
                // 窗口后移
                ArrayList<Double> newSignalBuffer = new ArrayList<>();
                windowStart = (int) ((double) windowLength * (1 - (double) overlapPercentage / 100));
                for (int i = windowStart; i < signalBuffer.size(); ++i) {
                    newSignalBuffer.add(signalBuffer.get(i));
                }
                signalBuffer = newSignalBuffer;
            }
        }
        // 停止并释放录音实例
        mRecorder.stop();
        mRecorder.release();
    }

    private void switchPictures(int flag) {
        //text.setText(""+flag);
        switch (flag) {
            case 2:
                if (flagList[2]) {
                    //第二次漱口
                    flagList[2] = false;
                } else {
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
                    for (int i = 3; i < 19; i++) {
                        flagList[i] = false;
                    }
                }
                break;
            case 3:
                if (flagList[6]) {
                    outer_ulb.setImageResource((R.mipmap.outer_ulb));
                } else {
                    outer_ulb.setImageResource(R.mipmap.outer_ulbo);
                }
                flagList[3] = true;
                break;
            case 4:
                outer_ufo.setImageResource(R.mipmap.outer_ufo);
                flagList[4] = true;
                break;
            case 5:
                if (flagList[7]) {
                    outer_urb.setImageResource(R.mipmap.outer_urb);
                } else {
                    outer_urb.setImageResource(R.mipmap.outer_urbo);
                }
                flagList[5] = true;
                break;
            case 6:
                if (flagList[3]) {
                    outer_ulb.setImageResource(R.mipmap.outer_ulb);
                } else {
                    outer_ulb.setImageResource(R.mipmap.outer_ulbm);
                }
                flagList[6] = true;
                break;
            case 7:
                if (flagList[5]) {
                    outer_urb.setImageResource(R.mipmap.outer_urb);
                } else {
                    outer_urb.setImageResource(R.mipmap.outer_urbm);
                }
                flagList[7] = true;
                break;
            case 8:
                inner_ulbi.setImageResource(R.mipmap.inner_ulbi);
                flagList[8] = true;
                break;
            case 9:
                inner_ufi.setImageResource(R.mipmap.inner_ufi);
                flagList[9] = true;
                break;
            case 10:
                inner_urbi.setImageResource(R.mipmap.inner_urbi);
                flagList[10] = true;
                break;
            case 11:
                if (flagList[14]) {
                    outer_dlb.setImageResource(R.mipmap.outer_dlb);
                } else {
                    outer_dlb.setImageResource(R.mipmap.outer_dlbo);
                }
                flagList[11] = true;
                break;
            case 12:
                outer_dfo.setImageResource(R.mipmap.outer_dfo);
                flagList[12] = true;
                break;
            case 13:
                if (flagList[15]) {
                    outer_drb.setImageResource(R.mipmap.outer_drb);
                } else {
                    outer_drb.setImageResource(R.mipmap.outer_drbo);
                }
                flagList[13] = true;
                break;
            case 14:
                if (flagList[11]) {
                    outer_dlb.setImageResource(R.mipmap.outer_dlb);
                } else {
                    outer_dlb.setImageResource(R.mipmap.outer_dlbm);
                }
                flagList[14] = true;
                break;
            case 15:
                if (flagList[13]) {
                    outer_drb.setImageResource(R.mipmap.outer_drb);
                } else {
                    outer_drb.setImageResource(R.mipmap.outer_drbm);
                }
                flagList[15] = true;
                break;
            case 16:
                inner_dlbi.setImageResource(R.mipmap.inner_dlbi);
                flagList[16] = true;
                break;
            case 17:
                inner_dfi.setImageResource(R.mipmap.inner_dfi);
                flagList[17] = true;
                break;
            case 18:
                inner_drbi.setImageResource(R.mipmap.inner_drbi);
                flagList[18] = true;
                break;
            default:
        }

    }

    final Handler handler = new Handler() {          // handle
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                time_seconds++;
            }
            super.handleMessage(msg);
        }
    };

    class MyThread extends Thread {      // thread

        @Override
        public void run() {
            while (isRecording) {
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
            switchPictures(msg.what + 1);
            Log.i(Constants.TAG, "getCurrentState_thread result :" + msg.what);
        }
    };

    class MyThread_getCurrentState extends Thread {      // thread

        @Override
        public void run() {
            while (isRecording) {
                Log.i(Constants.TAG, "getCurrentState_thread runing ...");
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
            Toast.makeText(DisplayActivity.this, "已结束", Toast.LENGTH_SHORT).show();
        }
    };

    class MyThread_getLatestScore extends Thread {      // thread

        @Override
        public void run() {
            Log.i(Constants.TAG, "getLastScore_thread runing ...");
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
