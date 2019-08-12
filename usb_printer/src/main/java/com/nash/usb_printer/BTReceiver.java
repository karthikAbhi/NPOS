package com.nash.usb_printer;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Broadcast Receiver to listen for all the actions specified in the Intent Filter
 */
public abstract class BTReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            //Device Found
            if(device.equals("NPOS820")){
                Log.i("BT", "Found");
                Toast.makeText(context,"Device Found!",
                        Toast.LENGTH_SHORT)
                        .show();
                //establishConnection(device);

            }
        }
        else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
            Log.i("BT", "ACTION_ACL_CONNECTED");
            Toast.makeText(context,"Action ACL Connected!",
                    Toast.LENGTH_SHORT).show();

            if(device.getName().equals("NPOS820")){
                Log.i("BT", "Found");
                Toast.makeText(context,"Device Connected!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)){
            Log.i("BT", "ACTION_ACL_DISCONNECT_REQUESTED");
            Toast.makeText(context,"ACTION_ACL_DISCONNECT_REQUESTED!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
            Log.i("BT", "ACTION_ACL_DISCONNECTED");
            Toast.makeText(context,"ACTION_ACL_DISCONNECTED",
                    Toast.LENGTH_SHORT).show();
            if(device.getName().equals("NPOS820")){
                if(device.equals("NPOS820")){
                    Log.i("BT", "Found");
                    Toast.makeText(context,
                            "Device Disconnected!\n" + "NPOS820", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            disconnect();
        }
        else{
            Toast.makeText(context,"Something is broadcasted!" + intent.getAction(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void disconnect();

    public abstract void connect();
}