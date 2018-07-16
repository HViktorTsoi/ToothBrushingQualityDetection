package tooth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cjh.recorder.R;
import tooth.util.Constants;

/**
 * Created by JX on 2017/11/29.
 */

public class BrushResultActivity extends AppCompatActivity {

    private TextView totaltime, grade, strings;

    private int total_time = 0;
    private int scores = 0;
    private boolean[] string = new boolean[19];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brushresult_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        total_time = bundle.getInt(Constants.TOTAL_TIME);
        scores = bundle.getInt(Constants.SCORES);
        string = bundle.getBooleanArray(Constants.STRINGS);

        initialize();
    }

    private void initialize() {
        totaltime = (TextView)findViewById(R.id.brushresult_totaltime);
        grade = (TextView)findViewById(R.id.brushresult_grade);
        strings = (TextView)findViewById(R.id.brushresult_strings);

        totaltime.setText(timeFormat(total_time));
        grade.setText(scores+getResources().getString(R.string.brushresult_fen));
        strings.setText(stringFormat(string));

    }

    private String timeFormat(int timeseconds){
        String result = "";

        if(timeseconds>=60){
            result = timeseconds/60 + "m" + timeseconds%60 + "s";
        }
        else{
            result = timeseconds + "s";
        }
        return result;
    }

    private String stringFormat(boolean[] strs){
        StringBuffer stringBuffer = new StringBuffer();
        if(!strs[3]){
            stringBuffer.append(getResources().getString(R.string.brushresult_ulbo)+'\n');
        }
        if(!strs[4]){
            stringBuffer.append(getResources().getString(R.string.brushresult_ufo)+'\n');
        }
        if(!strs[5]){
            stringBuffer.append(getResources().getString(R.string.brushresult_urbo)+'\n');
        }
        if(!strs[6]){
            stringBuffer.append(getResources().getString(R.string.brushresult_ulbm)+'\n');
        }
        if(!strs[7]){
            stringBuffer.append(getResources().getString(R.string.brushresult_urbm)+'\n');
        }
        if(!strs[8]){
            stringBuffer.append(getResources().getString(R.string.brushresult_ulbi)+'\n');
        }
        if(!strs[9]){
            stringBuffer.append(getResources().getString(R.string.brushresult_ufi)+'\n');
        }
        if(!strs[10]){
            stringBuffer.append(getResources().getString(R.string.brushresult_urbi)+'\n');
        }
        if(!strs[11]){
            stringBuffer.append(getResources().getString(R.string.brushresult_dlbo)+'\n');
        }
        if(!strs[12]){
            stringBuffer.append(getResources().getString(R.string.brushresult_dfo)+'\n');
        }
        if(!strs[13]){
            stringBuffer.append(getResources().getString(R.string.brushresult_drbo)+'\n');
        }
        if(!strs[14]){
            stringBuffer.append(getResources().getString(R.string.brushresult_dlbm)+'\n');
        }
        if(!strs[15]){
            stringBuffer.append(getResources().getString(R.string.brushresult_drbm)+'\n');
        }
        if(!strs[16]){
            stringBuffer.append(getResources().getString(R.string.brushresult_dlbi)+'\n');
        }
        if(!strs[17]){
            stringBuffer.append(getResources().getString(R.string.brushresult_dfi)+'\n');
        }
        if(!strs[18]){
            stringBuffer.append(getResources().getString(R.string.brushresult_drbi)+'\n');
        }
        return stringBuffer.toString();
    }


    /*@Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.brushresult_totaltime:
                intent = new Intent(BrushResultActivity.this,AddUserActivity.class);
                startActivity(intent);
                break;
            case R.id.m:
                intent = new Intent(BrushResultActivity.this,ChooseUserActivity.class);
                startActivity(intent);
                break;
            case R.id.main_datacollection:
                intent = new Intent(BrushResultActivity.this,CollectionActivity.class);
                startActivity(intent);
                break;
            default:
                ;
        }
    }*/
}
