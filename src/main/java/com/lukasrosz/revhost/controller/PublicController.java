package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.exception.AccessToFileDeniedException;
import com.lukasrosz.revhost.storage.entity.FileDTO;
import com.lukasrosz.revhost.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicController {

    private StorageService storageService;

    @Autowired
    public PublicController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/file")
    public String showFilePage(@RequestParam("c") String fileCode, Model model)
            throws AccessToFileDeniedException {
        FileDTO file = storageService.loadFile(fileCode);
        String downloadURL;
        model.addAttribute("file", file);
        if(file.isPublicAccess()) {
            downloadURL = file.getUrl();
        } else {
            downloadURL = "/storage/download/" + file.getName() + "?code=" + fileCode;
        }
        model.addAttribute("downloadURL", downloadURL);
        return "file-page";
    }

    //TODO: Exception Handler

}
