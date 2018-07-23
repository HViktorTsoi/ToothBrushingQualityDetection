package weka;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by qizhiping on 2017/10/15.
 */

public class MakeArffFile {


    private static FileWriter writer;

    public static void initArffFile() {
        try {
            File file = new File(Constant.FILE_PATH);
            boolean flag = true;
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(Constant.FILE_PATH, true);
            flag = false;
            writer = new FileWriter(Constant.FILE_PATH, true);
            if (!flag) {
                writer.write("@relation mfcc_td_fd\n");
                writer.write("\n");
                writer.write("@attribute bMax numeric\n");
                writer.write("@attribute bMin numeric\n");
                writer.write("@attribute bMean numeric\n");
                writer.write("@attribute bSTD numeric\n");
                writer.write("@attribute bMed numeric\n");
                writer.write("@attribute bKur numeric\n");
                writer.write("@attribute bSke numeric\n");
                writer.write("@attribute bQ1 numeric\n");
                writer.write("@attribute bQ3 numeric\n");
                writer.write("@attribute bIQR numeric\n");
                writer.write("@attribute bFMean1 numeric\n");
                writer.write("@attribute bFMean2 numeric\n");
                writer.write("@attribute bFMean3 numeric\n");
                writer.write("@attribute bFMean4 numeric\n");
                writer.write("@attribute bFMean5 numeric\n");
                writer.write("@attribute bFMean6 numeric\n");
                writer.write("@attribute bFMean7 numeric\n");
                writer.write("@attribute bFSD numeric\n");
                writer.write("@attribute bFMed numeric\n");
                writer.write("@attribute bFKur numeric\n");
                writer.write("@attribute bFSke numeric\n");
                writer.write("@attribute bFIqr numeric\n");
                writer.write("@attribute MFCC1 numeric\n");
                writer.write("@attribute MFCC2 numeric\n");
                writer.write("@attribute MFCC3 numeric\n");
                writer.write("@attribute MFCC4 numeric\n");
                writer.write("@attribute MFCC5 numeric\n");
                writer.write("@attribute MFCC6 numeric\n");
                writer.write("@attribute MFCC7 numeric\n");
                writer.write("@attribute MFCC8 numeric\n");
                writer.write("@attribute MFCC9 numeric\n");
                writer.write("@attribute MFCC10 numeric\n");
                writer.write("@attribute MFCC11 numeric\n");
                writer.write("@attribute MFCC12 numeric\n");
                // 分类目标
                writer.write("@attribute class {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}\n");
                writer.write("\n");
                writer.write("@data\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static ArrayList<String> _record = new ArrayList<>();

    public static void calculate(List<Byte> datalist, String type, int sampleRateInHz, int channelConfig) {
        double[] param = new double[datalist.size()];
        for (int i = 0; i < datalist.size(); i++)
            param[i] = Double.valueOf(datalist.get(i));
        double[] t_ret = AudioFeature.timedomain(param);
        double[] f_ret = AudioFeature.freqdomain(param);
        List<float[]> mfcc_ret_batch = AudioFeature.mfccFeature(datalist, sampleRateInHz, channelConfig);

        //String tmp = "";
        try {
            writer = new FileWriter(Constant.FILE_PATH, true);
            for (double aT_ret : t_ret) {
                writer.write(String.valueOf(aT_ret) + ",");
            }
            for (double aF_ret : f_ret) {
                writer.write(String.valueOf(aF_ret) + ",");
            }
            // 如果有mfcc属性 则写入
            if (mfcc_ret_batch != null && mfcc_ret_batch.size() > 0) {
                // 由于窗口是自己实现好的 所以只需要写入mfccList的第一维 即当前窗口的mfcc
                float[] mfcc_ret = mfcc_ret_batch.get(0);
                for (float aMfcc_ret : mfcc_ret) {
                    writer.write(String.valueOf(aMfcc_ret) + ",");
                }
            }
            writer.write(type);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeToFile() {
        if (_record.size() > 20) {
            try {
                writer = new FileWriter(Constant.FILE_PATH, true);
                for (String str : _record) {
                    writer.write(str);
                }
                _record.clear();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
