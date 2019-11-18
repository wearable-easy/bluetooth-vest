package io.renren.modules.bluetooth.dao;

import io.renren.common.page.PageSortCondition;
import io.renren.modules.bluetooth.entity.BluetoothDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BluetoothDao {
    int create(BluetoothDO bluetoothDO);

    List<BluetoothDO> findBy(@Param(PageSortCondition.NAME) PageSortCondition condition);

    int countBy(@Param(PageSortCondition.NAME) PageSortCondition condition);
}
