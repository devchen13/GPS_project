package com.mcz.gps_appproject.app.model;

import java.io.Serializable;

/**
 * Created by mcz on 2018/1/10.
 */

public class DataInfo implements Serializable{
    private String DeviceId ;
    private String DeviceName;
    private String DeviceStatus;
    private String DeviceType;
    private String DeviceLongitude;
    private String DeviceLatitude;
    private String error_code;
    ////////////////////////////////
    private String lasttime;
    private String Devicetimestamp;


    public String getDevicetemperature() {
        return Devicetemperature;
    }

    public void setDevicetemperature(String devicetemperature) {
        Devicetemperature = devicetemperature;
    }

    public String getDevicehumidity() {
        return Devicehumidity;
    }

    public void setDevicehumidity(String devicehumidity) {
        Devicehumidity = devicehumidity;
    }

    private String Devicetemperature;
    private String Devicehumidity;

    public DataInfo(){

    }
    public void setDeviceLongitude(String dv_Longitude) {
        DeviceLongitude = dv_Longitude;
    }

    public String getDeviceLongitude(){
        return DeviceLongitude;
    }


    public void setDeviceLatitude(String dv_Latitude) {
        DeviceLatitude = dv_Latitude;
    }

    public String getDeviceLatitude() {
        return DeviceLatitude;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }



    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }


    public String getDeviceStatus() {
        return DeviceStatus;
    }

    public void setDevicetimestamp(String timestamp) {
        Devicetimestamp = timestamp;
    }



    public String getDevicetimestamp() {
        return Devicetimestamp;
    }

    public void setDeviceStatus(String deviceStatus) {
        DeviceStatus = deviceStatus;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.DeviceId = deviceId;
    }
    public String getGatewayId() {
        return DeviceId;
    }

    public void setGatewayId(String deviceId) {
        this.DeviceId = deviceId;
    }
}
