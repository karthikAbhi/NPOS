package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum ExtraBarcodeType {

    PDF417("0"),
    CODE128("1");

    private String extraBarcodeType;

    ExtraBarcodeType(String extraBarcodeType) {
        this.extraBarcodeType = extraBarcodeType;
    }

    public String getExtraBarcodeType() {
        return extraBarcodeType;
    }
}
