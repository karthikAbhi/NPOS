package com.nash.npos;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.nash.nposlibrary.BTReceiver;
import com.nash.nposlibrary.BT_Printer;
import com.nash.nposlibrary.BarcodeType;
import com.nash.nposlibrary.CharacterFontType;
import com.nash.nposlibrary.CutCommand;
import com.nash.nposlibrary.FunctionType;
import com.nash.nposlibrary.PDF417ErrorCorrectionLevel;
import com.nash.nposlibrary.PDF417ErrorCorrectionMode;
import com.nash.nposlibrary.PDF417Options;
import com.nash.nposlibrary.Printer;
import com.nash.nposlibrary.QRErrCorrLvl;
import com.nash.nposlibrary.USBReceiver;
import com.nash.nposlibrary.USB_Printer;
import com.nash.nposlibrary.VendorRequest;
import com.nash.nposlibrary.Wifi_Printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NPOS";
    private static final int REQUEST_ENABLE_BT = 1;
    private Context mContext;
    private int mConnectionId = -1;

    private byte[] mReceivedBuffer;

    private Printer mPrinter;

    Button mBtnConnect, mBtnCommand, mBtnClose;

    private BluetoothAdapter mBluetoothAdapter;

    private ArrayList<String> mBTDevices = new ArrayList<>();

    ArrayAdapter<String> mBTAdapter;

    AlertDialog.Builder mBuilder;

    // --- UI - Button References ---

    private Button mTestPrintButton;

    private Button mPrintButton;
    private Button mLFCommandButton;
    private Button mFFCommandButton;
    private Button mPMCommandButton;
    private Button mSMCommandButton;
    private Button mHTCommandButton;
    private Button mInitializePrinterCommandButton;
    private Button mGSMFCommandButton;
    private Button mCutFormCommandButton;
    private Button mFFIPIXELSCommandButton;
    private Button mPDFF2FCommandButton;
    private Button mPDPMCommandButton;
    private Button mPDFNLCommandButton;
    private Button mSLFCommandButton;
    private Button mSGRSCCommandButton;
    private Button mSPPMCommandButton;
    private Button mSelectFontCommandButton;
    private Button mSetLeftMarginCommandButton;
    private Button mSetWidthOfPrintAreaCommandButton;
    private Button mSpecifyPrintAreaOnPageModeCommandButton;
    private Button mSetPhysicalPositionCommandButton;
    private Button mSetLogicalPositionCommandButton;
    private Button mSetVerticalPhysicalPositionOnPageModeCommandButton;
    private Button mSelectLSB_MSBDirectionInImageCommandButton;
    private Button mSetVerticalLogicalPositionOnPageModeCommandButton;
    private Button mSetPrintPositionOfHRICharCommandButton;
    private Button mSelectHRICharacterSizeCommandButton;
    private Button mSetBarcodeHeightCommandButton;
    private Button mSetBarcodeWidthCommandButton;
    private Button mSetNWAspectBarcode;
    private Button mPrintBarcodeButton;
    private Button mSetPrintDensity;
    private Button mInformSysTimeOfHostCommandButton;
    //private Button mPrintBitImageCommandButton;
    private Button mPrintRasterBitImageCommandButton;
    private Button mPrintNVBitImageCommandButton;
    private Button mSelectNVBitImageCommandButton;
    private Button mDefineNVBitImageCommandButton;
    private Button mtUOnOffCommandButton;
    private Button mSDLSCommandButton;
    private Button mTEMOnOffCommandButton;
    private Button mSPDPMCommandButton;
    private Button mSJCommandButton;
    private Button mQRCodeCommandButton;
    private Button mTurnWBRPOnOffCommandButton;
    private Button mSCMCPCommandButton;
    private Button mPDF417CommandButton;

    private RadioGroup mRadioGroupQR;
    private RadioButton mRadioButtonQR;

    private RadioGroup mRadioGroupFontType;
    private RadioButton mRadioButtonFontType;

    private RadioGroup mRadioGroupFT;
    private RadioButton mRadioButtonFT;
    private RadioGroup mRadioGroupCT;
    private RadioButton mRadioButtonCT;

    private Spinner mBarcodeSpinner;
    private int mBarcodeTypeSelected;

    private CheckBox mCheckBoxDoubleHeight;
    private CheckBox mCheckBoxDoubleWidth;

    //Control Transfer
    private Button mControlTransferButton;
    private int mVendorRequestTypeSelected;

    /**
     * UI - EditText References
     */
    private EditText mSampleTextEditText;
    private EditText mPDFF2FEditText;
    private EditText mPDFNLEditText;
    private EditText mSLFEditText;
    private EditText mSGRSCEditText;
    private EditText mSelectFontEditText;
    private EditText mSetLeftMarginEditText;
    private EditText mSetWidthOfPrintAreaEditText;
    private EditText mSPAPMxaxisEditText, mSPAPMyaxisEditText, mSPAPMxlengthEditText,
            mSPAPMylengthEditText;
    private EditText mSetPhysicalPositionEditText;
    private EditText mSetLogicalPositionEditText;
    private EditText mSetVerticalPhysicalPositionOnPageModeEditText;
    private EditText mSetVerticalLogicalPositionOnPageModeEditText;
    //Raster Bit - Printing
    private EditText mModeOfRasterBitEditText;
    //NV Bit - Printing
    private EditText mSelectPNVBitEditText, mModePNVBitEditText;
    //NV Bit - Selection
    private EditText mSelectSNVBitEditText, mModeSNVBitEditText, mOffsetSNVBitEditText;
    private EditText mNumberOfNVBitImagesEditText;
    private EditText mSelectLSB_MSBDirectionInImageEditText;
    private EditText mSetPrintPositionOfHRICharEditText;
    private EditText mSelectHRICharacterSizeEditText;
    private EditText mSetBarcodeHeightEditText;
    private EditText mSetBarcodeWidthEditText;
    private EditText mSetNWAspectBarcodeEditText;
    private EditText mBarcodeDataEditText;
    private EditText mSetPrintDensityEditText;
    private EditText mFFIPIXELSEditText;
    private EditText mUnderLineEditText;
    private EditText mTEMOnOffEditText;
    private EditText mSPDPMEditText;
    private EditText mSJEditText;
    private EditText mQRSizeEditText, mQRUserDataEditText;
    private EditText mTurnWBRPOnOffEditText;
    private EditText mSCMCPEditText;

    //TODO Control Transfer
    private Spinner mVendorRequestSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        //mBtnConnect = findViewById(R.id.btn_connect);
        //mBtnCommand = findViewById(R.id.btn_command);
        //mBtnClose = findViewById(R.id.btn_close);

        /*mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConnectionId == ConnectionType.USB.getTypeId()){

                }
                if(mConnectionId == ConnectionType.BT.getTypeId()){

                }
                if(mConnectionId == ConnectionType.WIFI.getTypeId()){

                }
            }
        });

        mBtnCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPrinter.printText("Hello\n");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrinter.closeConnection();
            }
        });*/


        // --- UI Components - EditText Reference Creation ---

        mSampleTextEditText = findViewById(R.id.sampleTextEditText);
        mPDFF2FEditText = findViewById(R.id.pdff2fEditText);
        mPDFNLEditText = findViewById(R.id.pdfnlEditText);
        mSLFEditText = findViewById(R.id.slfEditText);
        mSGRSCEditText = findViewById(R.id.sgrscEditText);
        mSelectFontEditText = findViewById(R.id.selFontEditText);
        mSetLeftMarginEditText = findViewById(R.id.setLeftMarginEditText);
        mSetWidthOfPrintAreaEditText = findViewById(R.id.setWidthOfPrintAreaEditText);
        mSPAPMxaxisEditText = findViewById(R.id.spapmxaxisEditText);
        mSPAPMyaxisEditText = findViewById(R.id.spapmyaxisEditText);
        mSPAPMxlengthEditText = findViewById(R.id.spapmxlengthEditText);
        mSPAPMylengthEditText = findViewById(R.id.spapmylengthEditText);
        mSetPhysicalPositionEditText = findViewById(R.id.setPhysicalPositionEditText);
        mSetLogicalPositionEditText = findViewById(R.id.setLogicalPositionEditText);
        mSetVerticalPhysicalPositionOnPageModeEditText = findViewById(R.id.setVPPOnPageModeEditText);
        mSetVerticalLogicalPositionOnPageModeEditText = findViewById(R.id.setVLPOnPageModeEditText);
        mModeOfRasterBitEditText = findViewById(R.id.modeOfRasterBitEditText);
        mSelectPNVBitEditText = findViewById(R.id.selectPNVBitEditText);
        mModePNVBitEditText = findViewById(R.id.modePNVBitEditText);
        mSelectSNVBitEditText = findViewById(R.id.selectSNVBitEditText);
        mModeSNVBitEditText = findViewById(R.id.modeSNVBitEditText);
        mOffsetSNVBitEditText = findViewById(R.id.offsetSNVBitEditText);
        mNumberOfNVBitImagesEditText = findViewById(R.id.numberOfNVBitImagesEditText);
        mSelectLSB_MSBDirectionInImageEditText = findViewById(R.id.selectLSB_MSBDirInImgEditText);
        mSetPrintPositionOfHRICharEditText = findViewById(R.id.setPosHRICharEditText);
        mSelectHRICharacterSizeEditText = findViewById(R.id.selectHRICharSizeEditText);
        mSetBarcodeHeightEditText = findViewById(R.id.setBarcodeHeightEditText);
        mSetBarcodeWidthEditText = findViewById(R.id.setBarcodeWidthEditText);
        mSetNWAspectBarcodeEditText = findViewById(R.id.setNWAspectOfBarcodeEditText);
        mBarcodeDataEditText = findViewById(R.id.barcodeDataEditText);
        mSetPrintDensityEditText = findViewById(R.id.setPrintDensityEditText);
        mFFIPIXELSEditText = findViewById(R.id.ffinPixelsEditText);
        mUnderLineEditText = findViewById(R.id.underLineEditText);
        mTEMOnOffEditText = findViewById(R.id.turnEmphasizedOnOffEditText);
        mSPDPMEditText = findViewById(R.id.selectPrintDirInPageModeEditText);
        mSJEditText = findViewById(R.id.selectJustificationEditText);
        mQRSizeEditText = findViewById(R.id.qrSizeEditText);
        mQRUserDataEditText = findViewById(R.id.qrUserDataEditText);
        mTurnWBRPOnOffEditText = findViewById(R.id.turnBlackWhiteRevPrintModeOnOffEditText);
        mSCMCPEditText = findViewById(R.id.selectCutModeCutPaperEditText);

        // --- UI Components - Button Reference Creation ---
        // Test Print Button
        mTestPrintButton = findViewById(R.id.btn_testprint);
        //Print Text
        mPrintButton = findViewById(R.id.printButton);
        //Print Barcode
        //mPrintBarcodeButton = findViewById(R.id.printBarcodeButton);
        //LF Command
        mLFCommandButton = findViewById(R.id.lfCommandButton);
        //FF Command
        mFFCommandButton = findViewById(R.id.ffCommandButton);
        //Print data and Feed form to forward Command
        mPDFF2FCommandButton = findViewById(R.id.pdff2fButton);
        //Print data on feed n lines Command
        mPDFNLCommandButton = findViewById(R.id.pdfnlButton);
        //Print data on page mode Command
        mPDPMCommandButton = findViewById(R.id.pdpmButton);
        //Set the line feed amount Command
        mSLFCommandButton = findViewById(R.id.slfButton);
        //Set the gap of right side of the characters Command (14.07)
        mSGRSCCommandButton = findViewById(R.id.sgrscButton);
        //Set parameters of Print mode Command (14.08)
        mRadioGroupFontType = findViewById(R.id.radioGroup_FontType);
        mRadioButtonFontType = findViewById(R.id.font_A);
        mCheckBoxDoubleHeight = findViewById(R.id.checkbox_DoubleHeight);
        mCheckBoxDoubleWidth = findViewById(R.id.checkbox_DoubleWidth);
        mSPPMCommandButton = findViewById(R.id.sppmButton);
        //Select the font Command (14.09)
        mSelectFontCommandButton = findViewById(R.id.selFontButton);
        //Page Mode Command (14.13)
        mPMCommandButton = findViewById(R.id.pageModeButton);
        //Standard Mode Command (14.14)
        mSMCommandButton = findViewById(R.id.standardModeButton);
        //Horizontal Tab Command (14.15)
        mHTCommandButton = findViewById(R.id.horizontalTabButton);
        //Set the left side margin Command (14.16)
        mSetLeftMarginCommandButton = findViewById(R.id.setLeftMarginButton);
        //Set the width of print area Command (14.17)
        mSetWidthOfPrintAreaCommandButton = findViewById(R.id.setWidthOfPrintAreaButton);
        //Set the print area on page mode (14.18)
        mSpecifyPrintAreaOnPageModeCommandButton = findViewById(R.id.specifyPrintAreaOnPageModeButton);
        //Set the physical position (14.19)
        mSetPhysicalPositionCommandButton = findViewById(R.id.setPhysicalPositionButton);
        //Set the logical position (14.20)
        mSetLogicalPositionCommandButton = findViewById(R.id.setLogicalPositionButton);
        //Set the vertical physical position in page mode (14.21)
        mSetVerticalPhysicalPositionOnPageModeCommandButton = findViewById(R.id.setVPPOnPageModeButton);
        //Set the vertical logical position in page mode (14.22)
        mSetVerticalLogicalPositionOnPageModeCommandButton = findViewById(R.id.setVLPOnPageModeButton);
        //Print bit image (14.23)
        //mPrintBitImageCommandButton = findViewById(R.id.printBitImageButton);
        //Print raster bit image (14.24)
        mPrintRasterBitImageCommandButton = findViewById(R.id.printRasterBitImageButton);
        //Print NV Bit Image (14.25)
        mPrintNVBitImageCommandButton = findViewById(R.id.printNVBitImageButton);
        //Select NV Bit Image (14.25)
        mSelectNVBitImageCommandButton = findViewById(R.id.selectNVBitImageButton);
        //Define NV Bit Image (14.26)
        mDefineNVBitImageCommandButton = findViewById(R.id.defineNVBitImageButton);
        //Select LSB-MSB direction in image (14.27)
        mSelectLSB_MSBDirectionInImageCommandButton = findViewById(R.id.selectLSB_MSBDirInImgButton);
        //Set print position of HRI characters (14.28)
        mSetPrintPositionOfHRICharCommandButton = findViewById(R.id.setPosHRICharButton);
        //Select HRI character size (14.29)
        mSelectHRICharacterSizeCommandButton = findViewById(R.id.selectHRICharSizeButton);
        //Set barcode height (14.30)
        mSetBarcodeHeightCommandButton = findViewById(R.id.setBarcodeHeightButton);
        //Set barcode width (14.31)
        mSetBarcodeWidthCommandButton = findViewById(R.id.setBarcodeWidthButton);
        //Set N:W aspect of the barcode (14.32)
        mSetNWAspectBarcode = findViewById(R.id.setNWAspectOfBarcodeButton);
        //Barcode Spinner
        mBarcodeSpinner = findViewById(R.id.spinner_barcode_types);
        //Print Barcode (14.33)
        mPrintBarcodeButton = findViewById(R.id.printBarcodeButton);
        //Initialize Printer Command
        mInitializePrinterCommandButton = findViewById(R.id.initializePrinterButton);
        //Set print density (14.40)
        mSetPrintDensity = findViewById(R.id.setPrintDensityButton);
        //Cue the Marked form Command
        mGSMFCommandButton = findViewById(R.id.gsMFButton);
        //Cut form Command
        mCutFormCommandButton = findViewById(R.id.cutFormButton);
        //Inform system time of the host (14.51)
        mInformSysTimeOfHostCommandButton = findViewById(R.id.infoSysTimeHostButton);
        //Feed form in pixels Command (14.54)
        mFFIPIXELSCommandButton = findViewById(R.id.ffinPixelsButton);
        //Turn Underline button ON/OFF (14.70)
        mtUOnOffCommandButton = findViewById(R.id.underLineButton);
        //Select default line spacing (14.71)
        mSDLSCommandButton = findViewById(R.id.setDefaultLineSpaceButton);
        //Turn emphasized mode on/off (14.72)
        mTEMOnOffCommandButton = findViewById(R.id.turnEmphasizedOnOffButton);
        //Select print direction in page mode (14.73)
        mSPDPMCommandButton = findViewById(R.id.selectPrintDirInPageModeButton);
        //Select justification (14.74)
        mSJCommandButton = findViewById(R.id.selectJustificationButton);
        //Print QR Code (14.75) Part - 2
        mRadioGroupQR = findViewById(R.id.radioGroup_QRType);
        mRadioButtonQR = findViewById(R.id.qr_L);
        mQRCodeCommandButton = findViewById(R.id.qrCodeButton);
        //Print downloaded bit image (14.77)
        //mPDBICommandButton = findViewById(R.id.printDownldBitImgButton);
        //Turn white/black reverse print mode on/off (14.78)
        mTurnWBRPOnOffCommandButton = findViewById(R.id.turnBlackWhiteRevPrintModeOnOffButton);
        //Select cut mode and cut paper (14.79)
        mRadioGroupFT = findViewById(R.id.radioGroup_FunctionType);
        mRadioGroupCT = findViewById(R.id.radioGroup_CutType);
        mRadioButtonFT = findViewById(R.id.FT_a);
        mRadioButtonCT = findViewById(R.id.full_cut);
        mSCMCPCommandButton = findViewById(R.id.selectCutModeCutPaperButton);
        mPDF417CommandButton = findViewById(R.id.pdf417Button);

        //Vendor request
        mControlTransferButton = findViewById(R.id.controlTransferButton);
        mVendorRequestSpinner = findViewById(R.id.spinner_vendorrequesttypes);
        //Spinner for Vendor Request
        ArrayAdapter<String> vendorRequestTypesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.vendor_request_types));
        vendorRequestTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVendorRequestSpinner.setAdapter(vendorRequestTypesAdapter);

        mVendorRequestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mVendorRequestTypeSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mVendorRequestTypeSelected = 1; // Default: Reset Printer at position 1
            }
        });

        // --- IMPLEMENTATION ---

        // Test Print Command
        mTestPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.transfer(new byte[]{0x12, 0x54});
                }
            }
        });

        //Basic Print Command
        mPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(mPrinter != null){
                        mPrinter.printText(mSampleTextEditText.getText().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Connection is null",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mPDFF2FEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_J(mPDFF2FEditText.getText().toString());
                }
            }
        });
        //Print data and feed a line (14.01)
        mLFCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.LF();
                }
            }
        });
        //Print data and feed a page on page mode (14.02)
        mFFCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.FF();
                }
            }
        });
        //Print data on page mode (14.03)
        mPDPMCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_FF();
                }
            }
        });
        //Print data and feed the form to forward (14.04)
        mPDFF2FCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_J(mPDFF2FEditText.getText().toString());
                }
            }
        });
        //Print data and feed n lines (14.05)
        mPDFNLCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_D(mPDFNLEditText.getText().toString());
                }
            }
        });
        mSLFCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_3(mSLFEditText.getText().toString());
                }
            }
        });
        mSGRSCCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_SP(mSGRSCEditText.getText().toString());
                }
            }
        });
        //Need to implement parameters
        mSPPMCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    if (mRadioButtonFontType.getText().toString().equals("Font A")) {
                        mPrinter.ESC_PM(CharacterFontType.FONT_A,
                                mCheckBoxDoubleHeight.isChecked(),
                                mCheckBoxDoubleWidth.isChecked());
                    } else if (mRadioButtonFontType.getText().toString().equals("Font B")) {
                        mPrinter.ESC_PM(CharacterFontType.FONT_B,
                                mCheckBoxDoubleHeight.isChecked(),
                                mCheckBoxDoubleWidth.isChecked());
                    }
                }
            }
        });
        //Need to implement parameters
        mSelectFontCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.ESC_M(mSelectFontEditText.getText().toString());
                }
            }
        });
        mPMCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.PAGE_MODE();
                }
            }
        });

        mSMCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.STANDARD_MODE();
                }
            }
        });
        mHTCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.HT();
                }
            }
        });
        //Set the left side margin Command (14.16)
        mSetLeftMarginCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.GS_L(mSetLeftMarginEditText.getText().toString());
                }
            }
        });
        //Set the width of print area Command (14.17)
        mSetWidthOfPrintAreaCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()){
                    mPrinter.GS_W(mSetWidthOfPrintAreaEditText.getText().toString());
                }
            }
        });
        //Set the print area on page mode (14.18)
        mSpecifyPrintAreaOnPageModeCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_W(mSPAPMxaxisEditText.getText().toString(),
                            mSPAPMyaxisEditText.getText().toString(),
                            mSPAPMxlengthEditText.getText().toString(),
                            mSPAPMylengthEditText.getText().toString());
                }
            }
        });
        //Set the physical position (14.19)
        mSetPhysicalPositionCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_PP(mSetPhysicalPositionEditText.getText().toString());
                }
            }
        });
        //Set the logical position (14.20)
        mSetLogicalPositionCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_LP(mSetLogicalPositionEditText.getText().toString());
                }
            }
        });
        //Set the vertical physical position in page mode (14.21)
        mSetVerticalPhysicalPositionOnPageModeCommandButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkConnectionStatus()) {
                            mPrinter.GS_VPP(mSetVerticalPhysicalPositionOnPageModeEditText.
                                    getText().toString());
                        }
                    }
                });
        //Set the vertical logical position on page mode (14.22)
        mSetVerticalLogicalPositionOnPageModeCommandButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkConnectionStatus()) {
                            mPrinter.GS_VLP(mSetVerticalLogicalPositionOnPageModeEditText.
                                    getText().toString());
                        }
                    }
                });
        //Print bit image (14.23)
        /*mPrintBitImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    pickBitImage();
                }
            }
        });*/

        //Print raster bit image (14.24)
        mPrintRasterBitImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    pickRasterBitImage();
                }
            }
        });
        //Print NV Bit Image (14.25)
        mPrintNVBitImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.FS_PP(mSelectPNVBitEditText.getText().toString(),
                            mModePNVBitEditText.getText().toString());
                }
            }
        });
        //Select NV Bit Image (14.25)
        mSelectNVBitImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.FS_PS(mSelectSNVBitEditText.getText().toString(),
                            mModeSNVBitEditText.getText().toString(),
                            mOffsetSNVBitEditText.getText().toString());
                }
            }
        });
        //Define NV Bit Image (14.26)
        mDefineNVBitImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Integer.parseInt(mNumberOfNVBitImagesEditText.getText().toString()) > 0) {
                        pickNVBitImage();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Enter number of NV bit Images to be defined",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("Error", e.getMessage());
                }


            }
        });

        //Select LSB-MSB direction in image (14.27)
        mSelectLSB_MSBDirectionInImageCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.DC2_DIR(mSelectLSB_MSBDirectionInImageEditText.
                            getText().toString());
                }
            }
        });

        //Set print position of HRI characters (14.28)
        mSetPrintPositionOfHRICharCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_H(mSetPrintPositionOfHRICharEditText.getText().toString());
                }
            }
        });

        //Select HRI character size (14.29)
        mSelectHRICharacterSizeCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_F(mSelectHRICharacterSizeEditText.getText().toString());
                }
            }
        });

        //Set barcode height (14.30)
        mSetBarcodeHeightCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_h(mSetBarcodeHeightEditText.getText().toString());
                }
            }
        });

        //Set barcode width (14.31)
        mSetBarcodeWidthCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_w(mSetBarcodeWidthEditText.getText().toString());
                }
            }
        });

        //Set N:W aspect of the barcode (14.32)
        mSetNWAspectBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.DC2_ARB(mSetNWAspectBarcodeEditText.getText().toString());
                }
            }
        });

        //Print Barcode (14.33)
        ArrayAdapter<String> barcodeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.barcode_types));
        barcodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBarcodeSpinner.setAdapter(barcodeAdapter);

        mBarcodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBarcodeTypeSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBarcodeTypeSelected = 0;
            }
        });

        mPrintBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {

                    switch (mBarcodeTypeSelected) {
                        case 0:
                            mPrinter.GS_k(BarcodeType.UPC_A, mBarcodeDataEditText.getText().toString());
                            break;
                        case 1:
                            mPrinter.GS_k(BarcodeType.UPC_E, mBarcodeDataEditText.getText().toString());
                            break;
                        case 2:
                            mPrinter.GS_k(BarcodeType.JAN13, mBarcodeDataEditText.getText().toString());
                            break;
                        case 3:
                            mPrinter.GS_k(BarcodeType.JAN8, mBarcodeDataEditText.getText().toString());
                            break;
                        case 4:
                            mPrinter.GS_k(BarcodeType.CODE39, mBarcodeDataEditText.getText().toString());
                            break;
                        case 5:
                            mPrinter.GS_k(BarcodeType.ITF, mBarcodeDataEditText.getText().toString());
                            break;
                        case 6:
                            mPrinter.GS_k(BarcodeType.CODABAR, mBarcodeDataEditText.getText().toString());
                            break;
                        case 7:
                            mPrinter.GS_k(BarcodeType.CODE93, mBarcodeDataEditText.getText().toString());
                            break;
                        case 8:
                            mPrinter.GS_k(BarcodeType.CODE128, mBarcodeDataEditText.getText().toString());
                            break;
                        default:
                            mPrinter.GS_k(BarcodeType.UPC_A, mBarcodeDataEditText.getText().toString());
                            break;
                    }
                }
            }
        });

        //Initialize the printer (14.34)
        mInitializePrinterCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_INIT();
                }
            }
        });

        //Set print density (14.40)
        mSetPrintDensity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.DC2_PD(mSetPrintDensityEditText.getText().toString());
                }
            }
        });

        /*Cue the marked form(feed the form to print start position)
         * (14.41)
         */
        mGSMFCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_MF();
                }
            }
        });

        //Cut the form (14.43)
        mCutFormCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_I();
                }
            }
        });

        //Inform system time of the host (14.51)
        mInformSysTimeOfHostCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    byte[] a = new byte[6];

                    a[0] = (byte) Calendar.getInstance().get(Calendar.SECOND);//d1 - second
                    a[1] = (byte) Calendar.getInstance().get(Calendar.MINUTE);//d2 - minute
                    a[2] = (byte) Calendar.getInstance().get(Calendar.HOUR);//d3 - hour
                    a[3] = (byte) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//d4 - day
                    a[4] = (byte) Calendar.getInstance().get(Calendar.MONTH);//d5 - month
                    a[5] = (byte) Calendar.getInstance().get(Calendar.YEAR);//d6 - year

                    mPrinter.GS_i(a);
                }
            }
        });

        //Download the firmware (14.52)

        //Feed the form in pixels (14.54)
        mFFIPIXELSCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_d(mFFIPIXELSEditText.getText().toString());
                }
            }
        });

        //Turn Underline button ON/OFF (14.70)
        mtUOnOffCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_hyphen(mUnderLineEditText.getText().toString());
                }
            }
        });
        //Select default line spacing (14.71)
        mSDLSCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_2();
                }
            }
        });

        //Turn emphasized mode on/off (14.72)
        mTEMOnOffCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_E(mTEMOnOffEditText.getText().toString());
                }
            }
        });

        //Select print direction in page mode (14.73)
        mSPDPMCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_T(mSPDPMEditText.getText().toString());
                }
            }
        });

        //Select justification (14.74)
        mSJCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.ESC_a(mSJEditText.getText().toString());
                }
            }
        });

        //Set up and print the symbol (14.75)

        //Print QR Code (14.75) Part - 2
        mQRCodeCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    if (mRadioButtonQR.getText().toString().equals("L")) {
                        mPrinter.QrCode(mQRSizeEditText.getText().toString(), QRErrCorrLvl.L,
                                mQRUserDataEditText.getText().toString());
                    } else if (mRadioButtonQR.getText().toString().equals("M")) {
                        mPrinter.QrCode(mQRSizeEditText.getText().toString(), QRErrCorrLvl.M,
                                mQRUserDataEditText.getText().toString());
                    } else if (mRadioButtonQR.getText().toString().equals("Q")) {
                        mPrinter.QrCode(mQRSizeEditText.getText().toString(), QRErrCorrLvl.Q,
                                mQRUserDataEditText.getText().toString());
                    } else if (mRadioButtonQR.getText().toString().equals("H")) {
                        mPrinter.QrCode(mQRSizeEditText.getText().toString(), QRErrCorrLvl.H,
                                mQRUserDataEditText.getText().toString());
                    }
                }
            }
        });

        //Turn white/black reverse print mode on/off (14.78)
        mTurnWBRPOnOffCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.GS_B(mTurnWBRPOnOffEditText.getText().toString());
                }
            }
        });

        //Select cut mode and cut paper (14.79)
        mSCMCPCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    if (mRadioButtonCT.getText().toString().equals("Full Cut")) {
                        if (mRadioButtonFT.getText().toString().equals("A")) {
                            mPrinter.GS_V(FunctionType.A, CutCommand.FULLCUT, mSCMCPEditText.getText().toString());
                        } else if (mRadioButtonFT.getText().toString().equals("B")) {
                            mPrinter.GS_V(FunctionType.B, CutCommand.FULLCUT, mSCMCPEditText.getText().toString());
                        } else if (mRadioButtonFT.getText().toString().equals("C")) {
                            mPrinter.GS_V(FunctionType.C, CutCommand.FULLCUT, mSCMCPEditText.getText().toString());
                        }
                    } else if (mRadioButtonCT.getText().toString().equals("Partial Cut")) {
                        if (mRadioButtonFT.getText().toString().equals("A")) {
                            mPrinter.GS_V(FunctionType.A, CutCommand.PARTIALCUT, mSCMCPEditText.getText().toString());
                        } else if (mRadioButtonFT.getText().toString().equals("B")) {
                            mPrinter.GS_V(FunctionType.B, CutCommand.PARTIALCUT, mSCMCPEditText.getText().toString());
                        } else if (mRadioButtonFT.getText().toString().equals("C")) {
                            mPrinter.GS_V(FunctionType.C, CutCommand.PARTIALCUT, mSCMCPEditText.getText().toString());
                        }
                    }
                }
            }
        });


        //Basic Print Command
        mPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.printText(mSampleTextEditText.getText().toString());
                }
            }
        });

        //PDF417 Command
        mPDF417CommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    mPrinter.printPDF417("5",
                        "18",
                        PDF417ErrorCorrectionMode.LEVEL,
                        PDF417ErrorCorrectionLevel.LEVEL0,
                        PDF417Options.STANDARD,
                        mSampleTextEditText.getText().toString());
                }
            }
        });

        //TODO Control Transfer
        mControlTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnectionStatus()) {
                    switch (mVendorRequestTypeSelected) {
                        case 0:
                            mReceivedBuffer = mPrinter.controlTransfer(VendorRequest.REPLY_PRINTER_STATUS);
                            break;
                        case 1:
                            mReceivedBuffer = mPrinter.controlTransfer(VendorRequest.RESET_PRINTER);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(),
                                    "Select Vendor Request type..",
                                    Toast.LENGTH_SHORT).show();
                    }
                    //TODO: Parse the string using String.toBytes to get all the individual byte data.
                    Toast.makeText(getApplicationContext(),
                            "1st Byte: " + mReceivedBuffer[0] + "\n" +
                                    "2nd Byte: " + mReceivedBuffer[1] + "\n" +
                                    "3rd Byte: " + mReceivedBuffer[2] + "\n" +
                                    "4th Byte: " + mReceivedBuffer[3] + "\n" +
                                    "5th Byte: " + mReceivedBuffer[4] + "\n" +
                                    "6th Byte: " + mReceivedBuffer[5] + "\n" +
                                    "7th Byte: " + mReceivedBuffer[6] + "\n" +
                                    "8th Byte: " + mReceivedBuffer[7] + "\n" +
                                    "9th Byte: " + mReceivedBuffer[8] + "\n" +
                                    "10th Byte: " + mReceivedBuffer[9],
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Check the Printer connection status
    private boolean checkConnectionStatus(){
        if(mPrinter != null){
            return true;
        }
        Log.e(TAG, "mPrinter: Null Pointer Exception");
        Toast.makeText(getApplicationContext(),
                "Connection is Null",
                Toast.LENGTH_SHORT).show();
        return false;
    }

    //Newly Added
    /**
     * Broadcast Receiver to listen for all the actions specified in the Intent Filter
     */


    /***
     * Supporting Methods
     */
    private void pickBitImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop","true");
        intent.putExtra("scale",false);
        intent.putExtra("return-data",true);
        startActivityForResult(intent, 1);
    }

    private void pickRasterBitImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if(Build.VERSION.SDK_INT <=22){
            intent.putExtra("crop","true");
            intent.putExtra("scale",true);
        }
        intent.putExtra("return-data",true);
        startActivityForResult(intent, 2);
    }

    /*private void pickRasterBitImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.putExtra("crop","true");
        //intent.putExtra("scale",true);
        intent.putExtra("return-data",true);
        startActivityForResult(intent, 2);
    }*/

    private void pickNVBitImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        //intent.putExtra("return-data",true);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        //TODO - Bit Image Selection
        if(requestCode == 1){
            final Bundle extras = data.getExtras();
            if(extras != null){
                //Get Image from Gallery
                //mBmp = extras.getParcelable("data");
            }
        }
        //Raster Bit Image Selection
        else if(requestCode == 2){
            // For API version 22 and below
            if(Build.VERSION.SDK_INT <= 22){
                final Bundle extras = data.getExtras();
                if(extras != null){
                    //Get Image from Gallery
                    Bitmap bmp = extras.getParcelable("data");
                    mPrinter.GS_v(bmp, mModeOfRasterBitEditText.getText().toString());
                }
            }
            // For API version 23 and above
            else{
                if(data.getData() != null) {
                    Uri uri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.
                                getBitmap(getApplicationContext().getContentResolver(), uri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //TODO: Need to check the size of the image
                    //https://stackoverflow.com/questions/17840521/android-fatal-signal-11-sigsegv-at-0x636f7d89-code-1-how-can-it-be-tracked
                    if(bitmap.getHeight() <= 700 && bitmap.getWidth() <= 700){
                        mPrinter.GS_v(bitmap, mModeOfRasterBitEditText.getText().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Warning! Image Size too big...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        //NV Bit Image Selection
        else if(requestCode == 3){
            /**
             * Case 1: Number of NV Bit Images = 1
             * Case 2: Number of NV Bit Images > 1
             */
            //Case 1:
            if(data.getData() != null) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.
                            getBitmap(getApplicationContext().getContentResolver(), uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPrinter.FS_Q(new Bitmap[]{bitmap}, mNumberOfNVBitImagesEditText.
                        getText().toString());
            }
            //Case 2:
            else if(data.getClipData() != null){

                ClipData clipData = data.getClipData();
                int numberOfImages = clipData.getItemCount();
                Bitmap[] bitmapArray = new Bitmap[numberOfImages];
                for(int i = 0; i < numberOfImages; i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    try{
                        bitmapArray[i] = MediaStore.Images.Media.
                                getBitmap(getApplicationContext().getContentResolver(),uri);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                mPrinter.FS_Q(bitmapArray, mNumberOfNVBitImagesEditText.
                        getText().toString());
            }
            else{
                Toast.makeText(getApplicationContext(),"Invalid!",Toast.LENGTH_SHORT).show();
            }
        }

        // TODO: Bluetooth related code - need to shift to library
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

    /**
     * Callback method for Radiogroup - Function Type
     * @param view - Radiobutton selected
     */
    public void RadioButtonFuncTypeSelected(View view){
        int radioButtonId = mRadioGroupFT.getCheckedRadioButtonId();
        mRadioButtonFT = findViewById(radioButtonId);

        Toast.makeText(this, "Function Mode: "+ mRadioButtonFT.getText(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback method for Radiogroup - Cut Type
     * @param view
     */
    public void RadioButtonCutTypeSelected(View view){
        int radioButtonId = mRadioGroupCT.getCheckedRadioButtonId();
        mRadioButtonCT = findViewById(radioButtonId);

        Toast.makeText(this, "Cut Mode: "+ mRadioButtonCT.getText(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback method for Radiogroup - QR Correction Type
     * @param view
     */
    public void RadioButtonQRTypeSelected(View view){
        int radioButtonId = mRadioGroupQR.getCheckedRadioButtonId();
        mRadioButtonQR = findViewById(radioButtonId);

        Toast.makeText(this, "QR Correction Level: "+ mRadioButtonQR.getText(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback method for Radiogroup - Font Type Selection
     * @param view
     */
    public void RadioButtonFontTypeSelected(View view){
        int radioButtonId = mRadioGroupFontType.getCheckedRadioButtonId();
        mRadioButtonFontType = findViewById(radioButtonId);

        Toast.makeText(this, "Font Type: "+ mRadioButtonFontType.getText(), Toast.LENGTH_SHORT).show();
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
                        mBTAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1,
                                mBTDevices);

                        mBuilder = new AlertDialog.Builder(MainActivity.this);
                        mBuilder.setTitle("Select the Device")
                                .setAdapter(mBTAdapter, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        connectBt(mBTDevices.get(which));
                                        invalidateOptionsMenu();
                                    }
                                });

                        initBT();

                        mBuilder.create();
                        mBuilder.show();
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

    private void connectBt(String s) {
        mPrinter = BT_Printer.getInstance(getApplicationContext(), s);
        ConnectionType.BT.setConnectionState(true);
        mConnectionId = 1;
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
                Log.i(TAG, "Bluetooth was off... Turning on now...");
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

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){

            for(BluetoothDevice device : pairedDevices){
                String deviceName = device.getName(); // BT Device Name
                String hardwareAddress = device.getAddress(); // MAC Address

                Log.d(TAG, "Device name: " + deviceName);
                Log.d(TAG, "MAC Address: " + hardwareAddress);

                if(!mBTDevices.contains(deviceName)){
                    mBTDevices.add(deviceName);
                }
            }
            mBTAdapter.notifyDataSetChanged();
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
