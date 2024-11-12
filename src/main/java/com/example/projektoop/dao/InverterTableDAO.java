package com.example.projektoop.dao;

import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.util.SQLiteUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InverterTableDAO {

    public void createTable() {
        String createTable1 = "CREATE TABLE IF NOT EXISTS smartDevices \n" +
                "(id TEXT PRIMARY KEY, \n" +
                "name TEXT,\n " +
                "maxPower REAL,\n " +
                "connectedPower REAL, \n " +
                "priorityValue INTEGER, \n" +

                "isActive INTEGER, \n" +

                "isDouble INTEGER)";

        try (Connection connection = SQLiteUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTable1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(SmartDevice device) {
        String insertSQL = "INSERT INTO smartDevices (id, name, maxPower, connectedPower, priorityValue, " +
                "isActive, isDouble) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = SQLiteUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, device.getId());
            preparedStatement.setString(2, device.getName());
            preparedStatement.setDouble(3, device.getMaxPower());
            preparedStatement.setDouble(4, device.getConnectedPower());
            preparedStatement.setInt(5, device.getPriorityValue());
            preparedStatement.setInt(6,  0);
            preparedStatement.setInt(7, device.isDual() ? 1 : 0);
            System.out.println("Insert onto db device " + device.getName() + " with id " + device.getId() + " and maxPower " + device.getMaxPower() + " and connectedPower " + device.getConnectedPower() + " and priorityValue " + device.getPriorityValue() +  " and isDual " + device.isDual());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SmartDevice> getAll() {
        List<SmartDevice> smartDeviceList = new ArrayList<>();
        String selectSQL = "SELECT * FROM smartDevices";

        try (Connection conn = SQLiteUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double maxPower = rs.getDouble("maxPower");
                double connectedPower = rs.getDouble("connectedPower");
                int priority = rs.getInt("priorityValue");
                int isActive = 0;
                boolean isDual = rs.getBoolean("isDouble");

                SmartDevice sd = new SmartDevice(name, id, isDual, maxPower, connectedPower, priority, isActive);
                smartDeviceList.add(sd);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return smartDeviceList;
    }

    public void dropTable() {
        String dropTableSQL = "DROP TABLE IF EXISTS smartDevices";
        try (Connection connection = SQLiteUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(dropTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeMaxPower(String id, double maxPower) {
        String updateSQL = "UPDATE smartDevices SET maxPower = ? WHERE id = ?";
        try (Connection connection = SQLiteUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, maxPower);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeConnectedPower(String id, double connectedPower) {
        String updateSQL = "UPDATE smartDevices SET connectedPower = ? WHERE id = ?";
        try (Connection connection = SQLiteUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, connectedPower);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changePriorityValue(String id, int priorityValue) {
        String updateSQL = "UPDATE smartDevices SET priorityValue = ? WHERE id = ?";
        try (Connection connection = SQLiteUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setInt(1, priorityValue);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}