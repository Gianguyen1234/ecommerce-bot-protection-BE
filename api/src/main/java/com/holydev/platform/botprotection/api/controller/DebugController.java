package com.holydev.platform.botprotection.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}