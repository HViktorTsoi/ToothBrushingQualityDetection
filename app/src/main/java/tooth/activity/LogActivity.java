package tooth.activity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cjh.recorder.R;
import tooth.util.Constants;
import weka.Constant;

/**
 * Created by JX on 2017/11/29.
 */

public class LogActivity extends ListActivity {

    private String username = "";

    private TextView user_name;

    private int num = 0;

    private ArrayList<String> creationTime = new ArrayList<>();
    private ArrayList<String> duration = new ArrayList<>();
    private ArrayList<String> scores = new ArrayList<>();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);

        SharedPreferences userSettings= getSharedPreferences(Constants.SETTINGS, 0);
        username = userSettings.getString(Constants.USERNAME+Constants.USER_NUM,"");
        //Log.i(Constants.TAG,"num:"+num);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        creationTime = bundle.getStringArrayList(Constants.CREATIONTIME);
        duration = bundle.getStringArrayList(Constants.DURATION);
        scores = bundle.getStringArrayList(Constants.SCORES);

        num = creationTime.size();
        initialize();
    }

    private void initialize() {


        user_name = (TextView)findViewById(R.id.log_username);
        user_name.setText(username);

        SimpleAdapter adapter = new SimpleAdapter(this,getData(), R.layout.log_item,
                new String[]{"num","time","duration","grade"},
                new int[]{R.id.num,R.id.time,R.id.duration,R.id.grade});
        setListAdapter(adapter);
    }

    private void showConfirmDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(LogActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.dialog_title));
        alertDialog.setMessage(getResources().getString(R.string.dialog_nouser));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private List<Map<String, Object>> getData() {

        // TODO: 2017/11/29  need to finish receiving data from database...

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        //SharedPreferences userSettings= getSharedPreferences(Constants.SETTINGS, 0);

        for(int i=0; i<num; i++){
            map = new HashMap<String, Object>();
            map.put("num", i+1);
            map.put("time",timeFormat2(creationTime.get(i)));
            map.put("duration",timeFormat(Long.valueOf(duration.get(i))));
            map.put("grade",scores.get(i)+getResources().getString(R.string.brushresult_fen));
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);

        /*Constants.USER_NUM = position;
        Intent intent = new Intent(LogActivity.this,UserActivity.class);
        startActivity(intent);*/

    }

    private String timeFormat(long timeseconds){
        String result = "";

        timeseconds /= 1000;

        if (timeseconds>=3600){
            result = "60min";
        }
        else if(timeseconds>=60){
            result = timeseconds/60 + "m" + timeseconds%60 + "s";
        }
        else{
            result = timeseconds + "s";
        }
        return result;
    }

    private String timeFormat2(String ms){
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日HH:mm:ss");
        Date curDate =  new Date(Long.valueOf(ms));
        String   str   =   formatter.format(curDate);
        return  str;
    }


}
