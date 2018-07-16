package cjh.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import tooth.util.Constants;

/**
 * Created by chenjiahuan on 16/5/8.
 */
public class AudioRecorderUtil {

    private static Object mLock = new Object();

    private static boolean first = true;

    private static AudioRecord audioRecord;

    private static String currentFilePath;

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

    public static void startRecordering(String filePath) {
        first = true;
        currentFilePath = filePath;
        audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        /*if(Constants.flag2){
            audioRecord.setPreferredDevice(Constants.audioDeviceInfo_Blu);
        }*/
        if(Constants.audioDeviceInfo_Mic!=null){
            System.out.println("Mic type:"+Constants.audioDeviceInfo_Mic.getType());

            audioRecord.setPreferredDevice(Constants.audioDeviceInfo_Mic);
        }
        audioRecord.startRecording();
        Constants.audioDeviceInfo_Mic = audioRecord.getRoutedDevice();
        System.out.println("audioDeviceInfo Mic:" + Constants.audioDeviceInfo_Mic.getType());
        new Thread(new AudioRecordThread()).start();
    }

    public static void stopRecording() {
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;

            } catch (RuntimeException e) {
            }
        }
    }

    static class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            System.out.println("audio thread");
            writeDateTOFile();//往文件中写入裸数据
            copyWaveFile(currentFilePath, currentFilePath.replace(".pcm", ".wav"));//给裸数据加上头文件
        }
    }

    // 这里得到可播放的音频文¬件
    private static void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = sampleRateInHz;
        int channels = 2;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
    private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                            long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = test1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private static void writeDateTOFile() {
        System.out.println("audio writeDataToFile");
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        final byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        try {
            File file = new File(currentFilePath);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("BufferSizeInBytes:"+bufferSizeInBytes);

        while (MainActivity.instance.recordering == true) {
            final int readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            System.out.println("mic readsize:"+readsize);
            System.out.println("mic adudioData:"+audiodata.toString());
            for(int i =0;i<audiodata.length;i++){
                System.out.println("audiodata"+i+":"+audiodata[i]);
            }
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*Timer timer = new Timer();
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
                }, 0, 1000);
            first = false;*/

        }
        try {
            fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
