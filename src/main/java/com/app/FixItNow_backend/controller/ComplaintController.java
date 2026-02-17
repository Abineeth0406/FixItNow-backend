package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.entity.ComplaintStatus;
import com.app.FixItNow_backend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;


@RestController
@RequestMapping("/api/user/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")

public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Complaint createComplaint(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) MultipartFile file,
            Authentication authentication)
    throws IOException {

        String phone = authentication.getName();

        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);

        complaint.setUserPhone(phone);
        complaint.setStatus(ComplaintStatus.PENDING);

        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            complaint.setImagePath("uploads/" + fileName);
        }

        return complaintService.createComplaint(complaint);
    }



    @GetMapping
    public List<Complaint> getMyComplaints(Authentication authentication) {

        String phone = authentication.getName();

        return complaintService.getComplaintsByUser(phone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id,
                                             Authentication authentication) {

        String phone = authentication.getName();

        complaintService.deleteComplaint(id, phone);

        return ResponseEntity.ok("Complaint deleted successfully");
    }




    @GetMapping("/nearby")
    public List<Complaint> getNearbyComplaints(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5") Double radius
    ) {
        return complaintService.getNearbyComplaints(lat, lng, radius);
    }





}
