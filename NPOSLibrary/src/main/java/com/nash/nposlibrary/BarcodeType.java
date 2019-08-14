package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum BarcodeType {

    UPC_A(65),
    UPC_E(66),
    JAN13(67),
    JAN8(68),
    CODE39(69),
    ITF(70),
    CODABAR(71),
    CODE93(72),
    CODE128(73);

    private int barcode_type;

    BarcodeType(int type){
        this.barcode_type = type;
    }
    public int getBarcode_type() {
        return this.barcode_type;
    }
}
