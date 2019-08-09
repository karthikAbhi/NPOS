package com.nash.npos;

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
