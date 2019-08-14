package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum CutCommand {

    PARTIALCUT("0"),FULLCUT("1");

    private String mode;

    private CutCommand(String value){
        this.mode = value;
    }

    public String getMode() {
        return this.mode;
    }

}

