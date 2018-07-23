package tooth.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cjh.recorder.R;
import tooth.data.ServerInterface;
import tooth.util.Constants;
import tooth.util.ServerConstant;
import weka.gui.Main;

/**
 * Created by JX on 2017/11/29.
 */

public class MainAcitvity extends AppCompatActivity implements View.OnClickListener {

    private TextView adduser, chooseuser, datacollection, startBrushing;
    private MenuItem item1;

    private final static String XMLPATH = "F:\\toothbrush_detector_setting.xml";   //别忘了改这里

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        getXMLIPAddress(); //根据XML文件获取IP地址
        //System.out.println("当前状态在刷哪个面：" + ServerInterface.getCurrentState());  //返回为int，网络操作，需要在新线程里进行
        //System.out.println("最近一次得分" + ServerInterface.getLatestScore());  //返回为int，网络操作，需要在新线程里进行
        //System.out.println("当前状态哪个面没刷好：" + ServerInterface.getNotYet());   //返回为String,格式为"1,2,3,4"网络操作，需要在新线程里进行
        //System.out.println("历史数据：" + ServerInterface.getHistory());   //返回为String,格式为"开始时间，持续时间，评分|开始时间，持续时间，评分"网络操作，需要在新线程里进行,注意要用split("\\|")分隔

        initialize();
    }

    private void initialize() {
        adduser = (TextView) findViewById(R.id.main_adduser);
        adduser.setOnClickListener(this);
        chooseuser = (TextView) findViewById(R.id.main_chooseuser);
        chooseuser.setOnClickListener(this);
        datacollection = (TextView) findViewById(R.id.main_datacollection);
        datacollection.setOnClickListener(this);
        startBrushing = (TextView) findViewById(R.id.main_startbrushing);
        startBrushing.setOnClickListener(this);
    }


    public static void getXMLIPAddress() {
        /*try {
            File f = new File(XMLPATH);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);
            NodeList nl = doc.getElementsByTagName("value");
            for (int i = 0; i < nl.getLength(); i++) {
                ServerConstant.IP_ADDRESS = doc.getElementsByTagName("ipaddress").item(i).getFirstChild().getNodeValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServerConstant.IP_ADDRESS = "192.168.0.183";*/
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "toothbrush_detector_setting.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
            ServerConstant.IP_ADDRESS = sb.toString();
            Log.i(Constants.TAG, "ip:" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.main_adduser:
                intent = new Intent(MainAcitvity.this, AddUserActivity.class);
                startActivity(intent);
                break;
            case R.id.main_chooseuser:
                intent = new Intent(MainAcitvity.this, ChooseUserActivity.class);
                startActivity(intent);
                break;
            case R.id.main_datacollection:
                intent = new Intent(MainAcitvity.this, CollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.main_startbrushing:
                intent = new Intent(MainAcitvity.this, DisplayActivity.class);
                startActivity(intent);
                break;
            default:
                ;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater in = getMenuInflater();
        in.inflate(R.menu.menu_layout, menu);*/
        super.onCreateOptionsMenu(menu);
        int group = 1;
        item1 = menu.add(group, 1, 1, "清除用户");

        //SharedPreferences userSettings= getSharedPreferences("setting",0);
        //int option = userSettings.getInt("option",1);
        //menu.setGroupCheckable(group,true,true);
        /*switch (option){
            case 1:
                item1.setChecked(true);
                break;
        }*/

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                SharedPreferences userSettings = getSharedPreferences(Constants.SETTINGS, 0);
                SharedPreferences.Editor editor = userSettings.edit();
                editor.clear();
                editor.commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
