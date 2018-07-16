package cjh.recorder;

/**
 * Created by admin on 2017/10/11.
 */

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTBroadcastReceiver extends BroadcastReceiver {
    static final String LOG_TAG = "BTBroadcastReceiver";
    static final boolean DEBUG = true;
    static Context mContext = null;
    static boolean isScoConnect;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE,
                    0);
            if (state == BluetoothHeadset.STATE_CONNECTED) {
                Log.i("onReceive", "static BluetoothHeadset.STATE_CONNECTED");
                isScoConnect = true;
            } else if (state == BluetoothHeadset.STATE_DISCONNECTING) {
                Log.i("onReceive", "static BluetoothHeadset.STATE_CONNECTED");
                isScoConnect = false;
            }
        }

    }

    public static boolean getState() {
        return isScoConnect;
    }

}
