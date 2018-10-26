package com.lukasrosz.revhost.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lukasrosz.revhost.storage.entity.FileDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.lukasrosz.revhost.exception.AccessToFileDeniedException;
import com.lukasrosz.revhost.storage.service.StorageService;

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

	@GetMapping("") // storage?v=fileCode
	public String downloadFile(@RequestParam("v") String fileCode) throws AccessToFileDeniedException {
		FileDTO file = storageService.loadFile(fileCode);
		return "redirect:/storage/download/" + file.getName() + "?code=" + fileCode;
	}

	@GetMapping("/changeFileAccess")
	public String changeFileAccess(@RequestParam("c") String fileCode, @RequestParam("a") String newAccess)
			throws AccessToFileDeniedException {
		storageService.setFileAccess(fileCode, newAccess);
		return "redirect:/storage/files";
	}

	@GetMapping(value = "/download/{filename:.+}")
	public void downloadFile(HttpServletResponse response, @RequestParam("code") String fileCode)
			throws AccessToFileDeniedException {
		response.setContentType("application/file");
		try {
			InputStream is = storageService.loadAsInputStream(fileCode);

			if (is == null) {
				throw new NullPointerException();
			}

			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/delete")
	public String DeleteFile(@RequestParam("code") String fileCode) throws AccessToFileDeniedException {
		storageService.deleteFile(fileCode);
		return "redirect:/storage/files";
	}
	
	@ExceptionHandler(AccessToFileDeniedException.class)
	public String handleStorageFileNotFound(NullPointerException exc) {
		return "redirect:/error";
	}
}
