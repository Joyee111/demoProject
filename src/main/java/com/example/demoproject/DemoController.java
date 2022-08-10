package com.example.demoproject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping( "/upload")
public class DemoController {

    @RequestMapping("/test")
    public String Upload(MultipartFile multipartFile ){
        return "1";
    }
}
