package tooth.activity;


import android.app.Application;

import weka.MakeDecision;

public class RecordApplication extends Application {
    private static RecordApplication application;
    private MakeDecision predictModel = null;

    /*
     * 返回application
     * */
    public static RecordApplication getApplication() {
        return application;
    }

    public MakeDecision getPredictModel() {
        return predictModel;
    }

    public void setPredictModel(MakeDecision predictModel) {
        this.predictModel = predictModel;
    }

    /*

     * 对于一个应用来说 android入口并不是Activity中的OnCreate()而是Application里面的Oncreate()
     * 也就相当于是java中的Main方法，只不过这个方法被封装了
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        //在Application创建时,读取Application
        application = this;
    }
}
