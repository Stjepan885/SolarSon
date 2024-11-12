package com.example.projektoop.database;

import java.io.File;

public class CsvFileChecker {
    public static boolean doesFileExist ( String filename ) {
        File file = new File ( filename );
        return file.exists();
    }
}
