package com.example.projektoop.database;

import com.example.projektoop.models.InverterModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {
    public static void writeToCsv(String filepath, InverterModel inverterData, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, append))){
            if(!append){
                String header =  "timestamp,outputApparentPower,outputPower,loadPercentage,batteryVoltage," +
                        "batteryChargingCurrent,batteryCapacity,heatSinkTemperature," +
                        "pv1InputCurrent,pv1InputVoltage,batteryDischargeCurrent," +
                        "pv1InputPower,pv2InputCurrent,pv2InputVoltage,pv2InputPower," +
                        "inputPower,batteryCurrent";
                writer.write(header);
                writer.newLine();
            }
            writer.write(inverterData.toCsv());
            writer.newLine();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
