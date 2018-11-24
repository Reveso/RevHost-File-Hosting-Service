package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.storage.model.FileDTO;
import com.lukasrosz.revhost.storage.service.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class PublicController {

    private StorageService storageService;

    @Autowired
    public PublicController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/file")
    public String showFilePage(@RequestParam("c") String fileCode, Model model)
            throws AccessDeniedException {
        FileDTO file = storageService.loadFile(fileCode);
        model.addAttribute("file", file);

        String downloadURL = "/download/" + file.getName() + "?code=" + fileCode;
        model.addAttribute("downloadURL", downloadURL);

        boolean fileOwner = getLoggedUser().equals(file.getUsername()) ? true : false;
        model.addAttribute("fileOwner", fileOwner);
        return "file-page";
    }

    @GetMapping("/download") //download?c=fileCode
    public String downloadFile(@RequestParam("c") String fileCode) throws AccessDeniedException {
        FileDTO file = storageService.loadFile(fileCode);
        return "redirect:/download/" + file.getName() + "?code=" + fileCode;
    }

    @GetMapping(value = "/download/{filename:.+}")
    public void downloadFile(HttpServletResponse response, @RequestParam("code") String fileCode,
                             @PathVariable("filename") String filename)
            throws AccessDeniedException {
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

    private String getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
