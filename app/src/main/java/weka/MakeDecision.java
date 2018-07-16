package weka;

/**
 * Created by qizhiping on 2017/10/15.
 */

import java.io.File;

import weka.classifiers.misc.SerializedClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MakeDecision {

    private static FastVector datastructure;
    private static Instances header;
    private static SerializedClassifier currentmodel;

    /*
     * 调用方式
     * String[] attr = new String[35];
     *	attr[0] = String.valueOf(min(gz));
		attr[1] = String.valueOf(max(gz));
		attr[2] = String.valueOf(sd(gz));
		attr[3] = String.valueOf(var(gz));
		attr[4] = String.valueOf(mean(gz));
		attr[5] = String.valueOf(quartilesUp(gz));
		attr[6] = String.valueOf(quartilesDown(gz));
		attr[7] = String.valueOf(mode(gz));
		attr[8] = String.valueOf(min(gz));
		attr[9] = String.valueOf(max(gz));
		attr[10] = String.valueOf(sd(gz));
		attr[11] = String.valueOf(var(gz));
		attr[12] = String.valueOf(mean(gz));
		attr[13] = String.valueOf(quartilesUp(gz));
		attr[14] = String.valueOf(quartilesDown(gz));
		attr[15] = String.valueOf(mode(gz));
		attr[16] = String.valueOf(min(gz));
		attr[17] = String.valueOf(max(gz));
		attr[18] = String.valueOf(sd(gz));
		attr[19] = String.valueOf(var(gz));
		attr[20] = String.valueOf(mean(gz));
		attr[21] = String.valueOf(quartilesUp(gz));
		attr[22] = "?";
     * int class_type = MakeDecision.makedecision(attr);
     */
    public static int makedecision(String[] s) {
        int result_class = -1;
        try {
            init();
            insertdata(getelement(s, datastructure));
            result_class = (int) currentmodel.classifyInstance(header.firstInstance());
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result_class;
    }

    private static void init() throws Exception{
        datastructure = getstructure();
        header = getheader(datastructure, 2);
        currentmodel = readmodel(Constant.MODEL_PATH);
    }

    private static FastVector getstructure() {
        Attribute[] Atts = new Attribute[Constant.WEKA_ATTS.length];
        for (int i = 0; i < Constant.WEKA_ATTS.length; i++)
            Atts[i] = new Attribute(Constant.WEKA_ATTS[i]);

        FastVector ClassVal = new FastVector(Constant.WEKA_CLASSES.length);
        for (int i = 0; i < Constant.WEKA_CLASSES.length; i++)
            ClassVal.addElement(Constant.WEKA_CLASSES[i]);
        Attribute ClassAttribute = new Attribute("class", ClassVal);

        FastVector ds = new FastVector(23);
        for (int i = 0; i < Constant.WEKA_ATTS.length; i++)
            ds.addElement(Atts[i]);
        ds.addElement(ClassAttribute);
        return ds;
    }

    private static Instances getheader(FastVector fv, int size) {
        Instances dataSet = new Instances("justaname", fv, size);
        dataSet.setClassIndex(Constant.WEKA_ATTS.length);
        return dataSet;
    }

    private static Instance getelement(String[] s, FastVector fv) {
        Instance temp = new Instance(Constant.WEKA_ATTS.length + 1);
        for (int i = 0; i < s.length - 1; i++) {
            temp.setValue((Attribute) fv.elementAt(i), Double.valueOf(s[i]).doubleValue());
        }
        return temp;
    }


    private static void insertdata(Instance i) {
        if (header.numInstances() != 0) {
            header.delete();
        }
        header.add(i);
    }

    private static SerializedClassifier readmodel(String modelfile) throws Exception {
        File mfile = new File(modelfile);
        SerializedClassifier md = new SerializedClassifier();
        md.setModelFile(mfile);
        return md;
    }

}
