package com.nash.usb_printer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class USB_Printer extends Printer {

    private static USB_Printer myPrinter; // Singleton Design Pattern

    private static final String TAG = "USBPrinter";

    //Android Components
    private static UsbManager mUsbManager;
    private static UsbDevice mDevice;
    private UsbInterface mInterface;
    private UsbEndpoint mEndpoint;
    private UsbDeviceConnection mConnection;
    private static String mUsbDevice = "";
    private static PendingIntent mPermissionIntent;

    IntentFilter mFilter = new IntentFilter(USB_Printer.ACTION_USB_PERMISSION);

    public final static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    // Singleton Design Pattern continued...
    public static synchronized USB_Printer getInstance(Context context){
        if(myPrinter == null){
            myPrinter = new USB_Printer(context);
        }
        return myPrinter;
    }

    // Singleton Design Pattern continued...
    // Constructor to find and initialise the printer connection
    private USB_Printer(Context context) {

        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        context.registerReceiver(mUsbReceiver, mFilter);
        establishConnection(context);

    }

    public void establishConnection(Context context){

        context.registerReceiver(mUsbReceiver, mFilter);

        //Contains all the UsbDevices list in a Hashmap Datastructure
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        //deviceList = mUsbManager.getDeviceList();
        while (deviceIterator.hasNext()) {
            mDevice = deviceIterator.next();
            if (mDevice.getVendorId() == 12232) {
                //Device Found
                Toast.makeText(context.getApplicationContext(),
                        "Printer Connected!" + mUsbDevice,
                        Toast.LENGTH_SHORT).show();

                mPermissionIntent = PendingIntent.getBroadcast(context,
                        0,
                        new Intent(ACTION_USB_PERMISSION),
                        0);

                mUsbManager.requestPermission(mDevice, mPermissionIntent);
                break;

            }
            else {
                Toast.makeText(context.getApplicationContext(),
                        "Not a Printer",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context.getApplicationContext(), "Receiver called", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (USB_Printer.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    Toast.makeText(context.getApplicationContext(), "Receiver called : "+device.getDeviceName() + " "+device.getManufacturerName(), Toast.LENGTH_SHORT).show();

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndpoint = mInterface.getEndpoint(1);// 0 IN and  1 OUT to printer.
                            mConnection = mUsbManager.openDevice(device);

                            for (int i = 0; i < mInterface.getEndpointCount(); i++) {
                                Log.i("Printer", "EP: "
                                        + String.format("0x%02X", mInterface.getEndpoint(i)
                                        .getAddress()));

                                if (mInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                    Log.i("Printer", "Bulk Endpoint");
                                    if (mInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                        //epIN = mInterface.getEndpoint(i);
                                        Log.i("Printer", "input stream found");
                                    }else {
                                        mEndpoint = mInterface.getEndpoint(i);
                                        Log.i("Printer", "outstream found");
                                    }
                                } else {
                                    Log.i("Printer", "Not Bulk");
                                }
                            }

                        }
                    } else {
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    public void closeConnection(){
        try{
            mConnection.close();
        } catch (Exception e){
            Log.e("Close USB Connection", e.getMessage());
        }
    }

    public void printText(String dataToPrint){transfer(dataToPrint);}

    //Convert to Byte array
    private void transfer(String dataToPrintInString){

        byte[] printerInput = null;

        try {
            printerInput = dataToPrintInString.getBytes("UTF-8");
            transfer(printerInput);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Process USB Bulk Transfer
    @Override
    public String transfer(final byte[] dataToPrintInBytes){

        try{
            if(mInterface == null){
                throw new NullInterfaceException("Interface is Null");
            }else
                try {
                    if (mConnection == null) {
                        throw new NullConnectionException("Connection is Null");
                    }
                    else {
                        boolean status = mConnection.claimInterface(mInterface, true);
                        Log.i("Printer", "claim status " + status);

                        Log.i("Printer", "Before control transfer: ");

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                mConnection.bulkTransfer(mEndpoint,
                                        dataToPrintInBytes,
                                        dataToPrintInBytes.length,
                                        0);
                            }
                        });
                        thread.run();

                    }
                }
                catch (NullConnectionException e) {
                    Log.e("MyPrinter","Null Connection Exception"+ e.getMessage());
                }
        }catch(NullInterfaceException e){
            Log.e("MyPrinter","Null Interface Exception"+ e.getMessage());
        }
        catch (Exception e){
            Log.e("MyPrinter", e.getMessage());
        }

        return null; // TODO: Check this out
    }
}
