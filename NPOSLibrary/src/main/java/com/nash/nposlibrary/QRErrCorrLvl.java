package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum QRErrCorrLvl {
    L(48),
    M(49),
    Q(50),
    H(51);

    private int correctionlevel;

    QRErrCorrLvl(int correctionlvl) {
        this.correctionlevel = correctionlvl;
    }

    public int getCorrectionlevel() {
        return correctionlevel;
    }
}
