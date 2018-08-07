package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tooth.util.ParseUtil;
import weka.AudioFeature;
import weka.Constant;

public class TestFeatureCalc {
    public static void main(String[] args) throws IOException {
        int windowLength = 44100 / 2;
        double overlapPercentage = 0.5;
        // 初始化arff文件
        String filePath = "/tmp/sample.arff";
        File arffFile = new File("/tmp/sample.arff");
        FileWriter writer = new FileWriter(filePath);
        arffFile.createNewFile();
        writer.write("@relation " + "0.5_50%" + "\n");
        writer.write("\n");
        for (String attrName : Constant.WEKA_ATTS) {
            writer.write("@attribute " + attrName + " numeric\n");
        }
        // 分类目标
        writer.write("@attribute class {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}\n");
        writer.write("\n");
        writer.write("@data\n");
        for (int classID = 1; classID <= 18; ++classID) {
//            if (!(classID == 1 || classID == 2 || classID == 3 || classID == 7 || classID == 8)) {
//                continue;
//            }
            File audioFile = new File(String.format("/home/hviktortsoi/Code/AndroidRecorder/guide/sample/data/raw/%d.pcm", classID));
            InputStream inputStream = new FileInputStream(audioFile);
            int signalLength = (int) audioFile.length();
            byte[] signal = new byte[signalLength];
            inputStream.read(signal);
            inputStream.close();
            List<Double> numericalDatalist = new ArrayList<>();
//             将原始数据转换成double型数值数据 为了防止rawdata长度是奇数 需要判断时i+1
            for (int i = 0; i + 1 < signal.length; i += 2) {
                int sig = ParseUtil.rawAudioDataToShort(signal[i], signal[i + 1]);
                numericalDatalist.add((double) sig);
            }
            List<Integer> windowStartPositions = new ArrayList<>();
            int windowStart = 0;
            while (windowStart + windowLength < numericalDatalist.size()) {
                windowStartPositions.add(windowStart);
                windowStart += windowLength * (1 - overlapPercentage);
            }
            for (int idx = 0; idx < windowStartPositions.size(); ++idx) {
                int startIndex = windowStartPositions.get(idx);
                System.out.println(classID + " = 正在处理" + startIndex + " - " + (startIndex + windowLength) + "的数据");
                List<Double> aWindow = numericalDatalist.subList(startIndex, startIndex + windowLength);
                double[] aWindowNative = new double[aWindow.size()];
                for (int i = 0; i < aWindow.size(); ++i) {
                    aWindowNative[i] = aWindow.get(i);
                }
                double[] t_ret = AudioFeature.timedomain(aWindowNative);
                double[] f_ret = AudioFeature.freqdomain(aWindowNative);
                for (double aT_ret : t_ret) {
                    writer.write(String.format("%.5f,", aT_ret));
                }
                for (double aF_ret : f_ret) {
                    writer.write(String.format("%.5f,", aF_ret));
                }
                writer.write(String.valueOf(classID));
                writer.write("\n");
            }
        }
        writer.close();
    }
}
