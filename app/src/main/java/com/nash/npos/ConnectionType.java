package com.nash.npos;

/**
 * Created by Karthik Raj K on 14/8/19.
 * Project: NPOS
 * Copyright (c) 2019 NASH Industries India Pvt. Ltd. All rights reserved.
 */

public enum ConnectionType {

    USB(0, false),
    BT(1, false),
    WIFI(2, false);

    private int typeId;
    private boolean connectionState;

    ConnectionType(int typeId, boolean state) {
        this.typeId = typeId;
        this.connectionState = state;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public boolean isConnectionState() {
        return connectionState;
    }

    public void setConnectionState(boolean connectionState) {
        this.connectionState = connectionState;
    }
}
