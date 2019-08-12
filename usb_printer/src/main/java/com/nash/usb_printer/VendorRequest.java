package com.nash.usb_printer;

public enum VendorRequest {

    REPLY_PRINTER_STATUS(1,10),
    REPLY_FIRMWARE_VERSION(3,12),
    REPLY_FLASH_MEMORY_FREE_SPACE(5,4),
    RESET_PRINTER(7,10),
    REPLY_TRACEABILITY_INFO(17,12),
    REPLY_MAINTENANCE_COUNTER(18,4),
    REPLY_USB_ERROR_LOG(19,16),
    SPECIFY_DETECTION_FUNCTION(8,10),
    REPLY_ERROR_LOG(20,28),
    REPLY_DIP_SWITCH_SETTINGS(21,4),
    REPLY_HEAD_OPEN_DOT_COUNT(22,4),
    REPLY_DETAILED_INFO_EXTENDED_PRINTER_STATUS(23,10),
    REPLY_ADJUSTEMENT_VALUE_SETTINGS(24,10),
    REPLY_SENSOR_INFORMATION(25,10),
    REPLY_MECHANICAL_ADJUSTMENT_VALUE(26,2);

    private int bRequest;
    private int wLength;

    VendorRequest(int bRequestType, int wLengthSize){
        this.bRequest = bRequestType;
        this.wLength = wLengthSize;
    }

    public byte getbRequest() {
        return (byte)this.bRequest;
    }

    public byte getwLength() {
        return (byte)this.wLength;
    }
}
