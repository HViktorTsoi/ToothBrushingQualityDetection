package tooth.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cjh.recorder.R;
import tooth.util.PositionButtonWrapper;
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

    private String TAG = "ToothRecord";
    private AudioRecord mRecorder = null;
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

    private int windowLength = sampleRateInHz / 2;//3000;

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
    }

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
        assert button_test != null;
        button_test = (Button) findViewById(R.id.button_test);
        button_test.setOnClickListener(this);
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
                recordFlag = positionButtonWrapper.getButtonLogicID();
                isRecording = true;
                currentButton.setText("停止");
                currentButton.setBackgroundColor(getResources().getColor(R.color.light_green));
                startRecording();
            } else if (recordFlag == positionButtonWrapper.getButtonLogicID()) {
                recordFlag = 0;
                isRecording = false;
                currentButton.setText(positionButtonWrapper.getLabel());
                currentButton.setBackgroundColor(getResources().getColor(R.color.gray));
                stopRecording();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater in = getMenuInflater();
        in.inflate(R.menu.menu_layout, menu);*/
        super.onCreateOptionsMenu(menu);
        int group = 1;
        item1 = menu.add(group, 1, 1, "0.1");
        item2 = menu.add(group, 2, 2, "0.2");
        item3 = menu.add(group, 3, 3, "0.5");
        item4 = menu.add(group, 4, 4, "0.8");

        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        int option = userSettings.getInt("option", 1);
        menu.setGroupCheckable(group, true, true);
        switch (option) {
            case 1:
                item1.setChecked(true);
                break;
            case 2:
                item2.setChecked(true);
                break;
            case 3:
                item3.setChecked(true);
                break;
            case 4:
                item4.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                setOption(1);
                item1.setChecked(true);
                break;
            case 2:
                setOption(2);
                item2.setChecked(true);
                break;
            case 3:
                setOption(3);
                item3.setChecked(true);
                break;
            case 4:
                setOption(4);
                item4.setChecked(true);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setOption(int i) {
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putInt("option", i);
        editor.commit();
    }

    public void startRecording() {
        // 实例化录音
        mRecorder = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        // 开始录音,计算并写入
        new Thread(new RecordThread()).start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.i(TAG, "stopRecording");
    }

    class RecordThread implements Runnable {
        @Override
        public void run() {
            mRecorder.startRecording();
            recordAndProcessData();
            System.out.println("Data Written.");
        }
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
        while (isRecording) {
            final int readsize = mRecorder.read(inputSignal, 0, bufferSizeInBytes);
            System.out.println("audioData:" + inputSignal.toString());

            for (byte anInputSignal : inputSignal) {
                signalBuffer.add(anInputSignal);
            }
            // 消费若干个窗口
            while (signalBuffer.size() > windowLength) {
                List<Byte> datalist = signalBuffer.subList(0, windowLength);
                System.out.println("windowLength:" + windowLength);
                System.out.println("dataListSize:" + datalist.size());
                MakeArffFile.calculate(datalist, String.valueOf(recordFlag));
                for (int i = 0; i < windowLength; i++) {
                    signalBuffer.remove(0);
                }
            }
        }
    }
}
