package com.acesdatasystems.quickcash;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        Broadcast receiver set for bluetooth state
        ACTION_ACL_CONNECTED
         */

        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(500); // 50 is time in ms
            HomeActivity.printerConnected();
            ProductsActivity.printerConnected();
        }
        /*
        Broadcast receiver set for bluetooth state
        ACTION_ACL_DISCONNECTED
         */
        if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())){
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(500); // 50 is time in ms
            HomeActivity.printerDisconnected();
            ProductsActivity.printerDisconnected();

        }
    }
}
