package com.example.deflate.controllers;

import com.example.deflate.service.CodeServ;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
}


