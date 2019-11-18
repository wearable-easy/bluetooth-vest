package io.renren.modules.bluetooth.bean;

import io.renren.common.page.PageSortCondition;

public class BluetoothQueryParam extends PageSortCondition {
    private Long startTime;
    private Long endTime;
    private String deviceName;
    private String type;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
