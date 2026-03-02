package com.app.FixItNow_backend.controller;
import com.app.FixItNow_backend.entity.*;
import com.app.FixItNow_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.FixItNow_backend.dto.ApproveRequest;
import com.app.FixItNow_backend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.app.FixItNow_backend.dto.CreateDepartmentRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


//    @GetMapping
//    public List<Complaint> getAllComplaints() {
//        return complaintService.getAllComplaints();
//    }

    @PutMapping("/{id}/approve")
    public Complaint approveComplaint(
            @PathVariable Long id,
            @RequestBody ApproveRequest request) {
System.out.println("Approved complaint ");
        return complaintService.approveAndAssign(id, request.getDepartmentEmail(), request.getPriority());
    }


    @PutMapping("/{id}/reject")
    public Complaint rejectComplaint(@PathVariable Long id) {
        return complaintService.rejectComplaint(id);
    }

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PutMapping("/{id}/priority")
    public Complaint setPriority(@PathVariable Long id,
                                 @RequestParam PriorityLevel priority) {
        return complaintService.setPriority(id, priority);
    }

//    @PostMapping("/create-department")
//    public String createDepartment(@RequestBody User request) {
//
//        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
//            throw new RuntimeException("Phone already exists");
//        }
//
//        User department = User.builder()
//                .fullName(request.getFullName())
//                .phone(request.getPhone())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.DEPARTMENT_AUTHORITY)
//                .phoneVerified(true)
//                .emailVerified(false)
//                .build();
//
//        userRepository.save(department);
//
//        return "Department authority created successfully";
//    }

    @GetMapping("/all-users")
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin cannot be deleted");
        }

        userRepository.delete(user);

        return "User deleted successfully";
    }

    @PostMapping("/create-department")
    public String createDepartment(@RequestBody CreateDepartmentRequest request) {

        if (userRepository.findByPhone(request.getDeptId()).isPresent()) {
            throw new RuntimeException("Department ID already exists");
        }

        User department = User.builder()
                .fullName(request.getDeptName())
                .email(request.getDeptId())   // ✅ USE EMAIL
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DEPARTMENT_AUTHORITY)
                .emailVerified(true)
                .phoneVerified(false)
                .build();

        userRepository.save(department);

        return "Department authority created successfully";
    }


    @GetMapping("/departments")
    public List<User> getAllDepartments() {
        return userRepository.findByRole(Role.DEPARTMENT_AUTHORITY);
    }


    @PutMapping("/{id}/resolve")
    public Complaint resolveComplaint(@PathVariable Long id) {
        return complaintService.markResolvedByAdmin(id);
    }


    @PutMapping("/admin/verify/{id}")
    public ResponseEntity<?> verifyComplaint(@PathVariable Long id) {
        complaintService.markAsCompleted(id);
        return ResponseEntity.ok("Complaint marked as completed");
    }


}
