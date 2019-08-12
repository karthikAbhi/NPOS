package com.nash.usb_printer;

import android.content.Context;

public abstract class Printer {

    public abstract void printText(String s) throws NullPointerException;

    public abstract void establishConnection(Context context);

    public abstract void closeConnection();
}
