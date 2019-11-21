<template>

  <div class="mod-demo-echarts">
    <div class="mod-log">
      <el-form :inline="true" :model="dataForm" @keyup.enter.native="setDeviceName()">
        <el-form-item>
          <el-input v-model="dataForm.chooseDeviceName" placeholder="Device Serial Number" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="setDeviceName()">Search</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div id="J_bluetoothRealtime" class="chart-box"></div>
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
          chooseDeviceName: ''
        },
        isdone: true,
        timer: null,
        deviceNameSet: 'HX-00043719',
        bluetoothRealtime: null,
        heartRate: [],
        currentTimeHeartRate: 0,
        countHeartRate: 0,
        respirationRate: [],
        currentTimeRespirationRate: 0,
        countRespirationRate: 0,
        inspiration: [],
        currentTimeInspiration: 0,
        countInspiration: 0,
        expiration: [],
        currentTimeExpiration: 0,
        countExpiration: 0,
        stepCount: [],
        currentTimeStepCount: 0,
        countStepCount: 0,
        activity: [],
        currentTimeActivity: 0,
        countActivity: 0,
        cadence: [],
        currentTimeCadence: 0,
        countCadence: 0
      }
    },
    mounted () {
      this.initBluetoothRealtime()
      this.timer = setInterval(this.realtimeWork, 1000)
    },
    activated () {
      // 由于给echart添加了resize事件, 在组件激活时需要重新resize绘画一次, 否则出现空白bug
      if (this.chartLine) {
        this.bluetoothRealtime.resize()
      }
    },
    methods: {
      realtimeWork () {
        this.isdone = false
        this.getRealtimeBy('HEART_RATE', this.heartRate, this.currentTimeHeartRate)
        this.getRealtimeBy('RESPIRATION_RATE', this.respirationRate, this.currentTimeRespirationRate)
        this.getRealtimeBy('INSPIRATION', this.inspiration, this.currentTimeInspiration)
        this.getRealtimeBy('EXPIRATION', this.expiration, this.currentTimeExpiration)
        this.getRealtimeBy('STEP_COUNT', this.stepCount, this.currentTimeStepCount)
        this.getRealtimeBy('ACTIVITY', this.activity, this.currentTimeActivity)
        this.getRealtimeBy('CADENCE', this.cadence, this.currentTimeCadence)
        this.isdone = true
      },

      getRealtimeBy (type, thisData, currentTime) {
        console.log(`====================/bluetooth/realtime/` + this.deviceNameSet + `?type=` + type + new Date().getTime())
        this.$http({
          url: this.$http.adornUrl(`/bluetooth/realtime/` + this.deviceNameSet + `?type=` + type),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if (data && data.code === 0) {
            var time = data.payload.time
            var type = data.payload.type
            var value = data.payload.value
            var now = new Date(time)
            if (time <= currentTime) {
              return
            }
            currentTime = time
            var result = {
              name: now.toString(),
              value: [
                now,
                Math.round(value)
              ]
            }
            if (thisData.length > 50) {
              thisData.shift()
              thisData.push(result)
            } else {
              thisData.push(result)
            }
            this.bluetoothRealtime.setOption({
              series: [{
                name: type,
                data: thisData
              }]
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      },

      initBluetoothRealtime () {
        var option = {
          title: {
            text: 'Real-Time Data'
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
        this.bluetoothRealtime = echarts.init(document.getElementById('J_bluetoothRealtime'))
        this.bluetoothRealtime.setOption(option)
        window.addEventListener('resize', () => {
          this.bluetoothRealtime.resize()
        })
      },

      setDeviceName () {
        if (this.dataForm.chooseDeviceName === undefined || this.dataForm.chooseDeviceName === null || this.dataForm.chooseDeviceName === '') {
          return
        }
        // console.log('===========点查询======' + this.dataForm.chooseDeviceName + '=====' + new Date().getTime())
        // console.log("===============" + this.isdone)
        if (this.isdone === false) {
          setTimeout(function () {
            this.setDeviceName()
          }, 300)
        }
        // console.log("=======================" + this.respirationRate)
        this.deviceNameSet = this.dataForm.chooseDeviceName
        this.heartRate = []
        this.currentTimeHeartRate = 0
        this.countHeartRate = 0
        this.respirationRate = []
        this.currentTimeRespirationRate = 0
        this.countRespirationRate = 0
        this.inspiration = []
        this.currentTimeInspiration = 0
        this.countInspiration = 0
        this.expiration = []
        this.currentTimeExpiration = 0
        this.countExpiration = 0
        this.stepCount = []
        this.currentTimeStepCount = 0
        this.countStepCount = 0
        this.activity = []
        this.currentTimeActivity = 0
        this.countActivity = 0
        this.cadence = []
        this.currentTimeCadence = 0
        this.countCadence = 0
        this.initBluetoothRealtime()
        // console.log("=======================" + this.respirationRate)
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
