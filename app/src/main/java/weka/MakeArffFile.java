package weka;

import android.os.Debug;
import android.os.Process;
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
 * Edited by cuijiahe on 2018/7/23
 */

public class MakeArffFile {


    private static FileWriter writer;
    private static List<float[]> mfcc_ret_batch;
    private static double[] t_ret;
    private static double[] f_ret;

    public static void initArffFile(String filePath, double windowLength, int overlapPercentage) {
        try {
            File file = new File(filePath);
            boolean flag = true;
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
                System.out.println("创建了父文件夹");
            }
//            if (!file.exists()) {
////                file.delete();
//                FileOutputStream fos = new FileOutputStream(filePath, true);
//                flag = false;
//            }
            if (!file.exists()) {
                System.out.println("创建文件头");
                String relationName = String.format("%.1f_%d%%", windowLength, overlapPercentage);
                writer = new FileWriter(filePath, true);
                writer.write("@relation " + relationName + "\n");
                writer.write("\n");
                writer.write("@attribute TMax numeric\n");
                writer.write("@attribute TMin numeric\n");
                writer.write("@attribute TMean numeric\n");
                writer.write("@attribute TSTD numeric\n");
                writer.write("@attribute TMed numeric\n");
//                writer.write("@attribute Kur numeric\n");
                writer.write("@attribute TSke numeric\n");
                writer.write("@attribute TQ1 numeric\n");
                writer.write("@attribute TQ3 numeric\n");
                writer.write("@attribute TIQR numeric\n");
                writer.write("@attribute FMean1 numeric\n");
                writer.write("@attribute FMean2 numeric\n");
                writer.write("@attribute FMean3 numeric\n");
                writer.write("@attribute FMean4 numeric\n");
                writer.write("@attribute FMean5 numeric\n");
                writer.write("@attribute FMean6 numeric\n");
                writer.write("@attribute FMean7 numeric\n");
                writer.write("@attribute FSD numeric\n");
                writer.write("@attribute FMed numeric\n");
//                writer.write("@attribute FKur numeric\n");
                writer.write("@attribute FSke numeric\n");
                writer.write("@attribute FIqr numeric\n");
//                writer.write("@attribute MFCC1 numeric\n");
//                writer.write("@attribute MFCC2 numeric\n");
//                writer.write("@attribute MFCC3 numeric\n");
//                writer.write("@attribute MFCC4 numeric\n");
//                writer.write("@attribute MFCC5 numeric\n");
//                writer.write("@attribute MFCC6 numeric\n");
//                writer.write("@attribute MFCC7 numeric\n");
//                writer.write("@attribute MFCC8 numeric\n");
//                writer.write("@attribute MFCC9 numeric\n");
//                writer.write("@attribute MFCC10 numeric\n");
//                writer.write("@attribute MFCC11 numeric\n");
//                writer.write("@attribute MFCC12 numeric\n");
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

    public static void calculate(
            double[] numericalDatalist,
            final List<Byte> rawDatalist,
            String type,
            String dataSetName,
            final int sampleRateInHz,
            final int channelConfig,
            double windowLength,
            int overlapPercentage
    ) {
        // 初始化文件
        String dataSetPath = Constant.FILE_PATH + dataSetName;
        initArffFile(dataSetPath, windowLength / sampleRateInHz, overlapPercentage);
        final double[] param = numericalDatalist;
//        final double[] param = new double[numericalDatalist.size()];
//        for (int i = 0; i < numericalDatalist.size(); i++)
//            param[i] = Double.valueOf(numericalDatalist.get(i));
//        Thread t_td_fd = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
//                t_ret = AudioFeature.timedomain(param);
//                f_ret = AudioFeature.freqdomain(param);
//            }
//        });
//        Thread t_mfcc = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
//                mfcc_ret_batch = AudioFeature.mfccFeature(rawDatalist, sampleRateInHz, channelConfig);
//            }
//        });
//        t_td_fd.start();
//        t_mfcc.start();
//        try {
//            t_td_fd.join();
//            t_mfcc.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        t_ret = AudioFeature.timedomain(param);
        f_ret = AudioFeature.freqdomain(param);
//        mfcc_ret_batch = AudioFeature.mfccFeature(rawDatalist, sampleRateInHz, channelConfig);
        try {
            writer = new FileWriter(dataSetPath, true);
            for (double aT_ret : t_ret) {
                writer.write(String.valueOf(aT_ret) + ",");
            }
            for (double aF_ret : f_ret) {
                writer.write(String.valueOf(aF_ret) + ",");
            }
            // 如果有mfcc属性 则写入
//            if (mfcc_ret_batch != null && mfcc_ret_batch.size() > 0) {
//                // 由于窗口是自己实现好的 所以只需要写入mfccList的第一维 即当前窗口的mfcc
//                float[] mfcc_ret = mfcc_ret_batch.get(0);
//                for (float aMfcc_ret : mfcc_ret) {
//                    writer.write(String.valueOf(aMfcc_ret) + ",");
//                }
//            }
            writer.write(type);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
