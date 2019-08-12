package com.nash.usb_printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.io.IOException;
import java.util.Set;

public class BT_Printer extends Printer {

    private static final String TAG = "BT_Printer";
    private Context mContext;

    private ConnectThread mConnectThread;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothManager mBluetoothManager;
    private String mBluetoothDeviceName;

    private static BT_Printer myPrinter; // Singleton Design Pattern

    public BluetoothAdapter mBluetoothAdapter;

    private static boolean mSocketState;

    private String mReceivedBuffer;

    // Singleton Design Pattern continued...
    // Constructor to find and initialise the printer connection
    private BT_Printer(Context context, String BTDeviceName) {
        mContext = context;
        mBluetoothDeviceName = BTDeviceName;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        establishConnection(context);
    }

    // Singleton Design Pattern continued...
    public static synchronized BT_Printer getInstance(Context context, String BTDeviceName){
        if(myPrinter == null || !mSocketState){
            myPrinter = new BT_Printer(context, BTDeviceName);
        }
        return myPrinter;
    }

    @Override
    public void printText(String s) throws NullPointerException {
        try {
            mConnectThread.writeData(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void establishConnection(Context context) {

        Set<BluetoothDevice> connectedDevices = mBluetoothAdapter.getBondedDevices();

        // Currently, Only one bonded device will connect

        for(BluetoothDevice d : connectedDevices){
            if(d.getName().equals(mBluetoothDeviceName)){
                mBluetoothDevice = d;
            }
        }

        mConnectThread = new ConnectThread(mBluetoothDevice);
        new Thread(mConnectThread).start();
    }

    @Override
    public void closeConnection() {
        mConnectThread.cancel();
    }
}
