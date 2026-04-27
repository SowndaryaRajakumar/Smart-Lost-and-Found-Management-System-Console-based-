package com.lostfound.service;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    /**
     * Appends a single line to a file (creates file if it does not exist).
     */
    public static void appendLine(String file, String data) {
        write(file, data);
    }

    /**
     * Appends a single line to the file. Creates the file if it does not exist.
     */
    public static void write(String file, String data) {
        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(file, true))) {

            bw.write(data);
            bw.newLine();

        } catch (IOException e) {

            System.out.println("File write error: " + e.getMessage());
        }
    }

    public static List<String> readLines(String file) {
        try {
            Path path = Paths.get(file);
            if (Files.notExists(path)) {
                Files.createFile(path);
                return new ArrayList<>();
            }
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("File read error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void overwrite(String file, List<String> lines) {
        try {
            Path path = Paths.get(file);
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("File overwrite error: " + e.getMessage());
        }
    }
}