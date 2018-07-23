package tooth.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import cjh.recorder.R;
import tooth.util.PositionButtonWrapper;
import tooth.util.noise.SpectralSubtraction;
import weka.Constant;
import weka.MakeArffFile;

/**
 * Created by admin on 2017/10/12.
 * Edited by Cui Jiahe 2018/07/16
 */

public class CollectionActivity extends AppCompatActivity implements View.OnClickListener {

    // UI配置
    // 所有的位置button信息的map
    private HashMap<Integer, PositionButtonWrapper> positionClassButtonMap = new HashMap<>();
    private Button button_test;
    private MenuItem item1, item2, item3, item4;
    private SeekBar sbAdjWindowSize, sbAdjOverlapPercentage;
    private TextView txtWindowLength, txtCurOverlapPercentage;
    private String TAG = "ToothRecord";
    private AudioRecord mRecorder = null;
    private ProgressDialog waitingDialog;
    // 正在记录的位置类型
    private int recordFlag = 0;
    public boolean isRecording = false;

    // 数据采集配置
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
    // 最大可调整窗口长度
    final static double maxWindowLengthInSecond = 0.5;
    private int windowLength = (int) ((double) sampleRateInHz * maxWindowLengthInSecond) / 5;//0.1s;
    // 步长
    private int overlapPercentage = 50;
    // 噪声消除器
    SpectralSubtraction spectralSubstraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_layout);
        /*
         ** By Y4
         *  初始化文件存储路径
         */
        File file = new File(Constant.FILE_PATH);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        file = new File(Constant.MODEL_PATH);
        fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        MakeArffFile.initArffFile();

        initialize();

        new AndroidFFMPEGLocator(this);
    }

    /*
     *
     * 初始化视图绑定
     * */
    private void initialize() {
        int[] buttonIDs = {
                R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8,
                R.id.button9, R.id.button10, R.id.button11, R.id.button12,
                R.id.button13, R.id.button14, R.id.button15, R.id.button16,
                R.id.button17, R.id.button18
        };
//         注册口腔位置的一系列button 并用map存储
        for (int buttonID : buttonIDs) {
            // 将button通过wrapper放入map中
            positionClassButtonMap.put(buttonID,
                    new PositionButtonWrapper((Button) findViewById(buttonID), positionClassButtonMap.size() + 1));
            positionClassButtonMap.get(buttonID).getButton().setOnClickListener(this);
        }
        button_test = (Button) findViewById(R.id.button_test);
        assert button_test != null;
        button_test.setOnClickListener(this);
//        初始化seekbar 调整窗口大小
        txtWindowLength = (TextView) findViewById(R.id.txtCurWindowSize);
        sbAdjWindowSize = (SeekBar) findViewById(R.id.AdjWindowSize);
        assert sbAdjWindowSize != null;
        // 初始化窗口大小
        int initProgress = (int) ((double) windowLength / (double) sampleRateInHz / maxWindowLengthInSecond * 100);
        sbAdjWindowSize.setProgress(initProgress);
        txtWindowLength.setText(String.format("%.2fs", progressToWindowSizeInSecond(initProgress)));
        sbAdjWindowSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                txtWindowLength.setText(String.format("%.2fs", progressToWindowSizeInSecond(progress)));
                // 计算并设置窗口大小
                windowLength = (int) (sampleRateInHz * progressToWindowSizeInSecond(progress));
//                保证windowLength的值为偶数
                windowLength = windowLength % 2 == 0 ? windowLength : windowLength + 1;
                System.out.println(windowLength);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbAdjOverlapPercentage = (SeekBar) findViewById(R.id.AdjOverlapPecentage);
        sbAdjOverlapPercentage.setProgress(overlapPercentage);
        txtCurOverlapPercentage = (TextView) findViewById(R.id.txtCurOverlapPecentage);
        txtCurOverlapPercentage.setText(String.format("%d%%", overlapPercentage));
        sbAdjOverlapPercentage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                overlapPercentage = progress;
                txtCurOverlapPercentage.setText(String.format("%d%%", overlapPercentage));
                System.out.println(overlapPercentage);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private double progressToWindowSizeInSecond(double progress) {
        return (progress + 1) / 100 * maxWindowLengthInSecond;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_test) {
            if (recordFlag == 0) {
                Intent intent = new Intent(CollectionActivity.this, DisplayActivity.class);
                startActivity(intent);
            }
        } else {
            // 获取当前点击的button信息
            PositionButtonWrapper positionButtonWrapper = positionClassButtonMap.get(v.getId());
            Button currentButton = positionButtonWrapper.getButton();
            // 如果未开始录制 则开始采集对应类别的信息 否则停止
            if (recordFlag == 0) {
                currentButton.setText("停止");
                currentButton.setBackgroundColor(getResources().getColor(R.color.light_green));
                sbAdjWindowSize.setEnabled(false);
                recordFlag = positionButtonWrapper.getButtonLogicID();
                startRecording();
            } else if (recordFlag == positionButtonWrapper.getButtonLogicID()) {
                sbAdjWindowSize.setEnabled(true);
                currentButton.setText(positionButtonWrapper.getLabel());
                currentButton.setBackgroundColor(getResources().getColor(R.color.gray));
                stopRecording();
                recordFlag = 0;
            }
        }
    }

    public void startRecording() {
        isRecording = true;
        Log.i(TAG, "startRecording");
        // 开始录音,计算并写入
        new Thread(new RecordThread()).start();
    }

    public void stopRecording() {
        isRecording = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.i(TAG, "stopRecording");
    }

    class RecordThread implements Runnable {
        @Override
        public void run() {
            // 录制并计算噪声特征
            recordAndCalcNoiseFeat();
            // 录制正常声信号
            recordAndProcessData();
            System.out.println("Data Written.");
        }
    }

    class NotifyNoiseRemainTimeThread implements Runnable {

        private Double remainTime;

        public NotifyNoiseRemainTimeThread(Double remainTime) {
            this.remainTime = remainTime;
        }

        @Override
        public void run() {
            waitingDialog.setMessage(String.format("剩余时间: %.1fs", remainTime));
        }
    }

    /*
     * 计算噪声特征并初始化噪声消除类
     * */
    private void recordAndCalcNoiseFeat() {
        final byte[] inputSignal = new byte[bufferSizeInBytes];
        List<Short> noiseSignal = new ArrayList<>();
        // 噪声信号采集时长
        int noiseLengthInSecond = 10;
        // 读取噪声信号 读取 sampleRateInHz/2*n秒的数据
        // 设置等待窗口
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waitingDialog = new ProgressDialog(CollectionActivity.this);
                waitingDialog.setTitle("请保持安静,正在采集噪声数据");
                waitingDialog.setMessage("请等待...");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();
            }
        });
        // 实例化录音
        mRecorder = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        mRecorder.startRecording();
        while (noiseSignal.size() < sampleRateInHz * noiseLengthInSecond) {
            final int readsize = mRecorder.read(inputSignal, 0, bufferSizeInBytes);
            assert readsize % 2 == 0;
            // 计算真实数值
            for (int i = 0; i < readsize; i += 2) {
                noiseSignal.add((short) rawAudioDataToShort(inputSignal[i], inputSignal[i + 1]));
            }
            System.out.println(noiseSignal.size() / readsize);
            if (noiseSignal.size() / readsize % 10 == 0) {
                // 在界面上显示噪声录制的进度
                double progrss = noiseLengthInSecond * (1 - (double) noiseSignal.size() / (sampleRateInHz * noiseLengthInSecond));
                progrss = progrss < 0.1 ? 0.1 : progrss;
                runOnUiThread(new NotifyNoiseRemainTimeThread(progrss));
            }
        }
//        mRecorder.stop();
//        mRecorder.release();
        System.out.println(noiseSignal.size());
        spectralSubstraction = new SpectralSubtraction(noiseSignal, 1024, noiseLengthInSecond * sampleRateInHz / 1024 / 2);
        waitingDialog.dismiss();
        // 重置录音机
        System.out.println("噪声记录完毕");
    }


    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private void recordAndProcessData() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        final byte[] inputSignal = new byte[bufferSizeInBytes];
        ArrayList<Byte> signalBuffer = new ArrayList<>();
        ArrayList<Byte> totalSignal = new ArrayList<>();
        System.out.println("windowLength:" + windowLength);
//        mRecorder = new AudioRecord(audioSource, sampleRateInHz,
//                channelConfig, audioFormat, bufferSizeInBytes * 10);
//        mRecorder.startRecording();
        // 舍弃前面5帧率
        int ignoreFrameCounter = 10;
        while (isRecording) {
            final int readsize = mRecorder.read(inputSignal, 0, bufferSizeInBytes);
            if (ignoreFrameCounter > 0) {
                ignoreFrameCounter--;
                continue;
            }
            for (byte anInputSignal : inputSignal) {
                signalBuffer.add(anInputSignal);
                totalSignal.add(anInputSignal);
            }
            // 消费若干个窗口
            while (signalBuffer.size() > windowLength) {
                List<Byte> rawDatalist = signalBuffer.subList(0, windowLength);
                List<Integer> numericalDatalist = new ArrayList<>();
                // 将原始数据转换成数值数据
                for (int i = 0; i < rawDatalist.size(); i += 2) {
                    int sig = rawAudioDataToShort(rawDatalist.get(i), rawDatalist.get(i + 1));
                    numericalDatalist.add(sig);
                }
                MakeArffFile.calculate(numericalDatalist, rawDatalist, String.valueOf(recordFlag), sampleRateInHz, channelConfig);
                // 此处考虑滑动窗口的overlap 每次只除去窗口的1-overlapPercentage部分
                for (int i = 0; i < (double) windowLength * (1 - (double) overlapPercentage / 100); i++) {
                    signalBuffer.remove(0);
                }
                System.out.println(".");
            }
        }

        // 发送请求
        try {

            // short类型的signal
            List<Short> signal_16bit = new ArrayList<>();
            // 原始信号
            JSONArray sigSeq = new JSONArray();
            for (int i = 0; i < totalSignal.size(); i += 2) {
                int sig = rawAudioDataToShort(totalSignal.get(i), totalSignal.get(i + 1));
                signal_16bit.add((short) sig);
                sigSeq.put(sig);
            }
            // 降噪之后的信号
//            SpectralSubtraction spectralSubstractionTest = new SpectralSubtraction(signal_16bit, 1024, 30);
            this.spectralSubstraction.setSignal(signal_16bit);
            short[] signal_16bit_denoise = spectralSubstraction.noiseSubtraction();
            JSONArray sigSeq_denoise = new JSONArray();
            for (short sig : signal_16bit_denoise) {
                sigSeq_denoise.put(sig);
            }
            byte[] buf = ("audio=" + sigSeq.toString() + "&audio_denoise=" + sigSeq_denoise.toString()).getBytes();
//            URL url = new URL("http://192.168.1.103:5000/audio");
            URL url = new URL("http://192.168.1.101:5000/audio");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            out.write(buf);
            out.close();
            int responseCode = con.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 将raw数据中的二进制转换为实际的音频信号 由于默认音频格式为16bit 小端存储 因此这里只使用high和low两位
     * 如果用其他bit宽度需要修改此函数
     *
     * */
    private int rawAudioDataToShort(byte high, byte low) {
        return (short) (low * 256) + (short) high;
    }
}
