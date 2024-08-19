package com.quickwave.QuickWave.service;

import com.quickwave.QuickWave.Entity.File;
import com.quickwave.QuickWave.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;
    public File store(MultipartFile file) throws IOException {
        // Store file details
        String fileName = file.getOriginalFilename();
        File newFile = new File();
        newFile.setName(fileName);
        newFile.setType(file.getContentType());
        newFile.setData(file.getBytes()); // Store the file data

        // Save the file in the database
        return fileRepository.save(newFile);
    }

    public File getFile(Long id) {
        Optional<File> fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            File file = fileOptional.get();

            // Increment the download count
            file.setDownloadCount(file.getDownloadCount() + 1);

            // Save the updated file in the database
            fileRepository.save(file);

            return file;
        }
        return null;
    }
    public List<File> getAllFiles() {
        List<File> files = fileRepository.findAll();
        System.out.println("Number of files fetched: " + files.size());
        return files;
    }

}