package com.lukasrosz.revhost.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lukasrosz.revhost.storage.entities.RevHostFile;
import com.lukasrosz.revhost.storage.entities.helperentities.Video;
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
		List<RevHostFile> files = storageService.loadAll(getLoggedUser());

		model.addAttribute("files", files);
		return "user-files";
	}
	
	@GetMapping("/file")
	public String showUserFiles(@RequestParam("code") String fileCode, Model model,
			@RequestHeader String host) {
	
		RevHostFile file = storageService.loadFile(fileCode);

		if(!file.getUsername().equals(getLoggedUser())) {
			return "redirect:/"; //TODO no access exception
		}
		
		if(file.getType().equals("file")) {
			return "redirect:/storage/download/" + file.getName() + "?code=" + fileCode;
		} else if (file.getType().equals("video")) {
			String shortUrl = host + "/storage/file?code=" + fileCode;
			String url = storageService.getVideoUrl(fileCode);
			String title = file.getName().substring(0, file.getName().length()-4).replaceAll("_", " ");
			Video video = new Video(title, url, shortUrl);			
			model.addAttribute("video", video);
			return "video-page";
		}
		
		return "redirect:/"; //TODO unknown type exception
	}

	@GetMapping("/upload")
	public String showUploadPage() {
		return "upload-form";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		storageService.store(file);
		return "redirect:/";
	}

	@GetMapping("") // storage?v=fileCode
	public String downloadFile(@RequestParam("v") String fileCode) {
		
		RevHostFile file = storageService.loadFile(fileCode);
		return "redirect:/storage/download/" + file.getName() + "?code=" + fileCode;
	}

	@GetMapping(value = "/download/{filename:.+}")
	public void downloadFilee(HttpServletResponse response, @RequestParam("code") String fileCode,
			@PathVariable String filename) {
		response.setContentType("application/file");
		try {
			InputStream is = storageService.loadAsInputStream(fileCode);

			if (is == null) {
				throw new NullPointerException(); //TODO some error there
			}

			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/delete")
	public String DeleteFile(@RequestParam("code") String fileCode) {
		storageService.deleteFile(fileCode);
		return "redirect:/storage/files";
	}
	
	public void test() { 
	
		System.out.println("===============>>>> 1");
    	InputStream in = storageService.loadAsInputStream("o1thX9EXRH");
    	System.out.println("===============>>>> 2");

    	File file = new File("/sss");
    	System.out.println("===============>>>> 3");

    	try {
        	System.out.println("===============>>>> 4");
			FileUtils.copyInputStreamToFile(in, file);
	    	System.out.println("===============>>>> 5");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// does not support byte-range requests
    @GetMapping(path = "/plain", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain() {
		System.out.println("===============>>>> plain()");

		
    	File file = new File("/sss");
    	if(!file.exists()) {
    		test();
    	}
    	
    	return new FileSystemResource(file);
    }

	@ExceptionHandler(NullPointerException.class)
	public String handleStorageFileNotFound(NullPointerException exc) {
		return "redirect:/error"; //TODO some error page
	}
	
	private String getLoggedUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}
