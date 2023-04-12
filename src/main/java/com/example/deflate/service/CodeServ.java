package com.example.deflate.service;

import com.example.deflate.Code.LZ78;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CodeServ {

    public final String HomeDirectory = "D:\\Coursework";
    public static LZ78 lz = new LZ78();


    public void archive(String fileName) {

        StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(HomeDirectory + "\\" + fileName))) {
            String line = reader.readLine();

            while (line != null) {
                if (str.length() > 0) {
                    str.append(System.lineSeparator());
                }
                str.append(line);
                line = reader.readLine();
            }

            str = new StringBuilder(lz.compress(str.toString()));
            String filePath = "D:\\Coursework\\" + fileName + ".archive";

            // Создание папки, если она не существует
            Path directoryPath = Paths.get("D:\\Coursework");
            if (!Files.exists(directoryPath)) {
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(str.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

    }

    public void unArchived(String fileName) {

       StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(HomeDirectory + "\\" + fileName))) {
            String line = reader.readLine();

            while (line != null) {
                str.append(line);
                str.append(System.lineSeparator());
                line = reader.readLine();
            }

            String result = lz.decompress(str.toString());
            String filePath = "D:\\Coursework\\" + fileName.substring(0, fileName.length() - 8);
            Path directoryPath = Paths.get("D:\\Coursework");
            if (!Files.exists(directoryPath)) {
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

    }
}
