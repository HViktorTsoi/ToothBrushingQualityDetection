package weka;

/**
 * Created by qizhiping on 2017/10/15.
 */

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.*;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.mfcc.MFCC;
import tooth.util.wav.WavFileWriter;

import static weka.Constant.MFCC_TMP_PATH;

public class AudioFeature {

    public static double[] timedomain(double[] rawdata) {
        double max = StatUtils.max(rawdata);
        double min = StatUtils.min(rawdata);
        Mean meancal = new Mean();
        double mean = meancal.evaluate(rawdata);
        Variance var = new Variance();
        double std = var.evaluate(rawdata);
        std = FastMath.sqrt(std);
        Median rank = new Median();
        double med = rank.evaluate(rawdata, 50);
        double q1 = rank.evaluate(rawdata, 25);
        double q3 = rank.evaluate(rawdata, 75);
        double iqr = q3 - q1;
        Kurtosis mykur = new Kurtosis();
        double kur = mykur.evaluate(rawdata);
        Skewness myske = new Skewness();
        double ske = myske.evaluate(rawdata);
        double[] tdfeature = {max, min, mean, std, med, q1, q3, iqr, kur, ske};
        return tdfeature;
    }


    public static double[] freqdomain(double[] rawdata) {
        int[] available = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};
        int rawdatalength = rawdata.length;
        int endindex = 0;//这个fft只支持长度为2的n幂的长度的数组，向下取整截取数组。
        for (int i = 0; i < available.length; i++) {
            if (rawdatalength < available[i]) {
                endindex = available[i - 1];
                break;
            } else endindex = available[i];
        }
        //System.out.println(rawdatalength+" "+endindex);

        double[] newrawdata = new double[endindex];
        System.arraycopy(rawdata, 0, newrawdata, 0, endindex);//截取原数组的前endindex项
        //for(int i=0;i<endindex;i++)
        //System.out.println(rawdata[i]+" "+newrawdata[i]);

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] freqdata = fft.transform(newrawdata, TransformType.FORWARD);
        //FFT fft=new FFT();
        //System.out.println(freqdata.length);
        //for(int i=0;i<freqdata.length;i++)
        //System.out.println(freqdata[i]);
        //System.out.println(freqdata[0].getReal());
        double[] freqrawdata = new double[endindex];
        int l = (int) (endindex / 7);
        //System.out.println(endindex+" "+l);
        double[][] sub = new double[7][l];
        System.arraycopy(rawdata, 0, newrawdata, 0, endindex);

        for (int i = 0; i < endindex; i++) {
            freqrawdata[i] = freqdata[i].getReal();
        }

        for (int i = 0; i < 7; i++) {
            //System.out.println("index "+i+":  ");
            System.arraycopy(freqrawdata, i * l, sub[i], 0, l);
            //for(int j=0;j<l;j++)System.out.print(sub[i][j]+" ");
        }

        Mean meancal = new Mean();
        double fmean1 = meancal.evaluate(sub[0]);
        double fmean2 = meancal.evaluate(sub[1]);
        double fmean3 = meancal.evaluate(sub[2]);
        double fmean4 = meancal.evaluate(sub[3]);
        double fmean5 = meancal.evaluate(sub[4]);
        double fmean6 = meancal.evaluate(sub[5]);
        double fmean7 = meancal.evaluate(sub[6]);

        Variance var = new Variance();
        double fstd = var.evaluate(freqrawdata);
        fstd = FastMath.sqrt(fstd);

        Median rank = new Median();
        double fmed = rank.evaluate(freqrawdata, 50);
        double fiqr = rank.evaluate(freqrawdata, 75) - rank.evaluate(freqrawdata, 25);

        Kurtosis mykur = new Kurtosis();
        double fkur = mykur.evaluate(freqrawdata);

        Skewness myske = new Skewness();
        double fske = myske.evaluate(freqrawdata);

        double[] fdfeature = {fmean1, fmean2, fmean3, fmean4, fmean5, fmean6, fmean7, fstd, fmed, fkur, fske, fiqr};
        return fdfeature;
    }

    public static List<float[]> mfccFeature(List<Byte> datalist, int sampleRateInHz, int channelConfig) {
        // 存入文件
        WavFileWriter writer = new WavFileWriter();
        final List<float[]> mfccList = new ArrayList<>(200);
        try {
            writer.openFile(MFCC_TMP_PATH, sampleRateInHz, channelConfig, 16);
            // 转换到基础类型数组
            byte[] byteData = new byte[datalist.size()];
            for (int i = 0; i < datalist.size(); ++i) {
                byteData[i] = datalist.get(i);
            }
            writer.writeData(byteData, 0, datalist.size());
            writer.closeFile();
            // 计算mfcc特征
            InputStream inStream = new FileInputStream(MFCC_TMP_PATH);
            AudioDispatcher dispatcher = new AudioDispatcher(
                    new UniversalAudioInputStream(inStream, new TarsosDSPAudioFormat(sampleRateInHz, datalist.size(), 1, true, true)), datalist.size(), 100);
            final MFCC mfcc = new MFCC(datalist.size(), sampleRateInHz, 12, 50, 300, 3000);
            dispatcher.addAudioProcessor(mfcc);
            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public void processingFinished() {
                }

                @Override
                public boolean process(AudioEvent audioEvent) {
                    mfccList.add(mfcc.getMFCC());
                    return true;
                }
            });
            dispatcher.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mfccList;
    }

}
