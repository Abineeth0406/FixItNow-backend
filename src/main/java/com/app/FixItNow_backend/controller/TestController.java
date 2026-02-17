package com.app.FixItNow_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/user/test")
    public String userTest() {
        return "User access granted";
    }

    @GetMapping("/api/admin/test")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/api/department/test")
    public String departmentTest() {
        return "Department access granted";
    }
}
