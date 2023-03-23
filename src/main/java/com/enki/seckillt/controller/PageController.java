package com.enki.seckillt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Enki
 * @Version 1.0
 */
@Controller
@RequestMapping("/page")
public class PageController {
    @RequestMapping("login")
    public String loginPage(){
        return "login";
    }
}
