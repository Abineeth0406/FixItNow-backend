package com.app.FixItNow_backend.service;

import com.app.FixItNow_backend.dto.ComplaintStatsDTO;
import com.app.FixItNow_backend.entity.*;
import com.app.FixItNow_backend.repository.ComplaintRepository;
import com.app.FixItNow_backend.repository.DepartmentRepository;
import com.app.FixItNow_backend.repository.UpvoteRepository;
import com.app.FixItNow_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    @Autowired
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final UpvoteRepository upvoteRepository;
//    private final DepartmentRepository departmentRepository;
    // ✅ CREATE
    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.PENDING);
        return complaintRepository.save(complaint);
    }

    // ✅ USER VIEW
    public List<Complaint> getComplaintsByUser(String userEmail) {
        return complaintRepository.findByUserEmail(userEmail);
    }

    // ✅ ADMIN VIEW
    public List<Complaint> getAllComplaints() {

        List<Complaint> complaints = complaintRepository.findAll();

        for (Complaint c : complaints) {
            long count = upvoteRepository.countByComplaint(c);
            c.setUpvotesCount(count);
        }

        return complaints;
    }













    // ✅ ADMIN APPROVE + ASSIGN
    @Transactional
    public Complaint approveAndAssign(Long id, String departmentEmail, PriorityLevel priority) {

        // 1️⃣ Fetch complaint safely
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Complaint not found with id: " + id));

        // 2️⃣ Ensure complaint is still pending
        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new IllegalStateException(
                    "Complaint cannot be approved. Current status: " + complaint.getStatus()
            );
        }

        // 3️⃣ Validate department phone
        if (departmentEmail == null || departmentEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Department phone must be provided");
        }

        // 4️⃣ Validate priority
        if (priority == null) {
            throw new IllegalArgumentException("Priority must be provided");
        }

        // 5️⃣ Optional: Validate department exists
//        UserRepository departmentRepository = null;
        User department = userRepository
                .findByEmail(departmentEmail)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        complaint.setAssignedDepartmentPhone(department.getEmail());
        // 6️⃣ Set fields
        complaint.setPriority(priority);
        complaint.setStatus(ComplaintStatus.APPROVED);
//        complaint.setAssignedDepartmentPhone(department.getPhone());
//        complaint.setAssignedDepartment(department); // If relationship exists

        // 7️⃣ Save and return
        return complaintRepository.save(complaint);
    }
























    // ✅ ADMIN REJECT
    public Complaint rejectComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new IllegalStateException(
                    "Complaint cannot be rejected. Current status: " + complaint.getStatus()
            );
        }

        complaint.setStatus(ComplaintStatus.REJECTED);

        return complaintRepository.save(complaint);
    }

    // ✅ DEPARTMENT START WORK
    public Complaint markInProgress(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        if (complaint.getStatus() != ComplaintStatus.APPROVED) {
            throw new IllegalStateException(
                    "Complaint must be APPROVED before starting work. Current status: "
                            + complaint.getStatus()
            );
        }

        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        return complaintRepository.save(complaint);
    }

    // ✅ DEPARTMENT RESOLVE
    public Complaint markResolved(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        if (complaint.getStatus() != ComplaintStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Complaint must be IN_PROGRESS before resolving. Current status: "
                            + complaint.getStatus()
            );
        }

        complaint.setStatus(ComplaintStatus.RESOLVED);

        return complaintRepository.save(complaint);
    }

    // ✅ DEPARTMENT VIEW
    public List<Complaint> getComplaintsByDepartment(String departmentPhone) {
        return complaintRepository.findByAssignedDepartmentPhone(departmentPhone);
    }

    // ✅ ADMIN PRIORITY
    public Complaint setPriority(Long id, PriorityLevel priority) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setPriority(priority);

        return complaintRepository.save(complaint);
    }

    // ✅ ADMIN DELETE
    public void deleteComplaint(Long id, String email) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        if (!complaint.getUserEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can delete only your own complaints"
            );
        }

        // 🔐 NEW RULE
        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Complaint cannot be deleted once it is in progress"
            );
        }

        complaintRepository.delete(complaint);
    }


    public List<Complaint> getNearbyComplaintsForUser(
            String email,
            ComplaintStatus status,
            String sortBy,
            Double radius
    ) {

        // 1️⃣ Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in db"));

        Double lat = user.getLatitude();
        Double lng = user.getLongitude();

        if (lat == null || lng == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please update your profile location first"
            );
        }

        // 2️⃣ Fetch nearby complaints
        List<Complaint> complaints =
//                complaintRepository.findNearbyComplaints(lat, lng, radius);
complaintRepository.findAll();
        // 3️⃣ Remove REJECTED complaints from public dashboard
        complaints = complaints.stream()
                .filter(c -> c.getStatus() != ComplaintStatus.REJECTED)
                .toList();

        // 4️⃣ Apply optional status filter
        if (status != null) {
            complaints = complaints.stream()
                    .filter(c -> c.getStatus() == status)
                    .toList();
        }

        // 5️⃣ Attach upvote count (single DB hit per complaint)
        for (Complaint complaint : complaints) {
            long count = upvoteRepository.countByComplaint(complaint);
            complaint.setUpvotesCount(count);
        }

        // 6️⃣ Apply sorting
        if ("UPVOTES".equalsIgnoreCase(sortBy)) {
            complaints = complaints.stream()
                    .sorted((a, b) ->
                            Long.compare(
                                    b.getUpvotesCount(),
                                    a.getUpvotesCount()
                            ))
                    .toList();
        }

        if ("PRIORITY".equalsIgnoreCase(sortBy)) {
            complaints = complaints.stream()
                    .sorted((a, b) ->
                            (b.getPriority() == null ? PriorityLevel.LOW : b.getPriority())
                                    .compareTo(
                                            a.getPriority() == null ? PriorityLevel.LOW : a.getPriority())
                    )
                    .toList();
        }

        return complaints;
    }


    public ComplaintStatsDTO getComplaintStats() {

        long total = complaintRepository.count();

        long pending = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);

        return new ComplaintStatsDTO(total, pending, inProgress, resolved);
    }

    public void updateComplaint(Long id, Complaint updatedComplaint, String email) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));

        if (!complaint.getUserEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can edit only your own complaints"
            );
        }

        // 🔐 NEW RULE
        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Complaint cannot be edited once it is in progress"
            );
        }

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setDescription(updatedComplaint.getDescription());
        complaint.setLocation(updatedComplaint.getLocation());

        complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint markResolvedByAdmin(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found with id: " + id));

        // 1️⃣ Must be IN_PROGRESS
        if (complaint.getStatus() != ComplaintStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Complaint must be IN_PROGRESS before resolving. Current status: "
                            + complaint.getStatus()
            );
        }

        // 2️⃣ Department must upload proof image
        if (complaint.getResolvedImageUrl() == null ||
                complaint.getResolvedImageUrl().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot resolve. Department has not uploaded fix proof image."
            );
        }

        complaint.setStatus(ComplaintStatus.RESOLVED);

        return complaintRepository.save(complaint);
    }


    public Complaint uploadResolvedImage(Long id, MultipartFile file) throws IOException {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        System.out.println("UPLOAD METHOD CALLED for id: " + id);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        complaint.setResolvedImageUrl("uploads/" + fileName);

        return complaintRepository.save(complaint);
    }

    public void markAsCompleted(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaintRepository.save(complaint);
    }
}
