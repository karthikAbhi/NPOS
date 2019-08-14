package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum PDF417ErrorCorrectionLevel {

    LEVEL0("48"),
    LEVEL1("49"),
    LEVEL2("50"),
    LEVEL3("51"),
    LEVEL4("52"),
    LEVEL5("53"),
    LEVEL6("54"),
    LEVEL7("55"),
    LEVEL8("56");

    private String correctionLevel;

    PDF417ErrorCorrectionLevel(String Level) {
        this.correctionLevel = Level;
    }

    public String getCorrectionLevel() {
        return correctionLevel;
    }
}
