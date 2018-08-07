package weka;

/**
 * Created by qizhiping on 2017/10/15.
 */

public class Constant {
    public static final String FILE_PATH = "/sdcard/brushingrecord/data/";
    public static final String MODEL_PATH = "/sdcard/brushingrecord/model/drums_classifier.model";
    public static final String MFCC_TMP_PATH = "/sdcard/brushingrecord/mfcc_temp.wav";

    final static public String[] WEKA_ATTS = {"TMax", "TMin", "TMean", "TSTD", "TMed", "TQ1", "TQ3", "TIQR", "TSke", "FMean1", "FMean2",
            "FMean3", "FMean4", "FMean5", "FMean6", "FMean7", "FSD", "FMed", "FSke", "FIqr"};

    final static public String[] WEKA_CLASSES = {"1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12",
            "13", "14", "15", "16", "17", "18"};
}
