package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.dto.ComplaintStatsDTO;
import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.entity.ComplaintStatus;
import com.app.FixItNow_backend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

        String email = authentication.getName();

        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);

        complaint.setUserEmail(email);
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



    @GetMapping("/my")
    public List<Complaint> getMyComplaints(Authentication authentication) {

        System.out.println("Authenticated user: " + authentication.getName());

        String email = authentication.getName();
        return complaintService.getComplaintsByUser(email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id,
                                             Authentication authentication) {

        String email = authentication.getName();

        complaintService.deleteComplaint(id, email);

        return ResponseEntity.ok("Complaint deleted successfully");
    }




    @GetMapping("/nearby")
    public List<Complaint> getNearbyComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "100") Double radius,
            Authentication authentication
    ) {

        String email = authentication.getName();
        return complaintService.getNearbyComplaintsForUser(email, status, sortBy, radius);
    }

    @GetMapping("/stats")
    public ResponseEntity<ComplaintStatsDTO> getComplaintStats() {
        return ResponseEntity.ok(complaintService.getComplaintStats());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaint updatedComplaint,
            Authentication authentication) {

        String email = authentication.getName();

        complaintService.updateComplaint(id, updatedComplaint, email);

        return ResponseEntity.ok("Complaint updated successfully");
    }




}
