package com.example.projektoop;

import com.example.projektoop.database.CsvFileChecker;
import com.example.projektoop.database.CsvWriter;
import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.models.InverterModel;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InverterService {
    private final String serialPort = "";
    private final String pathCsv = "";

    InverterCommunicationManager inverterCommunicationManager = InverterCommunicationManager.getInstance();

    private final ScheduledExecutorService scheduler;
    private final int intervalSeconds;

    private Consumer<InverterModel> onDataFetched;
    private Consumer<InverterModel> onChartDataFetched;
    private Consumer<List<SmartDevice>> onSmartDeviceFetched;

    private DataProcessor dataProcessor = new DataProcessor();

    public InverterService(int intervalSeconds) {
        this.scheduler = Executors.newScheduledThreadPool(1); // Single-threaded scheduler
        this.intervalSeconds = intervalSeconds;
    }

    public void startCommunication() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                InverterModel data = fetchInverterData();
                Platform.runLater(() -> {
                    if (onDataFetched != null) {
                        onDataFetched.accept(data);

                        List<Boolean> checkUpdates = dataProcessor.updateData(data);

                        if (checkUpdates.getFirst()) {
                            dataProcessor.saveData();
                        } else if (checkUpdates.getLast()) {
                            onChartDataFetched.accept(dataProcessor.updateChart());
                            onSmartDeviceFetched.accept(dataProcessor.getSmartDevices());
                        }
                        saveCsv(data);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);

    }

    private InverterModel fetchInverterData() {
        String command1 = "QPIGS";
        String command2 = "QPIGS2";

        String recivedData1 = inverterCommunicationManager.connectAndReceiveData(command1, serialPort);
        String recivedData2 = inverterCommunicationManager.connectAndReceiveData(command2, serialPort);

        return new InverterModel(recivedData1, recivedData2);
    }

    private void saveCsv(InverterModel data) {
        CsvWriter.writeToCsv(pathCsv, data, CsvFileChecker.doesFileExist(pathCsv));
    }

    public void setOnDataFetched(Consumer<InverterModel> onDataFetched) {
        this.onDataFetched = onDataFetched;
    }

    public void setOnChartDataFetched(Consumer<InverterModel> onChartDataFetched) {
        this.onChartDataFetched = onChartDataFetched;
    }

    public void setOnSmartDeviceFetched(Consumer<List<SmartDevice>> onSmartDeviceFetched) {
        this.onSmartDeviceFetched = onSmartDeviceFetched;
    }

    public void stopCommunication() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}

/*
        System.out.println("timestamp: " + timestamp + "\n" +
                "outputApparentPower: " + Double.parseDouble(arrofRecivedData1[4]) + "\n" +
                "outputPower: " + Double.parseDouble(arrofRecivedData1[5]) + "\n" +
                "loadPercentage: " + Double.parseDouble(arrofRecivedData1[6]) + "\n" +
                "batteryVoltage: " + Double.parseDouble(arrofRecivedData1[8]) + "\n" +
                "batteryChargingCurrent: " + Double.parseDouble(arrofRecivedData1[9]) + "\n" +
                "batteryCapacity: " + Double.parseDouble(arrofRecivedData1[10]) + "\n" +
                "heatSinkTemperature: " + Double.parseDouble(arrofRecivedData1[11]) + "\n" +
                "pv1InputCurrent: " + Double.parseDouble(arrofRecivedData1[12]) + "\n" +
                "pv1InputVoltage: " + Double.parseDouble(arrofRecivedData1[13]) + "\n" +
                "batteryDischargeCurrent: " + Double.parseDouble(arrofRecivedData1[14]) + "\n" +
                "pv1InputPower: " + Double.parseDouble(arrofRecivedData1[19]) + "\n" +
                "pv2InputCurrent: " + Double.parseDouble((arrofRecivedData2[0]).substring(1)) + "\n" +
                "pv2InputVoltage: " + Double.parseDouble(arrofRecivedData2[1]) + "\n" +
                "pv2InputPower: " + Double.parseDouble(arrofRecivedData2[2]) + "\n" +
                "inputPower: " + Double.parseDouble(arrofRecivedData1[9]) + "\n" +
                "batteryCurrent: " + Double.parseDouble(arrofRecivedData1[9]) + "\n"
        );
 */