package com.nash.nposlibrary;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyBluetoothService {
    private static final String TAG = "MyBluetoothService";

    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private static Handler handler; // handler that gets info from Bluetooth service
        private boolean mPRCDONE = true;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            handler = new Handler(Looper.getMainLooper());

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    mmInStream.read(mmBuffer);
                    System.out.println(mmBuffer);
                    mPRCDONE = true;
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) throws IOException{
            mmOutStream.write(bytes);
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        // Call this from the main activity to send data to the remote device.
        /*public  void write(byte[] bytes) throws IOException {
            int offset = 0;
            int diff = 500;
            int length = diff;
            if(mPRCDONE){

                int totallength = bytes.length;

                while(totallength > diff){

                    mmOutStream.write(bytes, offset, length);
                    offset = offset + length;
                    length = length + diff;
                    if(length > totallength){
                        length = totallength;
                        totallength = totallength - length;
                    }
                    else{
                        totallength = totallength - length;
                    }
                    mPRCDONE = false;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(totallength < diff){
                    mmOutStream.write(bytes);
                }
            }
            else{
                System.out.println("Error");
            }
        }*/
    }
}
