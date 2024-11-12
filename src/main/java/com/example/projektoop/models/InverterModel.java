package com.example.projektoop.models;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InverterModel {
    private long timestamp;

    private double outputApparentPower;
    private double outputPower;
    private int loadPercentage;
    private double batteryVoltage;
    private int batteryChargingCurrent;
    private int batteryCapacity;
    private double heatSinkTemperature;
    private double pv1InputCurrent;
    private double pv1InputVoltage;
    private double batteryDischargeCurrent;
    private double pv1InputPower;

    private double pv2InputCurrent;
    private double pv2InputVoltage;
    private double pv2InputPower;

    private double inputPower;
    private double batteryCurrent;

    private int inverterMode;

    public static InverterModel calculateAverage(List<InverterModel> dataList){
        InverterModel averageData = new InverterModel();

        Field[] fields = InverterModel.class.getDeclaredFields();

        for (Field field : fields){
            if (field.getType() == double.class){
                double total = 0;
                for (InverterModel data : dataList){
                    try {
                        total += field.getDouble(data);
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                try {
                    BigDecimal db = new BigDecimal(total/dataList.size()).setScale(2, RoundingMode.HALF_UP);
                    field.setDouble(averageData, db.doubleValue());
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }

            } else if (field.getType() == int.class){
                int total = 0;
                for (InverterModel data : dataList){
                    try {
                        total += field.getInt(data);
                    } catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                try {
                    field.setInt(averageData, total/dataList.size());
                } catch (IllegalAccessException e){
                    e.printStackTrace();
                }

            }else  if (field.getType() == long.class){
                double total = 0;
                for (InverterModel data : dataList){
                    try {
                        total += (double)field.getLong(data);
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                try {
                    field.setLong(averageData, (long)(total/dataList.size()));
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }

        return averageData;

    }

    public InverterModel() {

    }

    public InverterModel(String recivedData1, String recivedData2) {
        String[] arrofRecivedData1 = recivedData1.split(" ");
        String[] arrofRecivedData2 = recivedData2.split(" ");

        long timestamp = System.currentTimeMillis();
        double a = Double.parseDouble(arrofRecivedData1[19])+Double.parseDouble(arrofRecivedData2[2]);
        double batteryCurrent = 0.0;

        if (Double.parseDouble(arrofRecivedData1[9]) > Double.parseDouble(arrofRecivedData1[15])){
            batteryCurrent = 0+Double.parseDouble(arrofRecivedData1[9]);
        }else {
            batteryCurrent = 0-Double.parseDouble(arrofRecivedData1[15]);
        }

        this.timestamp = timestamp;
        this.outputApparentPower = Double.parseDouble(arrofRecivedData1[4]);
        this.outputPower = Double.parseDouble(arrofRecivedData1[5]);
        this.loadPercentage = Integer.parseInt(arrofRecivedData1[6]);
        this.batteryVoltage = Double.parseDouble(arrofRecivedData1[8]);
        this.batteryChargingCurrent = Integer.parseInt(arrofRecivedData1[9]);
        this.batteryCapacity = Integer.parseInt(arrofRecivedData1[10]);
        this.heatSinkTemperature = Double.parseDouble(arrofRecivedData1[11]);
        this.pv1InputCurrent = Double.parseDouble(arrofRecivedData1[12]);
        this.pv1InputVoltage = Double.parseDouble(arrofRecivedData1[13]);
        this.batteryDischargeCurrent = Double.parseDouble(arrofRecivedData1[14]);
        this.pv1InputPower = Double.parseDouble(arrofRecivedData1[19]);

        this.pv2InputCurrent = Double.parseDouble((arrofRecivedData2[0]).substring(1));
        this.pv2InputVoltage =  Double.parseDouble(arrofRecivedData2[1]);
        this.pv2InputPower = Double.parseDouble(arrofRecivedData2[2]);
        this.inputPower = a;
        this.batteryCurrent = batteryCurrent;
    }


    public InverterModel(long timestamp, double inputPower, double outputPower, int batteryCapacity, double batteryCurrent) {
        this.timestamp = timestamp;
        this.inputPower = inputPower;
        this.outputPower = outputPower;
        this.batteryCapacity = batteryCapacity;
        this.batteryCurrent = batteryCurrent;
    }

    public InverterModel(long timestamp, double outputApparentPower, double outputPower, int loadPercentage, double batteryVoltage, int batteryChargingCurrent, int batteryCapacity, double heatSinkTemperature, double pv1InputCurrent, double pv1InputVoltage, double batteryDischargeCurrent, double pv1InputPower, double pv2InputCurrent, double pv2InputVoltage, double pv2InputPower, double inputPower, double batteryCurrent) {
        this.timestamp = timestamp;
        this.outputApparentPower = outputApparentPower;
        this.outputPower = outputPower;
        this.loadPercentage = loadPercentage;
        this.batteryVoltage = batteryVoltage;
        this.batteryChargingCurrent = batteryChargingCurrent;
        this.batteryCapacity = batteryCapacity;
        this.heatSinkTemperature = heatSinkTemperature;
        this.pv1InputCurrent = pv1InputCurrent;
        this.pv1InputVoltage = pv1InputVoltage;
        this.batteryDischargeCurrent = batteryDischargeCurrent;
        this.pv1InputPower = pv1InputPower;
        this.pv2InputCurrent = pv2InputCurrent;
        this.pv2InputVoltage = pv2InputVoltage;
        this.pv2InputPower = pv2InputPower;
        this.inputPower = inputPower;
        this.batteryCurrent = batteryCurrent;
    }

    public String toCsv() {
        return String.join(",",
                String.valueOf(timestamp),
                String.valueOf(outputApparentPower),
                String.valueOf(outputPower),
                String.valueOf(loadPercentage),
                String.valueOf(batteryVoltage),
                String.valueOf(batteryChargingCurrent),
                String.valueOf(batteryCapacity),
                String.valueOf(heatSinkTemperature),
                String.valueOf(pv1InputCurrent),
                String.valueOf(pv1InputVoltage),
                String.valueOf(batteryDischargeCurrent),
                String.valueOf(pv1InputPower),
                String.valueOf(pv2InputCurrent),
                String.valueOf(pv2InputVoltage),
                String.valueOf(pv2InputPower),
                String.valueOf(inputPower),
                String.valueOf(batteryCurrent)
        );
    }

    @Override
    public String toString() {
        return "InverterModel{" +
                "timestamp=" + timestamp +
                ", outputApparentPower=" + outputApparentPower +
                ", outputPower=" + outputPower +
                ", loadPercentage=" + loadPercentage +
                ", batteryVoltage=" + batteryVoltage +
                ", batteryChargingCurrent=" + batteryChargingCurrent +
                ", batteryCapacity=" + batteryCapacity +
                ", heatSinkTemperature=" + heatSinkTemperature +
                ", pv1InputCurrent=" + pv1InputCurrent +
                ", pv1InputVoltage=" + pv1InputVoltage +
                ", batteryDischargeCurrent=" + batteryDischargeCurrent +
                ", pv1InputPower=" + pv1InputPower +
                ", pv2InputCurrent=" + pv2InputCurrent +
                ", pv2InputVoltage=" + pv2InputVoltage +
                ", pv2InputPower=" + pv2InputPower +
                ", inputPower=" + inputPower +
                ", batteryCurrent=" + batteryCurrent +
                '}';
    }

    public double getOutputApparentPower() {
        return outputApparentPower;
    }
    public int getLoadPercentage() {
        return loadPercentage;
    }
    public double getBatteryVoltage() {
        return batteryVoltage;
    }
    public int getBatteryChargingCurrent() {
        return batteryChargingCurrent;
    }
    public double getPv1InputCurrent() {
        return pv1InputCurrent;
    }
    public double getPv1InputVoltage() {
        return pv1InputVoltage;
    }
    public double getBatteryDischargeCurrent() {
        return batteryDischargeCurrent;
    }
    public double getPv1InputPower() {
        return pv1InputPower;
    }
    public double getPv2InputCurrent() {
        return pv2InputCurrent;
    }
    public double getPv2InputVoltage() {
        return pv2InputVoltage;
    }
    public double getPv2InputPower() {
        return pv2InputPower;
    }
    public double getHeatSinkTemperature() {
        return heatSinkTemperature;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public double getInputPower() {
        return inputPower;
    }
    public double getOutputPower() {
        return outputPower;
    }
    public double getBatteryCurrent() {
        return batteryCurrent;
    }
    public int getBatteryCapacity() {
        return batteryCapacity;
    }

}
