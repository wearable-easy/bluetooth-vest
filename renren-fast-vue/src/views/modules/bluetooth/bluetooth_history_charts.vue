<template>
  <div class="mod-demo-echarts">
    <div class="mod-log">
      <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-date-picker
            v-model="dataForm.startTime"
            type="datetime"
            placeholder="Start DateTime">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="dataForm.endTime"
            type="datetime"
            placeholder="End DateTime">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.deviceName" placeholder="Device Name" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="dataForm.type" placeholder="Parameter type">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">Search</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div id="J_bluetoothHistory" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import echarts from 'echarts'

  export default {
    data () {
      return {
        dataForm: {
          startTime: null,
          endTime: null,
          deviceName: null,
          type: null
        },
        dataListLoading: false,

        bluetoothRealtime: null,
        heartRate: [],
        respirationRate: [],
        inspiration: [],
        expiration: [],
        stepCount: [],
        activity: [],
        cadence: [],
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
    mounted () {
      this.initBluetoothRealtime()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.heartRate = []
        this.respirationRate = []
        this.inspiration = []
        this.expiration = []
        this.stepCount = []
        this.activity = []
        this.cadence = []
        this.$http({
          url: this.$http.adornUrl('/bluetooth/list'),
          method: 'get',
          params: this.$http.adornParams({
            'startTime': this.dataForm.startTime == null ? null : this.dataForm.startTime.getTime(),
            'endTime': this.dataForm.endTime == null ? null : this.dataForm.endTime.getTime(),
            'deviceName': this.dataForm.deviceName,
            'type': this.dataForm.type
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            for (let index in data.payload.list) {
              var time = data.payload.list[index].time
              var type = data.payload.list[index].type
              var value = data.payload.list[index].value
              var now = new Date(time)
              var result = {
                name: now.toString(),
                value: [
                  now,
                  Math.round(value)
                ]
              }
              if (type === 'HEART_RATE') {
                this.heartRate.push(result)
              } else if (type === 'RESPIRATION_RATE') {
                this.respirationRate.push(result)
              } else if (type === 'INSPIRATION') {
                this.inspiration.push(result)
              } else if (type === 'EXPIRATION') {
                this.expiration.push(result)
              } else if (type === 'STEP_COUNT') {
                this.stepCount.push(result)
              } else if (type === 'ACTIVITY') {
                this.activity.push(result)
              } else if (type === 'CADENCE') {
                this.cadence.push(result)
              }
            }
          }
          this.initBluetoothRealtime()
          this.dataListLoading = false
        }
        )
      },
      initBluetoothRealtime () {
        var option = {
          title: {
            text: 'Historical Curve'
          },
          tooltip: {
            trigger: 'axis',
            formatter: function (params) {
              params = params[0]
              var date = new Date(params.name)
              return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日 ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds() + '=' + params.value[1]
            },
            axisPointer: {
              animation: false
            }
          },
          legend: {
            data: ['HEART_RATE', 'RESPIRATION_RATE', 'INSPIRATION', 'EXPIRATION', 'STEP_COUNT', 'ACTIVITY', 'CADENCE']
          },
          xAxis: {
            type: 'time',
            splitLine: {
              show: false
            }
          },
          yAxis: {
            type: 'value',
            boundaryGap: [0, '100%'],
            splitLine: {
              show: false
            }
          },
          series: [{
            name: 'HEART_RATE',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.heartRate
          },
          {
            name: 'RESPIRATION_RATE',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.respirationRate
          },
          {
            name: 'INSPIRATION',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.inspiration
          },
          {
            name: 'EXPIRATION',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.expiration
          },
          {
            name: 'STEP_COUNT',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.stepCount
          },
          {
            name: 'ACTIVITY',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.activity
          },
          {
            name: 'CADENCE',
            type: 'line',
            showSymbol: true,
            hoverAnimation: false,
            data: this.cadence
          }
          ]
        }
        this.bluetoothRealtime = echarts.init(document.getElementById('J_bluetoothHistory'))
        this.bluetoothRealtime.setOption(option)
        window.addEventListener('resize', () => {
          this.bluetoothRealtime.resize()
        })
      }
    }
  }
</script>

<style lang="scss">
  .mod-demo-echarts {
    > .el-alert {
      margin-bottom: 10px;
    }

    > .el-row {
      margin-top: -10px;
      margin-bottom: -10px;

      .el-col {
        padding-top: 10px;
        padding-bottom: 10px;
      }
    }

    .chart-box {
      min-height: 400px;
    }
  }
</style>
