package com.example.smarty.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.smarty.model.File;
import com.example.smarty.repository.FileRepository;

@Service
public class FileService {
	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	private FileRepository fileRepository;

	public File storeFile(MultipartFile file) throws IOException {

		// Ensure directory exists
		Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(dirPath);

		// Create unique filename to prevent overwrites. e.g. "1691841023456_myphoto.jpg"
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		 // build full path example:uploads/1691841023456_myphoto.jpg
		Path targetLocation = dirPath.resolve(fileName);

		// Save file locally
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		File fileData = new File();
		fileData.setFileName(file.getOriginalFilename());
		fileData.setFilePath(targetLocation.toString());
		fileData.setContentType(file.getContentType());
		fileData.setSize(file.getSize());

		return fileRepository.save(fileData);

	}
}
