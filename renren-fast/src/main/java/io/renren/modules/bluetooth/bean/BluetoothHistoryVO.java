package io.renren.modules.bluetooth.bean;

import io.renren.modules.bluetooth.entity.BluetoothDO;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothHistoryVO {
    private Long id;
    private Long time;
    private String deviceName;
    private String type;
    private String value;

    public static List<BluetoothHistoryVO> buildFromDOList(List<BluetoothDO> list) {
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<BluetoothHistoryVO> resultList = new ArrayList<>();
        list.forEach(bluetoothDO -> {
            BluetoothHistoryVO vo = new BluetoothHistoryVO();
            vo.setId(bluetoothDO.getId());
            vo.setDeviceName(bluetoothDO.getDeviceName());
            vo.setTime(bluetoothDO.getTime());
            vo.setType(bluetoothDO.getType());
            vo.setValue(bluetoothDO.getValue());
            resultList.add(vo);
        });
        return resultList;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BluetoothRealtimeVO{" +
                "id=" + id +
                ", time=" + time +
                ", deviceName='" + deviceName + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
