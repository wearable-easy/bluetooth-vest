package com.hexoskin.hexoskin_smart_android;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceActivity extends AppCompatActivity {

    private BluetoothDevice _device;
    private BluetoothGatt _gatt;

    // Heart Rate Service UUID
    private static UUID HEART_RATE_MEASUREMENT_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");

    // Heart Rate Measurement UUID
    private static UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    // Respiration Rate Service UUID
    private static UUID RESPIRATION_SERVICE_UUID = UUID.fromString("3b55c581-bc19-48f0-bd8c-b522796f8e24");

    // Respiration Rate Measurement UUID
    private static UUID RESPIRATION_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("9bc730c3-8cc0-4d87-85bc-573d6304403c");

    // UUID for notification
    private static UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Accelerometer Service UUID
    private static UUID ACCELEROMETER_SERVICE_UUID = UUID.fromString("bdc750c7-2649-4fa8-abe8-fbf25038cda3");

    // Accelerometer Measurement UUID
    private static UUID ACCELEROMETER_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("75246a26-237a-4863-aca6-09b639344f43");

    private RecyclerView _recyclerView;
    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager _layoutManager;

    private List<String> _data = Collections.synchronizedList(new ArrayList<String>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _device =  getIntent().getParcelableExtra("Device");
        getSupportActionBar().setTitle(_device.getName());

        _recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        _recyclerView.setHasFixedSize(true);
        _layoutManager = new LinearLayoutManager(this);
        _recyclerView.setLayoutManager(_layoutManager);
        _adapter = new MyAdapter();
        _recyclerView.setAdapter(_adapter);

        // Table view data
        _data.add("Connecting ...");
        _data.add("HEART RATE --");
        _data.add("RESP. RATE --");
        _data.add("INSP/EXP --");
        _data.add("STEP COUNT --");
        _data.add("ACTIVITY --");
        _data.add("CADENCE --");

        _gatt = _device.connectGatt(this, true, mCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _gatt.close();
    }

    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {

        // Invoked when Bluetooth connection changes
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        _data.set(0, "Connected");
                        _gatt.discoverServices();
                    } else {
                        _data.set(0, "Disconnected");
                        _data.set(1, "HEART RATE --");
                        _data.set(2, "RESP. RATE --");
                        _data.set(3, "INSP/EXP --");
                        _data.set(4, "STEP COUNT --");
                        _data.set(5, "ACTIVITY --");
                        _data.set(6, "CADENCE --");

                    }

                    // reload table view
                    _adapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Listen for Heart Rate notification
                BluetoothGattService hrSvc = gatt.getService(HEART_RATE_MEASUREMENT_SERVICE_UUID);
                BluetoothGattCharacteristic hrChar = hrSvc.getCharacteristic(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(hrChar, true);
                BluetoothGattDescriptor descriptor = hrChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (descriptor.getCharacteristic().getUuid().equals(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                // Listen for Respiration Rate notification
                BluetoothGattService respSvc = gatt.getService(RESPIRATION_SERVICE_UUID);
                BluetoothGattCharacteristic respChar = respSvc.getCharacteristic(RESPIRATION_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(respChar, true);
                BluetoothGattDescriptor respDescriptor = respChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                respDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(respDescriptor);
            }
            else if (descriptor.getCharacteristic().getUuid().equals(RESPIRATION_RATE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                //Listen for Accelerometer notification
                BluetoothGattService accSvc = gatt.getService(ACCELEROMETER_SERVICE_UUID);
                BluetoothGattCharacteristic accChar = accSvc.getCharacteristic(ACCELEROMETER_MEASUREMENT_CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(accChar, true);
                BluetoothGattDescriptor accDescriptor = accChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                accDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(accDescriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] data = characteristic.getValue();
                    UUID uuid = characteristic.getUuid();
                    String hexString = byteArrayToHex(data);

                    //Heart Rate Received
                    if (uuid.equals(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                        int flag = data[0];
                        int format;
                        if ((flag & 0x01) == 0) {
                            format = BluetoothGattCharacteristic.FORMAT_UINT8;
                        } else {
                            format = BluetoothGattCharacteristic.FORMAT_UINT16;
                        }
                        int heartRate = characteristic.getIntValue(format, 1);
                        _data.set(1, "HEART RATE " + heartRate + ", (" + hexString + ")");
                        _adapter.notifyDataSetChanged();
                        sendHttp(_device.getName(), "HEART_RATE", hexString);

                    }
                    // Respiration Rate Received
                    else if (uuid.equals(RESPIRATION_RATE_MEASUREMENT_CHARACTERISTIC_UUID)) {

                        byte flag = data[0];
                        int format;
                        if ((flag & 0x01) == 0) {
                            format = BluetoothGattCharacteristic.FORMAT_UINT8;
                        } else {
                            format = BluetoothGattCharacteristic.FORMAT_UINT16;
                        }

                        int respRate = characteristic.getIntValue(format, 1);
                        _data.set(2, "RESP. RATE " + respRate + ", (" + hexString + ")");


                        boolean isInspExpPresent = (flag & 0x02) != 0;
                        _data.set(3, "INSP/EXP ");
                        if (isInspExpPresent) {
                            int startOffset = 1 + (format == BluetoothGattCharacteristic.FORMAT_UINT8 ? 1 : 2);
                            boolean inspFirst = (flag & 0x04) == 0;
                            StringBuilder sb = new StringBuilder();
                            sb.append("INSP/EXP ");
                            for (int i = startOffset; i < data.length; i += 2) {
                                float value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, i) / 32.0f;
                                if (inspFirst) {
                                    sb.append(value + "(I), ");
                                    inspFirst = false;
                                } else {
                                    sb.append(value + "(E), ");
                                    inspFirst = true;
                                }
                            }
                            _data.set(3, sb.toString());
                        }
                        _adapter.notifyDataSetChanged();
                    }

                    // Accelerometer data received
                    else if (uuid.equals((ACCELEROMETER_MEASUREMENT_CHARACTERISTIC_UUID))) {
                        byte flag = data[0];
                        int format = BluetoothGattCharacteristic.FORMAT_UINT16;
                        int dataIndex = 1;

                        boolean isStepCountPresent = (flag & 0x01) != 0;
                        boolean isActivityPresent = (flag & 0x02) != 0;
                        boolean isCadencePresent = (flag & 0x04) != 0;

                        if (isStepCountPresent) {
                            int stepCount = characteristic.getIntValue(format, dataIndex);
                            _data.set(4, "STEP COUNT " + stepCount + ", (" + hexString + ")");
                            dataIndex = dataIndex + 2;
                        }

                        if (isActivityPresent) {
                            float activity = characteristic.getIntValue(format, dataIndex)/256.0f;
                            _data.set(5, "ACTIVITY " + activity + ", (" + hexString + ")");
                            dataIndex = dataIndex + 2;
                        }

                        if (isCadencePresent) {
                            int cadence = characteristic.getIntValue(format,dataIndex);
                            _data.set(6, "CADENCE " + cadence + ", (" + hexString + ")");
                        }
                    }
                }
            });
        }
    };

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView _textView;
            public MyViewHolder(View v) {
                super(v);
                _textView = (TextView) v.findViewById(R.id.row_item);
            }
        }

        public MyAdapter() {
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder._textView.setText(_data.get(position));
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }
    }

    private void sendHttp(String deviceName, String key, String value) {
        final String deviceNameF = deviceName;
        final String keyF = key;
        final String valueF = value;
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
                String jsonStr = "{\"deviceName\":\""+ deviceNameF +"\",\""+ keyF +"\":\""+ valueF +"\"}";//json数据.
                RequestBody body = RequestBody.create(JSON, jsonStr);
                Request request = new Request.Builder()
                        .url("http://172.20.10.2:8080/renren-fast/bluetooth/save")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();//发送请求
                    String result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
