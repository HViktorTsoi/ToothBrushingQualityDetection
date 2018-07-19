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

    public static void initArffFile () {
        try {
            File file = new File(Constant.FILE_PATH);
            boolean flag = true;
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(Constant.FILE_PATH, true);
                flag = false;
            }
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
                writer.write("@attribute class {18,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17}\n");
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
        for(int i=0; i<datalist.size(); i++)
            param[i] = Double.valueOf(datalist.get(i));
        double[] t_ret = AudioFeature.timedomain(param);
        double[] f_ret = AudioFeature.freqdomain(param);
        List<float[]> mfcc_ret=AudioFeature.mfccFeature(datalist,sampleRateInHz,channelConfig);
        //String tmp = "";
        try {
            writer = new FileWriter(Constant.FILE_PATH, true);
            for (int i=0; i<t_ret.length; i++) {
                writer.write( String.valueOf(t_ret[i]) + ",");
                //tmp = tmp + String.valueOf(t_ret[i]) + ",";
            }
            for (int i=0; i<f_ret.length; i++) {
                writer.write(String.valueOf(f_ret[i]) + ",");
                //tmp = tmp + String.valueOf(f_ret[i]) + ",";
            }
            writer.write(type);
            writer.write("\n");
            writer.close();
            //tmp = tmp + type + "\n";
            //_record.add(tmp);
            //writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeToFile() {
        if (_record.size() > 20) {
            try {
                writer = new FileWriter(Constant.FILE_PATH, true);
                for(String str:_record) {
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
