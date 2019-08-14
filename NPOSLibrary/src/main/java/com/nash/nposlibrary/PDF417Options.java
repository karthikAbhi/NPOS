package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum PDF417Options {

    STANDARD("0"),
    TRUNCATED("1");

    private String option;

    PDF417Options(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
