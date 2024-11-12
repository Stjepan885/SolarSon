package com.example.projektoop;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class InverterCommunicationManager {
    private static InverterCommunicationManager instance;
    int POLYNOMIAL = 0x1021;

    private InverterCommunicationManager() {

    }

    public static InverterCommunicationManager getInstance() {
        if (instance == null) {
            instance = new InverterCommunicationManager();
        }
        return instance;
    }

    public synchronized String connectAndReceiveData(String command, String serialPort) {
        String recivedData = "";
        byte[] commandBytes = command.getBytes();

        int crc = calculateCRC(commandBytes);
        byte crcHigh = (byte) ((crc >> 8) & 0xFF);
        byte crcLow = (byte) (crc & 0xFF);

        byte[] fullCommand = new byte[commandBytes.length + 3];
        System.arraycopy(commandBytes, 0, fullCommand, 0, commandBytes.length);
        fullCommand[commandBytes.length] = crcHigh;
        fullCommand[commandBytes.length + 1] = crcLow;
        fullCommand[commandBytes.length + 2] = (byte) 0x0d;

        try (RandomAccessFile usb0 = new RandomAccessFile(serialPort, "rw")) {
            int cmdLen = fullCommand.length;

            if (cmdLen <= 8) {
                Thread.sleep(50);
                usb0.write(fullCommand);
            } else {
                List<byte[]> chunks = new ArrayList<>();
                for (int i = 0; i < cmdLen; i += 8) {
                    int end = Math.min(i + 8, cmdLen);
                    byte[] chunk = new byte[8];
                    System.arraycopy(fullCommand, i, chunk, 0, end - i);  // Copy data to chunk
                    if (end - i < 8) {
                        for (int j = end - i; j < 8; j++) {
                            chunk[j] = 0x00;
                        }
                    }
                    chunks.add(chunk);
                }

                for (byte[] chunk : chunks) {
                    Thread.sleep(50);  // 0.05 seconds
                    usb0.write(chunk);  // Write the chunk
                }
            }

            Thread.sleep(250);
            byte[] responseLine = new byte[0];

            for (int x = 0; x < 100; x++) {
                try {
                    Thread.sleep(150);
                    byte[] buffer = new byte[512];
                    int bytesRead = usb0.read(buffer);
                    if (bytesRead > 0) {
                        byte[] tempResponse = new byte[responseLine.length + bytesRead];
                        System.arraycopy(responseLine, 0, tempResponse, 0, responseLine.length);
                        System.arraycopy(buffer, 0, tempResponse, responseLine.length, bytesRead);
                        responseLine = tempResponse;
                    }
                } catch (IOException e) {
                    System.out.println("USB read error: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Thread sleep interrupted: " + e.getMessage());
                }

                if (contains(responseLine, (byte) 13)) {
                    int index = indexOf(responseLine, (byte) 13);
                    byte[] truncatedResponse = new byte[index + 1];
                    System.arraycopy(responseLine, 0, truncatedResponse, 0, index + 1);
                    responseLine = truncatedResponse;
                    break;
                }
            }

            String value = new String((responseLine));

            recivedData = value;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return recivedData;
    }

    private int calculateCRC(byte[] data) {
        int crc = 0; // Starting CRC value

        for (byte b : data) {
            crc ^= (b & 0xFF) << 8; // XOR with high byte of CRC
            for (int i = 0; i < 8; i++) { // Process each bit
                if ((crc & 0x8000) != 0) { // If the top bit is 1
                    crc = (crc << 1) ^ POLYNOMIAL; // Shift left and XOR with polynomial
                } else {
                    crc <<= 1; // Shift left if top bit is 0
                }
            }
        }
        return crc & 0xFFFF; // Return the lower 16 bits as the CRC value
    }

    private boolean contains(byte[] array, byte value) {
        for (byte b : array) {
            if (b == value) {
                return true;
            }
        }
        return false;
    }

    private int indexOf(byte[] array, byte value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

}
