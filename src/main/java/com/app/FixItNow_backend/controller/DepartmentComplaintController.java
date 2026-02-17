package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@RestController
@RequestMapping("/api/department/complaints")
@RequiredArgsConstructor
public class DepartmentComplaintController {

    private final ComplaintService complaintService;

    @PutMapping("/{id}/start")
    public Complaint startWork(@PathVariable Long id) {
        return complaintService.markInProgress(id);
    }

    @PutMapping("/{id}/resolve")
    public Complaint resolveComplaint(@PathVariable Long id) {
        return complaintService.markResolved(id);
    }

    @GetMapping
    public List<Complaint> getAssignedComplaints(Authentication authentication) {

        String phone = authentication.getName();

        return complaintService.getComplaintsByDepartment(phone);
    }

}
