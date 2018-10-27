package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.exception.AccessToFileDeniedException;
import com.lukasrosz.revhost.storage.entity.FileDTO;
import com.lukasrosz.revhost.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/storage")
public class StorageController {

	private final StorageService storageService;

	@Autowired
	public StorageController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/files")
	public String showUserFiles(Model model) {
		List<FileDTO> files = storageService.loadLoggedUserFiles();

		model.addAttribute("files", files);
		return "user-files";
	}
	
	@GetMapping("/upload")
	public String showUploadPage() {
		return "upload-form";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file) {
		storageService.store(file);
		return "redirect:/";
	}

	@GetMapping("/changeAccess")
	public String changeFileAccess(@RequestParam("c") String fileCode, @RequestParam("a") String newAccess)
			throws AccessToFileDeniedException {
		storageService.setFileAccess(fileCode, newAccess);
		return "redirect:/storage/files";
	}
	
	@GetMapping("/delete")
	public String DeleteFile(@RequestParam("c") String fileCode) throws AccessToFileDeniedException {
		storageService.deleteFile(fileCode);
		return "redirect:/storage/files";
	}
	
	@ExceptionHandler(AccessToFileDeniedException.class)
	public String handleStorageFileNotFound(NullPointerException exc) {
		return "redirect:/error";
	}
}
