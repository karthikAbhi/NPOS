package com.nash.nposlibrary;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum CODE128Subset {

    SUBSETA(0, 103),
    SUBSETB(1, 104),
    SUBSETC(2, 105);

    private int subset;
    private int index;

    public int getIndex() {
        return this.index;
    }

    public int getSubset() {
        return this.subset;
    }

    CODE128Subset(int index, int subset) {
        this.subset = subset;
        this.index = index;
    }
}
