package com.gavoza.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hc")
@RestController
public class HealthCheck {
    @GetMapping
    public String hc(){
        return "ok";
    }
}
