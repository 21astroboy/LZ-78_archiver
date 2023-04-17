package com.example.deflate.service;

import com.example.deflate.Code.Huffman;
import com.example.deflate.Code.LZ78;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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



            public void archiveFileWithHuffman(String filePath) {
                // Read the file contents
                String fileContents = readFile(filePath);

                // Create an instance of Huffman class
                Huffman huffman = new Huffman(fileContents);

                // Compress the file contents
                String compressedContents = huffman.code();

                // Create a new folder for the archived file and Huffman object
                File folder = new File("archived_files");
                folder.mkdir();

                // Write the compressed file to a new file in the folder
                String fileName = new File(filePath).getName();
                String compressedFilePath = "archived_files/" + fileName + ".archive";
                writeToFile(compressedFilePath, compressedContents);

                // Save the Huffman object to a file in the folder
                String huffmanFilePath = "archived_files/" + fileName + ".huffman";
                saveHuffmanObjectToFile(huffmanFilePath, huffman);
            }

        private void saveHuffmanObjectToFile(String filePath, Huffman huffman) {
            try {
                FileOutputStream fileOut = new FileOutputStream(filePath);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(huffman);
                objectOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void writeToFile(String filePath, String fileContents) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                writer.write(fileContents);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private String readFile(String filePath) {
            StringBuilder fileContents = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContents.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileContents.toString();
        }


        public void zipUnArch(String filename){
            try(ZipInputStream zin = new ZipInputStream(new FileInputStream(filename)))
            {
                ZipEntry entry;
                String name;
                long size;
                while((entry=zin.getNextEntry())!=null){

                    name = entry.getName(); // получим название файла

                    System.out.printf("File name: %s \t File size: %d \n", name);

                    // распаковка
                    FileOutputStream fout = new FileOutputStream("D:/Coursework/" + name);
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                    zin.closeEntry();
                    fout.close();
                }
            }
            catch(Exception ex){

                System.out.println(ex.getMessage());
            }
        }
}

