package org.fffd.l23o6.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController{
    @GetMapping("/hello")
    public String hello(){
        return "Hello, l23o6!";
    }
}