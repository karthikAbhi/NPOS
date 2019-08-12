package com.nash.nposlibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class Wifi_Printer extends Printer {

    private static final String TAG = "Wifi_Printer";

    private Context mContext;

    private static Wifi_Printer myPrinter; // Singleton Design Pattern

    private Socket mSocket;
    private BufferedInputStream mBufferedInputStream;
    private BufferedOutputStream mBufferedOutputStream;

    private static boolean mSocketState;

    private String mReceivedBuffer;

    // Singleton Design Pattern continued...
    // Constructor to find and initialise the printer connection
    private Wifi_Printer(Context context) {
        mContext = context;
        establishConnection(context);
    }

    // Singleton Design Pattern continued...
    public static synchronized Wifi_Printer getInstance(Context context){
        if(myPrinter == null || !mSocketState){
            myPrinter = new Wifi_Printer(context);
        }
        return myPrinter;
    }

    @Override
    public void printText(String s) throws NullPointerException {
        transfer(s.getBytes());
    }

    @Override
    public String transfer(byte[] dataToPrintInBytes) {
        new advancedSendCommandATask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataToPrintInBytes);
        return null; // TODO: Check this out
    }

    @Override
    public void establishConnection(Context context) {
        new createSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void closeConnection() {
        closeSocket();
    }

    private void closeSocket() {
        //TODO: Check if close lock is useful.
        try {
            if(mSocket.isConnected() && !mSocket.isClosed()) {

                mSocket.close();
                mSocketState = false;
                // invalidateOptionsMenu();
                Toast.makeText(mContext.getApplicationContext(),
                        "Socket Closed!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class createSocket extends AsyncTask<Void, Boolean, String>{

        @Override
        protected void onPreExecute() {
            Toast.makeText(mContext.getApplicationContext(),
                    "Attempting Socket Connection...",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            //Create a socket connection

            mSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("192.168.4.1", 23);

            try {
                mSocket.connect(socketAddress, 2000);

                if(mSocket.isConnected()){
                    mBufferedInputStream = new BufferedInputStream(mSocket.getInputStream());
                    mBufferedOutputStream = new BufferedOutputStream(mSocket.getOutputStream());
                    Log.d("Receive Buffer Size:" , String.valueOf(mSocket.getReceiveBufferSize()));
                    Log.d("Send Buffer Size:" , String.valueOf(mSocket.getSendBufferSize()));
                    Log.d("Input Shutdown:" , String.valueOf(mSocket.isInputShutdown()));
                    Log.d("Output Shutdown:" , String.valueOf(mSocket.isOutputShutdown()));
                    publishProgress(Boolean.TRUE);
                    mSocketState = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
                mSocketState = false;
                // invalidateOptionsMenu();
                publishProgress(Boolean.FALSE);
                return e.getMessage();
            }
            // invalidateOptionsMenu();
            return "Socket Connection Successful...";
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            Toast.makeText(mContext.getApplicationContext(),
                    "Wifi State:"+ values[0],
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Advanced Asynctask for running the Network related operations - Here, sendCommand to the Printer
     */
    private class advancedSendCommandATask extends AsyncTask<byte[], String, String>{

        @Override
        protected String doInBackground(byte[]... bytes) {

            try {
                if(mSocket == null){
                    throw new SocketException("Null Socket");
                }

                String receivedData = sendData(bytes[0]);

                if (receivedData.equals("true")) {
                    publishProgress(receivedData);
                } else {
                    publishProgress(receivedData);
                    return receivedData;
                }

            } catch (Exception e) {
                e.printStackTrace();
                mSocketState = false;
                // invalidateOptionsMenu();
                return e.getMessage();
            }
            return "Success!";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(!values[0].equals("true")) {
                Toast.makeText(mContext.getApplicationContext(),
                        "Process failed!" + values[0],
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals("Success!")) {
                Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                mReceivedBuffer = s;
            }
        }
    }

    /**
     *  Read-Write Thread Class
     */
    public String sendData(byte[] bytes) throws IOException{

        String success = "false";
        String tmp = "";

        int totalLength = bytes.length;
        int numOfPackets = (int) Math.ceil(totalLength/500.0);
        int numOfSentPkts = 0;

        // Write data to the Printer
        Log.d(TAG, "Starting Write Sequence...");
        if(mSocket.isConnected() && mBufferedOutputStream != null && mBufferedInputStream != null){

            // write data below 500 bytes
            if (totalLength < 500) {
                try {

                    mBufferedOutputStream.write(bytes);
                    mBufferedOutputStream.flush();
                    //Thread.sleep(100);

                    tmp = readDataFromBuffer();
                    numOfSentPkts++;

                } catch (IOException e) {
                    e.printStackTrace();
                    success = "false";
                    throw new IOException(e);
                }

                if(tmp.contains("PRCDONE")) {
                    return "true";
                }
                else {
                    return tmp;
                }
            }
            // write data above 500 bytes
            else {

                int offset = 0;
                int length = 500; //TODO: Check the wifi packet limit
                int rem = totalLength;

                do {
                    try {

                        tmp = ""; // Reset the read value from the read buffer

                        mBufferedOutputStream.write(bytes, offset, length);
                        mBufferedOutputStream.flush();

                        numOfSentPkts++;

                        Log.d(TAG, "Number of Packets to Send: " + String.valueOf(numOfPackets));
                        Log.d(TAG, "Number of Sent Packets: " + String.valueOf(numOfSentPkts));

                        tmp = readDataFromBuffer();

                    } catch (IOException e) {
                        e.printStackTrace();
                        success = "false";
                        Log.d(TAG, "Write Process: " + "Failed!\n"+ e.getMessage());
                        throw new IOException(e);
                    }

                    offset += length;

                    rem = rem - length;

                    if (rem < length) {
                        length = rem;
                        rem = 0;
                    }
                    Log.d(TAG, "Write Process: " + "In Progress...!");

                } while (totalLength != offset && tmp.contains("PRCDONE"));
                success = "true";
            }
        }
        if(numOfPackets == numOfSentPkts){
            success = "true";
        }
        else{
            success = "false";
        }
        Log.d(TAG, "Write Process: " + "Completed!");
        return success;
    }
    // Read data sent by the Printer
    private String readDataFromBuffer() throws IOException{

        String readBufferData = "";

        int len = 0;

        try {

            byte[] prcdone = new byte[10];

            while(true){

                len = mBufferedInputStream.available();

                if(len > 0){ //TODO: Changed != 0 to > 0

                    //mBufferedInputStream.mark(7);
                    mBufferedInputStream.read(prcdone, 0, len);

                    for(byte tmp : prcdone){
                        readBufferData += (char)tmp;
                    }

                    if(readBufferData.contains("PRCDONE")){
                        Log.d(TAG, "In Read method: Prcdone Complete...");
                    }
                    else{
                        Log.d(TAG,"Vendor Request Completed!, Status: " + readBufferData);
                    }
                    break;
                }
                Log.d(TAG, "In Read method: Waiting for Prcdone...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "In Read method: " + e.getMessage());
            throw new IOException(e);
        }
        return readBufferData;
    }

}
