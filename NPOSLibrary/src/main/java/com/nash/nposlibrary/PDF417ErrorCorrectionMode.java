package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum PDF417ErrorCorrectionMode {

    LEVEL("48"),
    RATIO("49");

    private String mode;

    PDF417ErrorCorrectionMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
