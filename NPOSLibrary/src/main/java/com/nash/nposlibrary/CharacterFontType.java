package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum CharacterFontType {

    FONT_A(0),
    FONT_B(1);

    private int fontType;

    CharacterFontType(int type){
        this.fontType = type;
    }

    public int getFont() {
        return this.fontType;
    }
}
