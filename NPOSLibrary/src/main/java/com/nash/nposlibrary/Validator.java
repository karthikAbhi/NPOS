package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

import android.util.Log;

import java.util.regex.Pattern;

public class Validator {

    public Validator() {
    }

    /**
     * Remove Carriage Return character(\r)
     * @param dataToPrintInBytes
     * @return
     */
    public static String cleanData(String dataToPrintInBytes) {

        String tmp = dataToPrintInBytes;

        while(tmp.contains("\r")){

            int pos = tmp.indexOf("\r");

            if(pos != -1){
                StringBuilder sb = new StringBuilder(tmp);
                sb.deleteCharAt(pos);
                tmp = sb.toString();
            }
        }
        dataToPrintInBytes = tmp;
        return dataToPrintInBytes;
    }


    public boolean check(String n, int minBound, int maxBound) {
        try{
            int temp = Integer.parseInt(n);

            try{
                if(temp < minBound || temp > maxBound ) {
                    throw new ValueOutOfBoundException("Incorrect Value "+n);
                }
                else{
                    return true;
                }
            } catch (ValueOutOfBoundException e){
                Log.e("ValueOutOfBoundError", e.getMessage());
            }
        }
        catch (NumberFormatException e){
            Log.e("Error","Invalid Input "+n);
        }
        return false;
    }

    public boolean check_barcode(BarcodeType barcodeType, String userInput){
        if(barcodeType.equals(BarcodeType.UPC_A)||barcodeType.equals(BarcodeType.UPC_E)){
            if((userInput.length() == 11||userInput.length()== 12) &&
                    Pattern.matches("^[0-9]*$", userInput)){
                return true;
            }
        }
        else if(barcodeType.equals(BarcodeType.JAN13)){
            if((userInput.length() == 12||userInput.length()== 13) &&
                    Pattern.matches("^[0-9]*$", userInput)){
                return true;
            }
        }
        else if(barcodeType.equals(BarcodeType.JAN8)){
            if((userInput.length() == 7||userInput.length()== 8) &&
                    Pattern.matches("^[0-9]*$", userInput)){
                return true;
            }
        }
        else if(barcodeType.equals(BarcodeType.CODE39)){
            if((userInput.length() > 0) &&
                    Pattern.matches("^[$%+=./\\- 0-9A-Z]*$", userInput)){
                return true;
            }
        }
        else if(barcodeType.equals(BarcodeType.ITF)){
            if((userInput.length() % 2 == 0) &&
                    Pattern.matches("^[0-9]*$", userInput)){
                return true;
            }
        }
        else if(barcodeType.equals(BarcodeType.CODABAR)){
            if((userInput.length() > 0)) {
                return (Pattern.matches("^[A-D][0-9+./:$\\-]*[A-D]$", userInput));
            }
        }
        return false;
    }
}
