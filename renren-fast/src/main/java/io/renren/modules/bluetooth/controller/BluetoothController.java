package io.renren.modules.bluetooth.controller;

import com.alibaba.druid.util.StringUtils;
import io.renren.common.page.PageListResult;
import io.renren.common.utils.R;
import io.renren.modules.bluetooth.bean.BluetoothHistoryVO;
import io.renren.modules.bluetooth.bean.BluetoothQueryParam;
import io.renren.modules.bluetooth.bean.BluetoothRealtimeVO;
import io.renren.modules.bluetooth.service.BluetoothService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/bluetooth")
@Api("VehicleDisplayController")
public class BluetoothController {

    @Autowired
    private BluetoothService bluetoothService;


    @PostMapping("/save")
    @ApiOperation(value = "保存蓝牙记录", notes = "保存蓝牙记录")
    public R save(@RequestBody Map map){

        System.out.println(map.toString());

        Set keySet = map.keySet();
        String key = null;
        for (Object k : keySet) {
            if (!StringUtils.equals("deviceName", (String)k)) {
                key = (String)k;
            }
        }

        if (key == null) {
            System.out.println("error");
            return null;
        }
        bluetoothService.save(map.get("deviceName"), key, map.get(key));
        return R.ok();
    }

    @GetMapping("/realtime/{deviceId}")
    @ApiOperation(value = "查询蓝牙实时记录", notes = "查询蓝牙实时记录")
    public R<BluetoothRealtimeVO> realtime(@PathVariable("deviceId") String deviceId, @RequestParam("type") String type) {
        BluetoothRealtimeVO bluetoothRealtimeVO = bluetoothService.getRealtimeByDeviceId(deviceId, type);
        return R.ok(bluetoothRealtimeVO);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询历史记录列表", notes = "查询历史记录列表")
    public R<PageListResult<BluetoothHistoryVO>> list(BluetoothQueryParam bluetoothQueryParam){
        //todo 参数校验
        PageListResult<BluetoothHistoryVO> result = bluetoothService.findBy(bluetoothQueryParam);
        if (result == null || Objects.equals(0, result.getTotalCount())) {
            return R.notFound(result);
        }
        return R.ok(result);
    }

}
