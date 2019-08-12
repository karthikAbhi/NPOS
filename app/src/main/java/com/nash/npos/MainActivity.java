package com.nash.npos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nash.usb_printer.BTReceiver;
import com.nash.usb_printer.BT_Printer;
import com.nash.usb_printer.Printer;
import com.nash.usb_printer.USBReceiver;
import com.nash.usb_printer.USB_Printer;
import com.nash.usb_printer.Wifi_Printer;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NPOS";
    private static final int REQUEST_ENABLE_BT = 1;
    private Context mContext;
    private int mConnectionId = -1;


    private Printer mPrinter;

    Button mConnectBtn;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mConnectBtn = findViewById(R.id.btn_connect);

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPrinter.printText("Hello\n");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_usb_connection:
                if(ConnectionType.USB.isConnectionState() == true){
                    // Turn Off
                    //mPrinter.closeConnection();
                    mPrinter = null;
                    ConnectionType.USB.setConnectionState(false);
                    unregisterReceiver(mUSBReceiver);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.BT.isConnectionState() || ConnectionType.WIFI.isConnectionState())){
                        // Enable USBConnection

                        IntentFilter intentFilterUSB = new IntentFilter();
                        intentFilterUSB.addAction(ACTION_USB_DEVICE_DETACHED);
                        intentFilterUSB.addAction(ACTION_USB_DEVICE_ATTACHED);

                        registerReceiver(mUSBReceiver, intentFilterUSB);

                        mPrinter = USB_Printer.getInstance(mContext);

                        ConnectionType.USB.setConnectionState(true);
                        mConnectionId = 0;
                    }
                    else{
                        // Pop up message containing the current connection
                        if(ConnectionType.BT.isConnectionState()){
                            Log.d(TAG, "Already connected to bluetooth Connection");
                        }
                        else if(ConnectionType.WIFI.isConnectionState()){
                            Log.d(TAG, "Already connected to Wifi Connection");
                        }
                    }
                }
                invalidateOptionsMenu();
                Toast.makeText(getBaseContext(),
                        "USB Connection",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_bt_connection:
                if(ConnectionType.BT.isConnectionState() == true){
                    // Turn Off
                    mPrinter.closeConnection();
                    mPrinter = null;
                    ConnectionType.BT.setConnectionState(false);
                    unregisterReceiver(mBTReceiver);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.USB.isConnectionState() || ConnectionType.WIFI.isConnectionState())){
                        // Enable BT Connection
                        initBT();

                        mPrinter = BT_Printer.getInstance(getApplicationContext(), "NPOS820");
                        ConnectionType.BT.setConnectionState(true);
                        mConnectionId = 1;
                    }
                    else{
                        // Pop up message containing the current connection
                        if(ConnectionType.USB.isConnectionState()){
                            Log.d(TAG, "Already connected to USB Connection");
                        }
                        else if(ConnectionType.WIFI.isConnectionState()){
                            Log.d(TAG, "Already connected to Wifi Connection");
                        }
                    }
                }
                invalidateOptionsMenu();
                Toast.makeText(mContext,
                        "Bluetooth Connection",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_wifi_connection:
                if(ConnectionType.WIFI.isConnectionState() == true){
                    // Turn Off
                    mPrinter.closeConnection();
                    mPrinter = null;
                    ConnectionType.WIFI.setConnectionState(false);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.USB.isConnectionState() || ConnectionType.BT.isConnectionState())){
                        // Enable WI-FI Connection

                        mPrinter = Wifi_Printer.getInstance(getApplicationContext());

                        ConnectionType.WIFI.setConnectionState(true);
                        mConnectionId = 2;
                    }
                    else{
                        // Pop up message containing the current connection
                        if(ConnectionType.BT.isConnectionState()){
                            Log.d(TAG, "Already connected to bluetooth Connection");
                        }
                        else if(ConnectionType.USB.isConnectionState()){
                            Log.d(TAG, "Already connected to USB Connection");
                        }
                    }
                }
                invalidateOptionsMenu();
                Toast.makeText(mContext,
                        "Wifi Connection",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_app_close:
                this.finish(); // Close the application
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Move this to Bluetooth printer
    private void initBT() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(),
                    "Device doesn't support bluetooth connectivity",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No bluetooth support for this device");
        } else {
            Toast.makeText(getApplicationContext(),
                    "Device support bluetooth connectivity",
                    Toast.LENGTH_SHORT).show();

            if (!mBluetoothAdapter.isEnabled()) {
                Log.i(TAG, "Bluetooth off... Turning on now...");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.i(TAG, "Bluetooth is already turned on...");
            }
        }

        // Register IntentFilter
        IntentFilter intentFilterBT = new IntentFilter();
        intentFilterBT.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilterBT.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilterBT.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilterBT.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilterBT.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBTReceiver, intentFilterBT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            Log.i("Info","Request code correct");

            if(resultCode == RESULT_OK){
                Log.i("Info","Result code correct");
                Toast.makeText(getApplicationContext(),"Bluetooth State: On",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Log.i("Info","Result code incorrect");
                Toast.makeText(getApplicationContext(),"Bluetooth State: Off",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.i("Info","Improper request code");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mConnectionId >= 0){
            MenuItem menuitem = menu.getItem(mConnectionId);
            Drawable tmp = null;

            switch((String) menuitem.getTitle()){
                case "USB":
                    tmp = getResources().getDrawable(R.drawable.ic_usb_white_24dp);
                    if(ConnectionType.USB.isConnectionState()){
                        tmp.setAlpha(255);
                    }
                    else{
                        tmp.setAlpha(128);
                    }
                    menuitem.setIcon(tmp);
                    break;
                case "Bluetooth":
                    tmp = getResources().getDrawable(R.drawable.ic_bluetooth_white_24dp);
                    if(ConnectionType.BT.isConnectionState()){
                        tmp.setAlpha(255);
                    }
                    else{
                        tmp.setAlpha(128);
                    }
                    menuitem.setIcon(tmp);
                    break;
                case "Wi-Fi":
                    tmp = getResources().getDrawable(R.drawable.ic_wifi_white_24dp);
                    if(ConnectionType.WIFI.isConnectionState()){
                        tmp.setAlpha(255);
                    }
                    else{
                        tmp.setAlpha(128);
                    }
                    menuitem.setIcon(tmp);
                    break;
                default:
                    return super.onPrepareOptionsMenu(menu);
            }
        }
        else{
            // Select a connection to proceed
            Toast.makeText(getApplicationContext(),
                    "Select a connection type",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // USB Related Methods

    USBReceiver mUSBReceiver = new USBReceiver(){
        @Override
        public void connect() {
            if(mPrinter == null){
                mPrinter = USB_Printer.getInstance(getApplicationContext());
            }
            else{
                mPrinter.establishConnection(getApplicationContext());
            }
        }

        @Override
        public void disconnect() {
            mPrinter.closeConnection();
        }
    };

    // BT Related Methods

    BTReceiver mBTReceiver = new BTReceiver(){

        @Override
        public void disconnect() {
            mPrinter.closeConnection();
        }

        @Override
        public void connect() {
            if(mPrinter == null){
                mPrinter = BT_Printer.getInstance(getApplicationContext(), "NPOS820");
            }
            else{
                mPrinter.establishConnection(getApplicationContext());
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if(ConnectionType.USB.isConnectionState()){
            if(mPrinter == null){
                mPrinter = USB_Printer.getInstance(mContext);
            }
        }
        if(ConnectionType.WIFI.isConnectionState()){
            if(mPrinter == null){
                mPrinter = Wifi_Printer.getInstance(mContext);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "In onPause()");
        Log.i(TAG, "Going to onStop()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "In onStop()");
        Log.i(TAG, "Going to onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "In onRestart()");
        Log.i(TAG, "Going to onStart()");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "In onStart()");
        Log.i(TAG, "Going to onResume()");
    }

    @Override
    protected void onDestroy() {

        Log.i(TAG, "In onDestroy()");
        Log.i(TAG, "Going to Activity Shutdown");

        if(ConnectionType.USB.isConnectionState()){
            unregisterReceiver(mUSBReceiver);
            ConnectionType.USB.setConnectionState(false);
        }
        if(ConnectionType.BT.isConnectionState()){
            unregisterReceiver(mBTReceiver);
            ConnectionType.BT.setConnectionState(false);
        }
        if(ConnectionType.WIFI.isConnectionState()){
            ConnectionType.WIFI.setConnectionState(false);
        }

        super.onDestroy();
    }


}
