package tooth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cjh.recorder.R;
import tooth.data.ServerInterface;
import tooth.util.Constants;

/**
 * Created by JX on 2017/11/29.
 */

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView brush,log;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);

        Log.i(Constants.TAG,"User Num :"+Constants.USER_NUM);
        initialize();
    }

    private void initialize() {
        brush = (TextView)findViewById(R.id.user_brush);
        brush.setOnClickListener(this);
        log = (TextView)findViewById(R.id.user_log);
        log.setOnClickListener(this);
        log.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.user_brush:
                intent = new Intent(UserActivity.this,DisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.user_log:
                new Thread(){
                    public void run() {
                        Log.i(Constants.TAG,"getHistory_thread runing ...");
                        try {
                            Message message = new Message();
                            message.what = 1;
                            message.obj = ServerInterface.getHistory(Constants.USER_NUM+1);
                            handler_getHistory.sendMessage(message);

                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }
                }.start();  //开启一个线程

                break;
            default:
                ;
        }
    }

    private ArrayList<String> creationTime = new ArrayList<>();
    private ArrayList<String> duration = new ArrayList<>();
    private ArrayList<String> scores = new ArrayList<>();

    final Handler handler_getHistory = new Handler() {          // handle
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String logHistory = (String) msg.obj;
                //Log.i(Constants.TAG,"getHistory_thread result logHistory:"+logHistory);
                String [] logHistorys = logHistory.split("\\|");
                creationTime.clear();
                duration.clear();
                scores.clear();
                for(int i=0;i<logHistorys.length;i++){
                    //Log.i(Constants.TAG,"getHistory_thread result logHistorys ["+i+"]:"+logHistorys[i]);
                    String [] loghistory =logHistorys[i].split(",");
                    creationTime.add(loghistory[0]);
                    duration.add(loghistory[1]);
                    scores.add(loghistory[2]);
                }
                /*Log.i(Constants.TAG,"getHistory_thread result creationTime:"+creationTime.toString());
                Log.i(Constants.TAG,"getHistory_thread result duration:"+duration.toString());
                Log.i(Constants.TAG,"getHistory_thread result scores:"+scores.toString());*/
                Intent intent = new Intent(UserActivity.this,LogActivity.class);
                Bundle bundle = new Bundle();
                //bundle.putInt(Constants.TOTAL_TIME,time_seconds);
                //bundle.putInt(Constants.SCORES,scores);
                //bundle.putBooleanArray(Constants.STRINGS,flagList);
                bundle.putStringArrayList(Constants.CREATIONTIME,creationTime);
                bundle.putStringArrayList(Constants.DURATION,duration);
                bundle.putStringArrayList(Constants.SCORES,scores);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };
}
