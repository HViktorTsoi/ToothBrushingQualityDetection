package tooth.activity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import tooth.util.Constants;
import cjh.recorder.R;
import tooth.util.ParseUtil;
import tooth.util.PositionButtonWrapper;
import weka.Constant;
import weka.MakeArffFile;
import weka.MakeDecision;

/**
 * Created by admin on 2017/10/13.
 * Edited By Cui jiahe 2017/8/4.
 */

public class DisplayActivity extends AppCompatActivity {

    private ImageView inner_dfi, inner_dlbi, inner_drbi, inner_ufi, inner_ulbi, inner_urbi;
    private ImageView outer_dfo, outer_dlb, outer_drb, outer_ufo, outer_ulb, outer_urb;

    private Button buttonStartDetect;
    private TextView txtPositionStatus;

    private boolean[] flagList = new boolean[19];

    int[] counterAtPosition = new int[Constant.WEKA_CLASSES.length + 1];
    double[] timeAtPosition = new double[Constant.WEKA_CLASSES.length + 1];
    boolean[] finishedFlags = new boolean[Constant.WEKA_CLASSES.length + 1];

    Vibrator vibrator = null;

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

    private int windowLength = 17288;//3000;
    private int overlapPercentage = 0;//3000;

    private double brushingTime = 0;
    private boolean isRecording = false;
    private Thread recordThreadExecutor;
    MakeDecision predictModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);

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


        buttonStartDetect = (Button) findViewById(R.id.audiorecorder);
        buttonStartDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    try {
                        isRecording = true;
                        recordThreadExecutor = new Thread(new RecordThread());
                        recordThreadExecutor.start();
                        brushingTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        brushingTime = System.currentTimeMillis() - brushingTime;
                        isRecording = false;
                        recordThreadExecutor.join();
                        buttonStartDetect.setText("开始检测");
                        buttonStartDetect.setBackgroundColor(getResources().getColor(R.color.light_green));
                        txtPositionStatus.setText("");
                        showResultActivity();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        txtPositionStatus = (TextView) findViewById(R.id.txtPositionStatus);
    }

    private void showResultActivity() {
        Intent intent = new Intent(DisplayActivity.this, BrushResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray(Constants.TOTAL_TIME, timeAtPosition);
        bundle.putBooleanArray(Constants.FINISHED, finishedFlags);
        bundle.putDouble(Constants.BRUSHING_TIME, brushingTime);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class RecordThread implements Runnable {
        @Override
        public void run() {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonStartDetect.setText("载入模型...");
                        buttonStartDetect.setBackgroundColor(getResources().getColor(R.color.blue));
                        buttonStartDetect.setClickable(false);
                    }
                });
                predictModel = new MakeDecision();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonStartDetect.setText("检测中...");
                        buttonStartDetect.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        buttonStartDetect.setClickable(true);
                    }
                });
                recordAndRecognize();
            } catch (Exception e) {
                Toast.makeText(DisplayActivity.this, "未找到模型文件!!!", Toast.LENGTH_SHORT).show();
                buttonStartDetect.setText("开始检测");
                buttonStartDetect.setBackgroundColor(getResources().getColor(R.color.light_green));
                e.printStackTrace();
            }
        }
    }

    private void countAndProcess(final int class_type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtPositionStatus.setText("实时: " + PositionButtonWrapper.labelList[class_type]);
            }
        });
        int threshold = 2;
        double maxTimeEachPosition = 3;
        if (counterAtPosition[class_type] >= threshold) {
            counterAtPosition[class_type]++;
            timeAtPosition[class_type] += counterAtPosition[class_type] * 0.5;
            counterAtPosition[class_type] = 0;
        } else {
            // 将其他类别置0
            for (int i = 0; i < counterAtPosition.length; ++i) {
                if (i != class_type) {
                    counterAtPosition[i] = 0;
                }
            }
            // 当前类别+1
            counterAtPosition[class_type]++;
        }
        if (timeAtPosition[class_type] >= maxTimeEachPosition) {
            // 如果达到指定的刷牙时间
            if (class_type >= 3 && !finishedFlags[class_type]) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchPictures(class_type);
                        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    }
                });
            }
            finishedFlags[class_type] = true;
        }
        for (int i = 0; i < counterAtPosition.length; ++i) {
            System.out.print(counterAtPosition[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < counterAtPosition.length; ++i) {
            System.out.print(timeAtPosition[i] + " ");
        }
    }

    private void recordAndRecognize() throws Exception {
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
                class_type = Integer.valueOf(Constant.WEKA_CLASSES[class_type]);
                this.countAndProcess(class_type);
                System.out.println("预测结果: " + class_type + " => " + PositionButtonWrapper.labelList[class_type]);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "正在处理未停止的录音进程", Toast.LENGTH_SHORT).show();
        System.out.println("停止录音");
        this.isRecording = false;
        if (this.recordThreadExecutor != null) {
            try {
                this.recordThreadExecutor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
