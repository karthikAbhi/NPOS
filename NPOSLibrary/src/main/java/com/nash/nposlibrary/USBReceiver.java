package com.nash.nposlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public abstract class USBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_USB_DEVICE_ATTACHED)){
            Toast.makeText(context,
                    "USB Device Attached!",
                    Toast.LENGTH_SHORT).show();
            connect();
        }
        else if(intent.getAction().equals(ACTION_USB_DEVICE_DETACHED)){
            Toast.makeText(context,
                    "USB Device detached!",
                    Toast.LENGTH_SHORT).show();
            disconnect();
        }
    }

    public void disconnect(){}

    public abstract void connect();
}
