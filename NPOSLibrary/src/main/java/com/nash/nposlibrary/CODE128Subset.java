package com.nash.nposlibrary;

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
