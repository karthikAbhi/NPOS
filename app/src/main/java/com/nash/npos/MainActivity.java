package com.nash.npos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NPOS";
    private Context mContext;
    private int mConnectionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

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
                    ConnectionType.USB.setConnectionState(false);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.BT.isConnectionState() || ConnectionType.WIFI.isConnectionState())){
                        // Enable BT Connection
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
                    ConnectionType.BT.setConnectionState(false);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.USB.isConnectionState() || ConnectionType.WIFI.isConnectionState())){
                        // Enable BT Connection
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
                    ConnectionType.WIFI.setConnectionState(false);
                    //mConnectionId = -1;
                }
                else{
                    // Check for other connections, pop up a message, then turn On
                    if(!(ConnectionType.USB.isConnectionState() || ConnectionType.BT.isConnectionState())){
                        // Enable WI-FI Connection
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
}
