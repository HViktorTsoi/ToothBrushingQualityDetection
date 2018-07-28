package weka;

/**
 * Created by qizhiping on 2017/10/15.
 */

public class Constant {
    public static final String FILE_PATH = "/sdcard/brushingrecord/data/data.arff";
    public static final String MODEL_PATH = "/sdcard/brushingrecord/model/tree.model";
    public static final String MFCC_TMP_PATH = "/sdcard/brushingrecord/mfcc_temp.wav";

    final static public String[] WEKA_ATTS = { "bMax", "bMin", "bMean", "bSTD", "bMed", "bKur", "bSke", "bQ1", "bQ3", "bIQR", "bFMean1", "bFMean2",
            "bFMean3", "bFMean4", "bFMean5", "bFMean6", "bFMean7", "bFSD", "bFMed", "bFKur", "bFSke",
            "bFIqr"};

    final static public String[] WEKA_CLASSES = { "1", "2", "3", "4", "5", "6",
                                                  "7", "8", "9", "10", "11", "12",
                                                  "13", "14", "15", "16", "17", "18"};
}
