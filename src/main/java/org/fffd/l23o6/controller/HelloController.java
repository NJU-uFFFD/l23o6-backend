package org.fffd.l23o6.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @CrossOrigin
    @GetMapping("/hello")
    public String hello() {
        return "Hello, l23o6!";
    }
}