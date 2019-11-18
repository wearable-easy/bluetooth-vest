<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-date-picker
          v-model="dataForm.startTime"
          type="datetime"
          placeholder="开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="dataForm.endTime"
          type="datetime"
          placeholder="结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.deviceName" placeholder="设备名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.type" placeholder="参数类型">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      style="width: 100%;">
      <el-table-column
        prop="time"
        :formatter="dateFormat"
        header-align="center"
        align="center"
        width="150"
        label="时间">
      </el-table-column>
      <!--<el-table-column-->
        <!--header-align="center"-->
        <!--align="center"-->
        <!--width="150"-->
        <!--label="时间">-->
        <!--<template slot-scope="scope">-->
          <!--{{this.getDateByTime(scope.row.time)}}-->
        <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column
        prop="deviceName"
        header-align="center"
        align="center"
        label="设备名">
      </el-table-column>
      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        label="参数类型">
      </el-table-column>
      <el-table-column
        prop="value"
        header-align="center"
        align="center"
        label="上报值">
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        dataForm: {
          startTime: null,
          endTime: null,
          deviceName: null,
          type: null
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        options: [{
          value: 'HEART_RATE',
          label: 'HEART_RATE'
        }, {
          value: 'RESPIRATION_RATE',
          label: 'RESPIRATION_RATE'
        }, {
          value: 'INSPIRATION',
          label: 'INSPIRATION'
        }, {
          value: 'EXPIRATION',
          label: 'EXPIRATION'
        }, {
          value: 'STEP_COUNT',
          label: 'STEP_COUNT'
        }, {
          value: 'ACTIVITY',
          label: 'ACTIVITY'
        }, {
          value: 'CADENCE',
          label: 'CADENCE'
        }]
      }
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/bluetooth/list'),
          method: 'get',
          params: this.$http.adornParams({
            'currentPage': this.pageIndex - 1,
            'pageSize': this.pageSize,
            'startTime': this.dataForm.startTime == null ? null : this.dataForm.startTime.getTime(),
            'endTime': this.dataForm.endTime == null ? null : this.dataForm.endTime.getTime(),
            'deviceName': this.dataForm.deviceName,
            'type': this.dataForm.type
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.payload.list
            this.totalPage = data.payload.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      getDateByTime (val) {
        var date = new Date(val);
        var Y = date.getFullYear() + '-'
        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-'
        var D = date.getDate() + ' '
        var h = date.getHours() + ':'
        var m = date.getMinutes() + ':'
        var s = date.getSeconds()
        // console.log(Y+M+D+h+m+s);
        return Y + M + D + h + m + s

      },
      dateFormat:function(row, column) {
        var time = row[column.property];
        if (time == undefined) {
          return "";
        }
        var date = new Date(time);
        var Y = date.getFullYear() + '-'
        // var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-'
        var M = (date.getMonth()+1) + '-'
        var D = date.getDate() + ' '
        var h = date.getHours() + ':'
        var m = date.getMinutes() + ':'
        var s = date.getSeconds()
        return Y + M + D + h + m + s
        // return new Date(date).format("YYYY-MM-DD HH:mm:ss");
      }

    }
  }
</script>
