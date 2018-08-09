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
    public MakeDecision() throws Exception {
        this.init();
    }

    private FastVector datastructure;
    private Instances header;
    private SerializedClassifier currentmodel;

    public int predict(String[] features) throws Exception {
        int result_class = -1;
        insertdata(getelement(features, datastructure));
        double classifyResult = currentmodel.classifyInstance(header.firstInstance());
        result_class = (int) classifyResult;
        return result_class;
    }

    private void init() throws Exception {
        this.datastructure = getstructure();
        this.header = getheader(datastructure, 2);
        this.currentmodel = readmodel(Constant.MODEL_PATH);
    }

    private FastVector getstructure() {
        Attribute[] Atts = new Attribute[Constant.WEKA_ATTS.length];
        for (int i = 0; i < Constant.WEKA_ATTS.length; i++)
            Atts[i] = new Attribute(Constant.WEKA_ATTS[i]);

        FastVector ClassVal = new FastVector(Constant.WEKA_CLASSES.length);
        for (int i = 0; i < Constant.WEKA_CLASSES.length; i++)
            ClassVal.addElement(Constant.WEKA_CLASSES[i]);
        Attribute ClassAttribute = new Attribute("class", ClassVal);

        FastVector ds = new FastVector(Constant.WEKA_ATTS.length);
        for (int i = 0; i < Constant.WEKA_ATTS.length; i++)
            ds.addElement(Atts[i]);
        ds.addElement(ClassAttribute);
        return ds;
    }

    private Instances getheader(FastVector fv, int size) {
        Instances dataSet = new Instances("justaname", fv, size);
        dataSet.setClassIndex(Constant.WEKA_ATTS.length);
        return dataSet;
    }

    private Instance getelement(String[] s, FastVector fv) {
        Instance temp = new Instance(Constant.WEKA_ATTS.length);
        for (int i = 0; i < s.length - 1; i++) {
            temp.setValue((Attribute) fv.elementAt(i), Double.valueOf(s[i]).doubleValue());
        }
        return temp;
    }

    private void insertdata(Instance i) {
        if (header.numInstances() != 0) {
            header.delete();
        }
        header.add(i);
    }

    private SerializedClassifier readmodel(String modelfile) throws Exception {
        File mfile = new File(modelfile);
        SerializedClassifier md = new SerializedClassifier();
        md.setModelFile(mfile);
        return md;
    }

}
