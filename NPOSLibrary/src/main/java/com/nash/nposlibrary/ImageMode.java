package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum ImageMode {
    NORMAL("0"),
    DOUBLE_WIDTH("1"),
    DOUBLE_HEIGHT("2"),
    DOUBLE_WIDTH_DOUBLE_HEIGHT("3");

    private String mode;

    ImageMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
