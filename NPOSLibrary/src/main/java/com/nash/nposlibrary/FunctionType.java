package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum FunctionType {

    A("<A>"), B("<B>"), C("<C>");

    String type;

    FunctionType(String setType){
        this.type = setType;
    }

    public String getFunctionType(){
        return this.type;
    }
}
