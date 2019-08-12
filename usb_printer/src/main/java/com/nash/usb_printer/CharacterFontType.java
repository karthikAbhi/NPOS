package com.nash.usb_printer;

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
