package com.nash.usb_printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.askjeffreyliu.floydsteinbergdithering.Utils;
import com.nash.nashprintercommands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

public abstract class Printer {

    private static final String TAG = "Printer_Library";

    public abstract void establishConnection(Context context); // Imp

    public abstract void closeConnection(); // Imp

    //Nash Printer Command Reference
    private Command myCommand = new Command();
    //Command Validator reference
    private Validator mValidator = new Validator();

    //Android Components

    private Context mContext;
    private int nCurrentSubset = 0;

    private String mReceivedDataFromPrinter;

    // Getter Method for Received Data from Printer
    public String getReceivedDataFromPrinter() {
        return mReceivedDataFromPrinter;
    }

    public void printText(String dataToPrint) {
        transfer(dataToPrint);
    }

    //Convert to Byte array
    private final void transfer(String dataToPrintInString) {

        byte[] printerInput = null;

        try {
            Validator.cleanData(dataToPrintInString);
            printerInput = dataToPrintInString.getBytes("UTF-8");
            transfer(printerInput);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Process User Input Transfer
    public abstract String transfer(final byte[] dataToPrintInBytes); // Imp

    /**
     * Image Related methods
     */
    private final Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int maxWidth = 128;
        int maxHeight = 128;

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) (height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) (width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    //RGB to Grayscale Conversion
    private final Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Implemetation of the printer commands according to our requirement
     */

    //Cut command - Cut the form (14.43)
    public void executeCutCommand() {
        transfer(myCommand.cut_paper);
    }

    //Print data and feed a line (14.01)
    public void LF() {
        transfer(myCommand.LF);
    }

    //Print data and feed a page on page mode (14.02)
    public void FF() {
        transfer(myCommand.FF);
    }

    //Print data on page mode (14.03)
    public void ESC_FF() {
        transfer(myCommand.ESC_FF);
    }

    //Print data and feed the form to forward (14.04)
    public void ESC_J(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_J);
            transfer(convertStringToByteArray(n));
        }
    }

    //Print data and feed n lines (14.05) (With Parameter)
    public void ESC_D(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_D);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set the line feed amount (14.06) (With Parameter)
    public void ESC_3(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_3);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set the gap of right side of the character (14.07) (With Parameter)
    public void ESC_SP(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_SP);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set parameter of print mode (14.08) (With Parameter)
    public void ESC_PM(CharacterFontType characterFontType,
                       boolean doubleHeight, boolean doubleWidth) {

        byte tmp = 0;

        // Character Font Selection
        if (characterFontType.getFont() == CharacterFontType.FONT_A.getFont()) {
            tmp = 0;
        } else if (characterFontType.getFont() == CharacterFontType.FONT_B.getFont()) {
            tmp = 1;
        }
        // Double Height
        if (doubleHeight) {
            tmp += 16;
        }
        // Double Width
        if (doubleWidth) {
            tmp += 32;
        }

        transfer(myCommand.ESC_PM);
        transfer(new byte[]{tmp});
    }

    //Select the font (14.09) (With Parameter)
    public void ESC_M(String n) {
        if (mValidator.check(n, 0, 1)) {
            transfer(myCommand.ESC_M);
            transfer(convertStringToByteArray(n));
        }
    }

    //Select character code table (14.10) (With Parameter)
    public void ESC_t(byte[] n) {
        transfer(myCommand.ESC_t);
        transfer(n);
    }

    //Specify download character (14.11) (With Parameter)
    public void ESC_DC() {
        transfer(myCommand.ESC_DC);
    }

    //Set or unset download character set (14.12) (With Parameter)
    public void ESC_SDC(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_SDC);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set the print mode to page mode (14.13)
    public void PAGE_MODE() {
        transfer(myCommand.ESC_L);
    }

    //Set the print mode to standard mode (14.14)
    public void STANDARD_MODE() {
        transfer(myCommand.ESC_S);
    }

    //Tabbing Horizontal (14.15)
    public void HT() {
        transfer(myCommand.HT);
    }

    //Set the left side margin (14.16) (With Parameter)
    public void GS_L(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.GS_L);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Set the width of print area (14.17) (With Parameter)
    public void GS_W(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.GS_W);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Specify the print area on page mode (14.18) (With Parameter)
    public void ESC_W(String x, String y, String xLength, String yLength) {

        if (mValidator.check(x, 0, 576)
                && mValidator.check(y, 0, 576)
                && mValidator.check(xLength, 0, 576)
                && mValidator.check(yLength, 0, 576)) {

            transfer(myCommand.ESC_W);
            transfer(convertStringToTwoByteArray(x));
            transfer(convertStringToTwoByteArray(y));
            transfer(convertStringToTwoByteArray(xLength));
            transfer(convertStringToTwoByteArray(yLength));

        }

    }

    //Set the physical position (14.19) (With Parameter)
    public void ESC_PP(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.ESC_PP);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Set the logical position (14.20) (With Parameter)
    public void ESC_LP(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.ESC_LP);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Set the vertical physical position in page mode (14.21) (With Parameter)
    public void GS_VPP(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.GS_VPP);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Set the vertical logical position on page mode (14.22) (With Parameter)
    public void GS_VLP(String n) {
        if (mValidator.check(n, 0, 576)) {
            transfer(myCommand.GS_VLP);
            transfer(convertStringToTwoByteArray(n));
        }
    }

    //Print bit image (14.23) (With Parameter)
    public void ESC_BIMG(Bitmap bmp, byte[] n) {
        /*
        Here, In Bit Image Implementation.
        m = 0,1,32 or 33
         */

        /***Need to implement***/
        transfer(myCommand.ESC_BIMG);
        transfer(n);

    }

    //Print raster bit image (14.24) (With Parameter)
    public void GS_v(Bitmap bmp, String n) {
        if (mValidator.check(n, 0, 3)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(myCommand.GS_v);
                out.write(convertStringToByteArray(n));
                out.write(Utils_1.decodeRasterBitImage(Utils.floydSteinbergDithering(bmp)));
                transfer(out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*transfer(myCommand.GS_v);
            transfer(convertStringToByteArray(n));
            transfer(Utils_1.decodeRasterBitImage(Utils.floydSteinbergDithering(bmp)));*/
        }
    }

    //Print NV Bit Image (14.25) (With Parameter)
    public void FS_PP(String select, String mode) {
        if (mValidator.check(select, 1, 255)
                && mValidator.check(mode, 0, 3)) {
            transfer(myCommand.FS_P);
            transfer(convertStringToByteArray(select));
            transfer(convertStringToByteArray(mode));
        }
    }

    //Select NV Bit Image (14.25) (With Parameter)
    public void FS_PS(String select, String mode, String offset) {
        if (mValidator.check(select, 1, 255)
                && mValidator.check(mode, 4, 7)
                && mValidator.check(offset, 0, 255)) {

            transfer(myCommand.FS_P);
            transfer(convertStringToByteArray(select));
            transfer(convertStringToByteArray(mode));
            transfer(convertStringToByteArray(offset));
        }
    }

    /**
     * Define NV Bit Image (14.26) (With Parameter)
     * TODO - Image size should be as per document
     */
    public void FS_Q(Bitmap[] bmp, String n) {
        try {
            if (bmp.length == Integer.parseInt(n)) {
                if (mValidator.check(n, 1, 255)) {
                    transfer(myCommand.FS_Q);
                    transfer(convertStringToByteArray(n));
                    //Transfer Image
                    for (int z = 0; z < Integer.parseInt(n); z++)
                        transfer(Utils_1.decodeNVBitImage(Utils.floydSteinbergDithering(
                                RotateBitmap(getResizedBitmap(bmp[z]), 90))));
                }
            } else {
                throw new NVBitException("Incorrect Number of NV Bit images provided!");
            }
        } catch (NVBitException e) {
            Log.e("Error", e.getMessage());
        }
    }

    //Select LSB-MSB direction in image (14.27) (With Parameter)
    public void DC2_DIR(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.DC2_DIR);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set print position of HRI characters (14.28) (With Parameter)
    public void GS_H(String n) {
        if (mValidator.check(n, 0, 3)) {
            transfer(myCommand.GS_H);
            transfer(convertStringToByteArray(n));
        }
    }

    //Select HRI character size (14.29) (With Parameter)
    public void GS_F(String n) {
        if (mValidator.check(n, 0, 1)) {
            transfer(myCommand.GS_F);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set barcode height (14.30) (With Parameter)
    public void GS_h(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.GS_h);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set barcode width (14.31) (With Parameter)
    public void GS_w(String n) {
        if (mValidator.check(n, 2, 6)) {
            transfer(myCommand.GS_w);
            transfer(convertStringToByteArray(n));
        }
    }

    //Set N:W aspect of the barcode (14.32) (With Parameter)
    public void DC2_ARB(String n) {
        if (mValidator.check(n, 0, 2)) {
            transfer(myCommand.DC2_ARB);
            transfer(convertStringToByteArray(n));
        }
    }

    //Print Barcode (14.33) (With Parameter)
    public void GS_k(BarcodeType barcodeType, String barcodeData) {
        try {
            if (barcodeType.equals(BarcodeType.CODE93)) {
                code93(barcodeType, barcodeData);
            } if (barcodeType.equals(BarcodeType.CODE128)) {
                code128(barcodeType, barcodeData);
            } else if (mValidator.check_barcode(barcodeType, barcodeData)) {
                //TODO: Change this later
                transfer(myCommand.GS_k);
                transfer(convertStringToByteArray(Integer.toString(barcodeType.getBarcode_type())));
                transfer(convertStringToByteArray(String.valueOf(barcodeData.length())));
                transfer(barcodeData);
            } else {
                throw new ValueOutOfBoundException("Invalid Input :" + barcodeData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Initialize the printer (14.34)
    public void ESC_INIT() {
        transfer(myCommand.ESC_INIT);
    }

    //Set the print density (14.40) (With Parameter)
    public void DC2_PD(String n) {
        if (mValidator.check(n, 65, 135)) {
            transfer(myCommand.DC2_PD);
            transfer(convertStringToByteArray(n));
        }
    }

    /*Cue the marked form(feed the form to print start position)
     * (14.41)
     */
    public void GS_MF() {
        transfer(myCommand.GS_MF);
    }

    //Cut the form (14.43)
    public void ESC_I() {
        transfer(myCommand.ESC_I);
    }

    //Inform system time of the host (14.51) (With Parameter)
    public void GS_i(byte[] n) {
        transfer(myCommand.GS_i);
        transfer(n);
    }

    //Download the firmware (14.52)
    public void ACK() {
        transfer(myCommand.ACK);
    }

    //Feed the form in pixels (14.54) (With Parameter)
    public void GS_d(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.GS_d);
            transfer(convertStringToByteArray(n));
        }
    }

    //Turn Underline button ON/OFF (14.70)
    public void ESC_hyphen(String n) {
        if (mValidator.check(n, 0, 2)) {
            transfer(myCommand.ESC_hyphen);
            transfer(convertStringToByteArray(n));
        }
    }

    //Select default line spacing (14.71)
    public void ESC_2() {
        transfer(myCommand.ESC_2);
    }

    //Turn emphasized mode on/off (14.72)
    public void ESC_E(String n) {
        if (mValidator.check(n, 0, 255)) {
            transfer(myCommand.ESC_E);
            transfer(convertStringToByteArray(n));
        }
    }

    //Select print direction in page mode (14.73)
    public void ESC_T(String n) {
        if (mValidator.check(n, 0, 3)) {
            transfer(myCommand.ESC_T);
            transfer(convertStringToByteArray(n));
        }
    }

    //Select justification (14.74)
    public void ESC_a(String n) {
        if (mValidator.check(n, 0, 2)) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(myCommand.ESC_a);
                out.write(convertStringToByteArray(n));
                transfer(out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Set up and print the symbol (14.75) PART - 1
    public void pdf417() {
        transfer(myCommand.GS_C_k);
        //TODO: Need to implement later
    }

    //Set up and print the symbol (14.75) PART - 2
    public void QrCode(String size, QRErrCorrLvl qrErrCorrLvl, String userData) {
        try {
            if (userData == null || userData.equals("") || size.equals("0")) {
                Log.i(TAG, "String empty or Size is zero");
                throw new InvalidParameterException("Check the parameters provided.");
            } else {

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                //Set the size of module
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x31, 0x43});
                out.write(convertStringToByteArray(size));

                //Select the error correction level <Function 169>
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x31, 0x45});
                out.write(convertStringToByteArray(Integer.toString(qrErrCorrLvl.getCorrectionlevel())));

                //Store the data in the symbol storage area <Function 180>
                out.write(myCommand.GS_C_k);
                out.write(convertStringToTwoByteArray(String.valueOf(userData.length() + 3)));
                out.write(new byte[]{0x31, 0x50, 0x30});
                out.write(userData.getBytes());

                //Print the symbol data in the symbol storage area <Function 181>
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x31, 0x51, 0x30});

                transfer(out.toByteArray());

                //TODO: Remember sometimes individual byte transfer may not work properly so use the above method
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Print downloaded bit image (14.77)
    public void GS_FS(String n) {
        if (mValidator.check(n, 0, 3)) {
            transfer(myCommand.GS_FS);
            transfer(convertStringToByteArray(n));
        }
    }

    //Turn white/black reverse print mode on/off (14.78)
    public void GS_B(String n) {
        if (mValidator.check(n, 0, 255)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(myCommand.GS_B);
                out.write(convertStringToByteArray(n));
                transfer(out.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Select cut mode and cut paper (14.79)
    public void GS_V(FunctionType functionType, CutCommand mode, String n) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (functionType.equals(FunctionType.C)) {
                if (mode == CutCommand.FULLCUT) {
                    if (mValidator.check(n, 0, 255)) {
                        out.write(myCommand.GS_V);
                        out.write(new byte[]{0x61});
                        out.write(convertStringToByteArray(n));
                        transfer(out.toByteArray());
                    }
                } else if (mode == CutCommand.PARTIALCUT) {
                    if (mValidator.check(n, 0, 255)) {
                        out.write(myCommand.GS_V);
                        out.write(new byte[]{0x62});
                        out.write(convertStringToByteArray(n));
                        transfer(out.toByteArray());
                    }
                }
            } else if (functionType.equals(FunctionType.B)) {
                if (mode == CutCommand.FULLCUT) {
                    if (mValidator.check(n, 0, 255)) {
                        out.write(myCommand.GS_V);
                        out.write(new byte[]{0x41});
                        out.write(convertStringToByteArray(n));
                        transfer(out.toByteArray());
                    }
                } else if (mode == CutCommand.PARTIALCUT) {
                    if (mValidator.check(n, 0, 255)) {
                        out.write(myCommand.GS_V);
                        out.write(new byte[]{0x42});
                        out.write(convertStringToByteArray(n));
                        transfer(out.toByteArray());
                    }
                }
            } else if (functionType.equals(FunctionType.A)) {
                if (mode == CutCommand.FULLCUT) {
                    out.write(myCommand.GS_V);
                    out.write(new byte[]{0x30});
                    transfer(out.toByteArray());
                } else if (mode == CutCommand.PARTIALCUT) {
                    out.write(myCommand.GS_V);
                    out.write(new byte[]{0x31});
                    transfer(out.toByteArray());
                }
            } else {
                throw new InvalidParameterException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    Extra Utility methods
     */
    //Resize the Bitmap Image
    public Bitmap getResizedBitmap(Bitmap bm) {

        int width = (bm.getWidth() / 8) * 8;
        int height = (bm.getHeight() / 8) * 8;

        float scaleWidth = ((float) width / bm.getWidth());
        float scaleHeight = ((float) height / bm.getHeight());

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                matrix, false);

        return resizedBitmap;

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        matrix.postScale(-1, 1, 0, 0);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /***
     * Convert String to Byte Array
     * Note: tmp = 0 (Default)
     */
    /*** Merge these methods later ***/
    public byte[] convertStringToByteArray(String n) {
        byte[] tmp = new byte[1];
        tmp[0] = (byte) Integer.parseInt(n);
        return tmp;
    }

    public byte[] convertStringToTwoByteArray(String n) {
        byte[] tmp = new byte[2];//nL and nH
        int temp = Integer.parseInt(n);
        tmp[0] = (byte) (temp % 256);
        temp = temp / 256;
        tmp[1] = (byte) (temp % 256);
        return tmp;
    }

    /**
     * Vendor Requests (OR) Control Transfer
     */
    public String controlTransfer(VendorRequest vendorRequest) {

        //TODO: Need to implement, Ask Praveen for Document.
        byte[] sendCmd = new byte[8];
        sendCmd[0] = (byte) 0xC1;
        sendCmd[1] = vendorRequest.getbRequest();
        sendCmd[2] = 0x00;
        sendCmd[3] = 0x00;
        sendCmd[4] = 0x00;
        sendCmd[5] = 0x00;
        sendCmd[6] = 0x00;
        sendCmd[7] = vendorRequest.getwLength();

        mReceivedDataFromPrinter = transfer(sendCmd);

        return mReceivedDataFromPrinter;
    }

    /**
     * TODO - Example Barcode Method from zxing library
     */
    /*
    private void printAdditionalBarcode(ExtraBarcodeType extraBarcodeType, String width, String height, String userData,
                             ImageMode imageMode){
        //TODO: Width and Height parameters needs to be constrainted.
        MultiFormatWriter mMultiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = null;
        try {
            if(extraBarcodeType.getExtraBarcodeType().equals("0")){
                bitMatrix = mMultiFormatWriter.encode(userData,
                        BarcodeFormat.PDF_417, Integer.parseInt(width), Integer.parseInt(height));
            }
            else if(extraBarcodeType.getExtraBarcodeType().equals("1")){
                bitMatrix = mMultiFormatWriter.encode(userData,
                        BarcodeFormat.CODE_128, Integer.parseInt(width), Integer.parseInt(height));
            }else{
                throw new InvalidParameterException("Invalid Barcode type selected");
            }

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            this.GS_v(bitmap, imageMode.getMode());//TODO - Set default to 1

        } catch (Exception e){
            Log.e("Barcode Error:",e.getMessage());
        }
    }*/

    /**
     * Print PDF417 barcode
     * @param No_of_Columns
     * @param No_of_Rows
     * @param mode
     * @param level
     * @param options
     * @param userData
     * Additional Info: Change the row and column size accordingly w.r.t Input string
     */
    public void printPDF417(String No_of_Columns,
                            String No_of_Rows,
                            PDF417ErrorCorrectionMode mode,
                            PDF417ErrorCorrectionLevel level,
                            PDF417Options options,
                            String userData) {
        try {
            if ((mValidator.check(No_of_Columns, 0, 30)) &&
                    (mValidator.check(No_of_Rows, 3, 90) || No_of_Rows.equals("0"))) {

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                //Set the number of columns in the data region
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x41});
                out.write(convertStringToByteArray(No_of_Columns));
                //Set the number of rows
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x42});
                out.write(convertStringToByteArray(No_of_Rows));
                //Set the width of the module
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x43});
                out.write(convertStringToByteArray(Integer.toString(3)));//TODO:Changes with Model
                //Set the row height
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x44});
                out.write(convertStringToByteArray(Integer.toString(3)));//TODO:Changes with Model
                //Set the error correction level
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x45});
                out.write(convertStringToByteArray(mode.getMode()));
                out.write(convertStringToByteArray(level.getCorrectionLevel()));

                //Select the options
                out.write(myCommand.GS_C_k);
                //out.write(convertStringToTwoByteArray(String.valueOf(userData.length() + 3)));
                out.write(new byte[]{0x03, 0x00, 0x30, 0x46});
                out.write(convertStringToByteArray(options.getOption()));

                //Store the data in the symbol storage area
                out.write(myCommand.GS_C_k);
                out.write(convertStringToTwoByteArray(String.valueOf(userData.length() + 3)));//TODO:Check why?
                out.write(new byte[]{0x30, 0x50, 0x30});
                //Userdata
                out.write(userData.getBytes());

                //Print the symbol data in the symbol storage area
                out.write(myCommand.GS_C_k);
                out.write(new byte[]{0x03, 0x00, 0x30, 0x51, 0x30});

                transfer(out.toByteArray());

                out.reset();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Validation Pending
    private void code93(BarcodeType barcodeType, String userData) {
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write(myCommand.GS_k);
            out.write(barcodeType.getBarcode_type());

            ByteArrayOutputStream tmp = new ByteArrayOutputStream();


            for (int nInputIdx = 0; nInputIdx < userData.length(); ++nInputIdx) {
                for (int nSearchIdx = 0; nSearchIdx < 128; nSearchIdx++) {
                    char ch = userData.charAt(nInputIdx);
                    if (CODE93.code93model[nSearchIdx][0] == ch) {
                        if ((CODE93.code93model[nSearchIdx][2]) == -1) {
                            tmp.write(CODE93.code93model[nSearchIdx][1]);
                        } else {
                            tmp.write(CODE93.code93model[nSearchIdx][1]);
                            tmp.write(CODE93.code93model[nSearchIdx][2]);
                        }
                        break;
                    }
                }
            }

            out.write(tmp.size()+1); // Add 1 for Ending character {0x47}
            out.write(tmp.toByteArray());
            out.write(new byte[]{0x47}); // Ending Character more than {0x47}

            transfer(out.toByteArray());
            out.reset();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Needs more clarity. Currently, SubsetB only working...
    public void code128(BarcodeType barcodeType, String userData) {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write(myCommand.GS_k);
            out.write(barcodeType.getBarcode_type());
            out.write(userData.length() + 2); // Starting and Ending values 103 to 105 / 106

            //nCurrentSubset = subset.getSubset();
            checkDigit(userData);
            //int nCurrentSubset = subset.getSubset();

            ByteArrayOutputStream tmp = new ByteArrayOutputStream();

            if(nCurrentSubset == CODE128Subset.SUBSETA.getIndex())
                tmp.write(103);
            if (nCurrentSubset == CODE128Subset.SUBSETB.getIndex())
                tmp.write(104);
            if (nCurrentSubset == CODE128Subset.SUBSETC.getIndex())
                tmp.write(105);

            for (int nInputIdx = 0; nInputIdx < userData.length(); ++nInputIdx) {
                int ch = userData.charAt(nInputIdx);

                int checkit = 0;

                checkit = CODE128.g_nASCIItoCode128SubsetAB[CODE128Subset.SUBSETB.getIndex()][ch];
                if (checkit >= 0)
                {
                    if (nCurrentSubset != CODE128Subset.SUBSETB.getIndex())
                    {
                        tmp.write(104);
                        nCurrentSubset = CODE128Subset.SUBSETB.getIndex();
                    }
                }
                else
                {
                    checkit = CODE128.g_nASCIItoCode128SubsetAB[CODE128Subset.SUBSETA.getIndex()][ch];
                    if (nCurrentSubset != CODE128Subset.SUBSETA.getIndex())
                    {
                        tmp.write(103);
                        nCurrentSubset = CODE128Subset.SUBSETA.getIndex();
                    }
                }


                if (nCurrentSubset == CODE128Subset.SUBSETC.getIndex())
                {
                    if (CODE128.g_nASCIItoCode128SubsetAB[0][ch] == 101)
                    {
                        tmp.write(101);
                        nCurrentSubset = CODE128Subset.SUBSETA.getIndex();
                    }
                    else if (CODE128.g_nASCIItoCode128SubsetAB[0][ch] == 100)
                    {
                        tmp.write(100);
                        nCurrentSubset = CODE128Subset.SUBSETB.getIndex();
                    }
                    else if (CODE128.g_nASCIItoCode128SubsetAB[0][ch] == 102)
                    {
                        tmp.write(100);
                    }
                    else
                    {
                        int ipCode = Integer.parseInt(userData.substring(ch, ++nInputIdx));
                        tmp.write(CODE128.g_nASCIItoCode128SubsetAB[0][ipCode]);
                    }
                }
                else
                {
                    // handle upper ASCII characters if necessary
                    if (ch < -1)
                        ch = ch & 255;

                    ch = CODE128.g_nASCIItoCode128SubsetAB[nCurrentSubset][ch];
                    tmp.write(ch);

                    // if switch in SUBSETA
                    if (nCurrentSubset == CODE128Subset.SUBSETA.getIndex())
                    {
                        if (ch == 100)
                            nCurrentSubset = CODE128Subset.SUBSETB.getIndex();
                        else if (ch == 99)
                            nCurrentSubset = CODE128Subset.SUBSETC.getIndex();
                    }
                    // if switch in SUBSETB
                    else if (nCurrentSubset == CODE128Subset.SUBSETB.getIndex())
                    {
                        if (ch == 101)
                            nCurrentSubset = CODE128Subset.SUBSETA.getIndex();
                        else if (ch == 99)
                            nCurrentSubset = CODE128Subset.SUBSETC.getIndex();
                    }
                    else if( ch == 98)
                    {
                        ch = userData.charAt(++nInputIdx);
                        if (nCurrentSubset == CODE128Subset.SUBSETA.getIndex())
                            tmp.write(CODE128.g_nASCIItoCode128SubsetAB[1][ch]);
                        else
                            tmp.write(CODE128.g_nASCIItoCode128SubsetAB[0][ch]);
                    }
                }

            }

            out.write(tmp.toByteArray());
            out.write(106); // Ending Character 106

            transfer(out.toByteArray());
            out.reset();
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkDigit(String userData)
    {
        char ch = userData.charAt(0);
        long temp = CODE128.g_nASCIItoCode128SubsetAB[1][ch];
        if (temp > 0)
        {
            nCurrentSubset = CODE128Subset.SUBSETB.getIndex();
        }
        else
        {
            nCurrentSubset = CODE128Subset.SUBSETA.getIndex();
        }
    }

}
