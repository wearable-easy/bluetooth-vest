package io.renren.modules.bluetooth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import io.renren.common.exception.RRException;
import io.renren.common.page.PageListResult;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.bluetooth.bean.BluetoothHistoryVO;
import io.renren.modules.bluetooth.bean.BluetoothQueryParam;
import io.renren.modules.bluetooth.bean.BluetoothRealtimeVO;
import io.renren.modules.bluetooth.dao.BluetoothDao;
import io.renren.modules.bluetooth.entity.BluetoothDO;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BluetoothService {

    private static final String BLU_PREFIX = "BLU_";

    private static final Gson GSON = new Gson();

    @Autowired
    private BluetoothDao bluetoothDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void save(Object deviceName, String key, Object o) {
        Date now = new Date();
        BluetoothDO bluetoothDO = new BluetoothDO();
        bluetoothDO.setTime(now.getTime());
        bluetoothDO.setDeviceName(deviceName.toString());
        bluetoothDO.setType(key);
        bluetoothDO.setValue(o.toString());
        bluetoothDO.setCreatedAt(now);

        //存在mysql
        bluetoothDao.create(bluetoothDO);
        //存在redis
        redisTemplate.opsForValue().set(BLU_PREFIX.concat(deviceName.toString()).concat("_").concat(key), GSON.toJson(bluetoothDO));
    }

    public BluetoothRealtimeVO getRealtimeByDeviceId(String deviceId, String type) {
        String key = BLU_PREFIX.concat(deviceId).concat("_").concat(type);
        String result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            throw new RRException("No Data");
        }
        BluetoothDO bluetoothDO = GSON.fromJson(result, BluetoothDO.class);
        BluetoothRealtimeVO bluetoothRealtimeVO = new BluetoothRealtimeVO();
        bluetoothRealtimeVO.setId(bluetoothDO.getId());
        bluetoothRealtimeVO.setDeviceName(bluetoothDO.getDeviceName());
        bluetoothRealtimeVO.setTime(bluetoothDO.getTime());
        bluetoothRealtimeVO.setType(bluetoothDO.getType());
        bluetoothRealtimeVO.setValue(bluetoothDO.getValue());
        return bluetoothRealtimeVO;
    }


    public PageListResult<BluetoothHistoryVO> findBy(BluetoothQueryParam bluetoothQueryParam) {
        bluetoothQueryParam.addAscOrder("id");
        int totalCount = bluetoothDao.countBy(bluetoothQueryParam);
        if (totalCount < 1) {
            return PageListResult.emptyResult();
        }
        List<BluetoothDO> list = bluetoothDao.findBy(bluetoothQueryParam);
        return new PageListResult<>(bluetoothQueryParam, totalCount, BluetoothHistoryVO.buildFromDOList(list));

    }
}
