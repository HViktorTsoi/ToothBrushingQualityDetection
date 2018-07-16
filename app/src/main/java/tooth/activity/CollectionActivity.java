package tooth.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cjh.recorder.R;
import weka.Constant;
import weka.MakeArffFile;

/**
 * Created by admin on 2017/10/12.
 * Edited by Cui Jiahe 2018/07/16
 */

public class CollectionActivity extends AppCompatActivity implements View.OnClickListener {

    public static CollectionActivity instance;

    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14, button15, button16;
    private Button button17, button18;
    private Button button_test;
    private MenuItem item1, item2, item3, item4;

    private int recordFlag = 0;

    File extDir = Environment.getExternalStorageDirectory();
    public static final String AUDIORECORDER = "audiorecorder";
    private String filePath;

    private String TAG = "BluetoothRecord";
    private static String mFileName = null;
    private AudioRecord mRecorder = null;
    private AudioManager mAudioManager = null;

    public boolean recordering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_layout);
        /*
         ** By Y4
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

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        initialize();
    }

    private void initialize() {
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(this);
        button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
        button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(this);
        button9 = (Button) findViewById(R.id.button9);
        button9.setOnClickListener(this);
        button10 = (Button) findViewById(R.id.button10);
        button10.setOnClickListener(this);
        button11 = (Button) findViewById(R.id.button11);
        button11.setOnClickListener(this);
        button12 = (Button) findViewById(R.id.button12);
        button12.setOnClickListener(this);
        button13 = (Button) findViewById(R.id.button13);
        button13.setOnClickListener(this);
        button14 = (Button) findViewById(R.id.button14);
        button14.setOnClickListener(this);
        button15 = (Button) findViewById(R.id.button15);
        button15.setOnClickListener(this);
        button16 = (Button) findViewById(R.id.button16);
        button16.setOnClickListener(this);
        button17 = (Button) findViewById(R.id.button17);
        button17.setOnClickListener(this);
        button18 = (Button) findViewById(R.id.button18);
        button18.setOnClickListener(this);
        button_test = (Button) findViewById(R.id.button_test);
        button_test.setOnClickListener(this);

    }


    private static String recordTyp = "";
    private static boolean mic_status = false;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                if (recordFlag == 0) {
                    recordFlag = 1;
                    recordering = true;
                    button1.setText("停止");
                    button1.setBackgroundColor(getResources().getColor(R.color.light_green));
                    button1.setBackgroundColor(getResources().getColor(R.color.gray));
                    this.recordTyp = "1"; // = "Gargling";
                    startRecording();
                } else if (recordFlag == 1) {
                    recordFlag = 0;
                    recordering = false;
                    this.recordTyp = "1"; // = "None";
                    button1.setText("无");
                    stopRecording();
                }
                break;
            case R.id.button2:
                if (recordFlag == 0) {
                    recordFlag = 2;
                    //recordering = true;
                    button2.setText("停止");
                    button2.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "1"; // = "Gargling";
                    //startRecording();
                } else if (recordFlag == 2) {
                    recordFlag = 0;
                    //recordering = false;
                    this.recordTyp = "18";
                    button2.setText("漱口");
                    button2.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button3:
                if (recordFlag == 0) {
                    recordFlag = 3;
                    //recordering = true;
                    button3.setText("停止");
                    button3.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "2"; // = "ULBO";
                    //startRecording();
                } else if (recordFlag == 3) {
                    recordFlag = 0;
                    //recordering = false;
                    this.recordTyp = "18";
                    button3.setText("上左后外");
                    button3.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button4:
                if (recordFlag == 0) {
                    recordFlag = 4;
                    //recordering = true;
                    button4.setText("停止");
                    button4.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "3"; // = "UFO";
                    //startRecording();
                } else if (recordFlag == 4) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button4.setText("上前外");
                    button4.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button5:
                if (recordFlag == 0) {
                    recordFlag = 5;
                    //recordering = true;
                    button5.setText("停止");
                    button5.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "4"; // = "URBO";
                    //startRecording();
                } else if (recordFlag == 5) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button5.setText("上右后外");
                    button5.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button6:
                if (recordFlag == 0) {
                    recordFlag = 6;
                    //recordering = true;
                    button6.setText("停止");
                    button6.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "5"; // = "ULBM";
                    //startRecording();
                } else if (recordFlag == 6) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button6.setText("上左后中");
                    button6.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button7:
                if (recordFlag == 0) {
                    recordFlag = 7;
                    //recordering = true;
                    button7.setText("停止");
                    button7.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "6"; // = "URBM";
                    //startRecording();
                } else if (recordFlag == 7) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button7.setText("上右后中");
                    button7.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button8:
                if (recordFlag == 0) {
                    recordFlag = 8;
                    //recordering = true;
                    button8.setText("停止");
                    button8.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "7"; // = "ULBI";
                    //startRecording();
                } else if (recordFlag == 8) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button8.setText("上左后内");
                    button8.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button9:
                if (recordFlag == 0) {
                    recordFlag = 9;
                    //recordering = true;
                    button9.setText("停止");
                    button9.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "8"; // = "UFI";
                    //startRecording();
                } else if (recordFlag == 9) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button9.setText("上前内");
                    button9.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button10:
                if (recordFlag == 0) {
                    recordFlag = 10;
                    //recordering = true;
                    button10.setText("停止");
                    button10.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "9"; // = "URBI";
                    //startRecording();
                } else if (recordFlag == 10) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button10.setText("上右后内");
                    button10.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button11:
                if (recordFlag == 0) {
                    recordFlag = 11;
                    //recordering = true;
                    button11.setText("停止");
                    button11.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "10"; // = "DLBO";
                    //startRecording();
                } else if (recordFlag == 11) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button11.setText("下左后外");
                    button11.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button12:
                if (recordFlag == 0) {
                    recordFlag = 12;
                    //recordering = true;
                    button12.setText("停止");
                    button12.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "11"; // = "DFO";
                    //startRecording();
                } else if (recordFlag == 12) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button12.setText("下前外");
                    button12.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button13:
                if (recordFlag == 0) {
                    recordFlag = 13;
                    //recordering = true;
                    button13.setText("停止");
                    button13.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "12"; // = "DRBO";
                    //startRecording();
                } else if (recordFlag == 13) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button13.setText("下右后外");
                    button13.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button14:
                if (recordFlag == 0) {
                    recordFlag = 14;
                    //recordering = true;
                    button14.setText("停止");
                    button14.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "13"; // = "DLBM";
                    //startRecording();
                } else if (recordFlag == 14) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button14.setText("下左后中");
                    button14.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button15:
                if (recordFlag == 0) {
                    recordFlag = 15;
                    //recordering = true;
                    button15.setText("停止");
                    button15.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "14"; // = "DRBM";
                    //startRecording();
                } else if (recordFlag == 15) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button15.setText("下右后中");
                    button15.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button16:
                if (recordFlag == 0) {
                    recordFlag = 16;
                    //recordering = true;
                    button16.setText("停止");
                    button16.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "15"; // = "DLBI";
                    //startRecording();
                } else if (recordFlag == 16) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button16.setText("下左后内");
                    button16.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button17:
                if (recordFlag == 0) {
                    recordFlag = 17;
                    //recordering = true;
                    button17.setText("停止");
                    button17.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "16"; // = "DFI";
                    //startRecording();
                } else if (recordFlag == 17) {
                    recordFlag = 0;
                    this.recordTyp = "18";
                    //recordering = false;
                    button17.setText("下前内");
                    button17.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button18:
                if (recordFlag == 0) {
                    recordFlag = 18;
                    //recordering = true;
                    button18.setText("停止");
                    button18.setBackgroundColor(getResources().getColor(R.color.light_green));
                    //Todo:startRecording
                    this.recordTyp = "17"; // = "DRBI";
                    //startRecording();
                } else if (recordFlag == 18) {
                    recordFlag = 0;
                    //recordering = false;
                    this.recordTyp = "18";
                    button18.setText("下右后内");
                    button18.setBackgroundColor(getResources().getColor(R.color.gray));
                    //Todo:stopRecording
                    //stopRecording();
                }
                break;
            case R.id.button_test:
                if (recordFlag == 0) {
                    Intent intent = new Intent(CollectionActivity.this, DisplayActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                recordFlag = 0;
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

    public void startRecording() {

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";
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
            dataOutput();
            System.out.println("Data Written.");
        }
    }


    private void dataOutput() {
        /*while(data.size() > windowLength){
            List<Byte> datalist = data.subList(0,windowLength);
            System.out.println("windowLength:"+windowLength);
            System.out.println("datalistsize:"+datalist.size());
            for(int i=0;i<datalist.size();i++){
                System.out.println("data"+i+": "+data.get(i));
                System.out.println("datalist"+i+": "+datalist.get(i));

                *//*
         ** By Y4
         *//*
                MakeArffFile.calculate(datalist, recordTyp);
            }
            for(int i=0;i<windowLength/2;i++){
                data.remove(0);
            }
        }*/
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
        while (recordering) {

            final int readsize = mRecorder.read(inputSignal, 0, bufferSizeInBytes);
            System.out.println("audioData:" + inputSignal.toString());

            for (byte anInputSignal : inputSignal) {
                signalBuffer.add(anInputSignal);
            }

            while (signalBuffer.size() > windowLength) {
                List<Byte> datalist = signalBuffer.subList(0, windowLength);
                System.out.println("windowLength:" + windowLength);
                System.out.println("dataListSize:" + datalist.size());
                MakeArffFile.calculate(datalist, recordTyp);
                for (int i = 0; i < windowLength; i++) {
                    signalBuffer.remove(0);
                }
            }

        }
    }
}
