package tooth.activity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cjh.recorder.R;
import tooth.util.Constants;

/**
 * Created by JX on 2017/11/29.
 */

public class ChooseUserActivity  extends ListActivity {

    private int num = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseuser_layout);

        SharedPreferences userSettings= getSharedPreferences(Constants.SETTINGS, 0);
        num = userSettings.getInt(Constants.USERNUM,0);
        //Log.i(Constants.TAG,"num:"+num);

        if(num == 0){
            showConfirmDialog();
        }
        initialize();
    }

    private void initialize() {
        SimpleAdapter adapter = new SimpleAdapter(this,getData(), R.layout.user_item,
                new String[]{"title"},
                new int[]{R.id.title});
        setListAdapter(adapter);
    }

    private void showConfirmDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(ChooseUserActivity.this).create();
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

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        SharedPreferences userSettings= getSharedPreferences(Constants.SETTINGS, 0);

        for(int i=0; i<num; i++){
            map = new HashMap<String, Object>();
            map.put("title", userSettings.getString(Constants.USERNAME+i,".."));
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);

        Constants.USER_NUM = position;
        Intent intent = new Intent(ChooseUserActivity.this,UserActivity.class);
        startActivity(intent);

    }



}
