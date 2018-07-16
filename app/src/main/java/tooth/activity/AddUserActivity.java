package tooth.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cjh.recorder.R;
import tooth.util.Constants;

/**
 * Created by JX on 2017/11/29.
 */

public class AddUserActivity extends AppCompatActivity {

    private EditText username;
    private TextView confirm;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser_layout);

        initialize();
    }

    private void initialize() {
        username = (EditText)findViewById(R.id.add_username);

        confirm = (TextView)findViewById(R.id.add_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                if (name.equals("")) {
                    showConfirmDialog();
                }
                else{
                    saveUserName(name);
                }
            }
        });
    }

    private void showConfirmDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(AddUserActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.dialog_title));
        alertDialog.setMessage(getResources().getString(R.string.dialog_message));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });
        alertDialog.show();
    }

    private void saveUserName(String name){
        // TODO: 2017/11/29 save the new added user name
        SharedPreferences userSettings = getSharedPreferences(Constants.SETTINGS, 0);
        int num = userSettings.getInt(Constants.USERNUM,0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(Constants.USERNAME+num,name);
        //Log.i(Constants.TAG,"num:"+num+"- -name:"+name);
        editor.putInt(Constants.USERNUM,++num);
        //Log.i(Constants.TAG,"num:"+num+"- -name:"+name);
        editor.commit();

        Toast.makeText(AddUserActivity.this,getResources().getString(R.string.add_success),Toast.LENGTH_SHORT).show();
        username.setText("");
        finish();
    }
}
