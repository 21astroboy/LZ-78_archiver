package com.example.deflate.controllers;

import com.example.deflate.Code.Huffman;
import com.example.deflate.service.CodeServ;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class MainController {
    String fileName;
    private final CodeServ serv = new CodeServ();
    private static String UPLOADED_FOLDER = "/D:/Projects/";

    @GetMapping("/")
    public String home(ModelAndView model) {

        return "home";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        fileName = file.getOriginalFilename();
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatus";
        }

        try {
            Path path = Paths.get("D:\\Coursework" + "\\" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            redirectAttributes.
                    addFlashAttribute
                            ("message", "File " +
                                    file.getOriginalFilename() + " uploaded successfully");


        } catch (IOException e) {

            e.printStackTrace();
        }


        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @Controller
    public class DownloadController {
        @GetMapping("/downloadArchive")
        public ResponseEntity<Resource> downloadFile() throws IOException {
            serv.archive(fileName);
            fileName = fileName + ".archive";
            Path filePath = Paths.get("D:", "Coursework", fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new FileNotFoundException("Файл не найден");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

        @GetMapping("/downloadUnArchivedFile")
        public ResponseEntity<Resource> downloadUnArchivedFile() throws IOException {
            serv.unArchived(fileName);
            fileName = fileName.substring(0, fileName.length() - 8);
            Path filePath = Paths.get("D:", "Coursework", fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new FileNotFoundException("Файл не найден");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

    }

    @Controller
    public class FullArchiveController {
        @GetMapping("/f")
        public ResponseEntity<?> uploadFile() throws IOException {
            String filePath = "D:/Coursework/" + fileName;
            Resource resource = new FileSystemResource(filePath);
            MultipartFile file = new MockMultipartFile(resource.getFilename(), resource.getInputStream());
            try {
                // Save the uploaded file to the specified path
                file.transferTo(new File(filePath));


            } catch (IOException e) {
                e.printStackTrace();
            }

            // Archive the uploaded file with Huffman coding
            archiveFileWithHuffman(filePath);

            // Create a new file containing the archive folder
            File archiveFolder = new File("archived_files.zip");
            try {
                // Compress the archive folder
                ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(archiveFolder));
                File folderToZip = new File("archived_files");
                zipFile(folderToZip, folderToZip.getName(), zipOut);
                zipOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set headers for downloading the archive file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/zip"));
            headers.setContentDispositionFormData("attachment", "archived_files.zip");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            // Return the archive file as a response
            FileSystemResource fileSystemResource = new FileSystemResource(archiveFolder);
            return new ResponseEntity<>(fileSystemResource, headers, HttpStatus.OK);
        }

        private void archiveFileWithHuffman(String filePath) {
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

        private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
            if (fileToZip.isHidden()) {
                return;
            }
            if (fileToZip.isDirectory()) {
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
    }
}



