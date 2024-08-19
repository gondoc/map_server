package com.gondo.map.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller("/views")
public class ViewController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
