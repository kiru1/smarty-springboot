package com.example.smarty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.smarty.model.File;
import com.example.smarty.service.FileService;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {
	@Autowired
	private FileService fileService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			File savedFile = fileService.storeFile(file);
			return ResponseEntity.ok(savedFile);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
		}
	}
}