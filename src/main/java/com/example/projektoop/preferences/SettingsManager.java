package com.example.projektoop.preferences;

import java.util.prefs.Preferences;

public class SettingsManager {

    private static final Preferences userPrefs = Preferences.userNodeForPackage(SettingsManager.class);


    public static void saveUserPreference(String key, String value) {
        userPrefs.put(key, value); // Saves the preference persistently
    }

    public static String getUserPreference(String key, String defaultValue) {
        return userPrefs.get(key, defaultValue);
    }
}
