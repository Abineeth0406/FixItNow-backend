package com.app.FixItNow_backend.controller;
import com.app.FixItNow_backend.entity.User;
import com.app.FixItNow_backend.entity.Role;
import com.app.FixItNow_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.FixItNow_backend.dto.ApproveRequest;
import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.entity.ComplaintStatus;
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

        return complaintService.approveAndAssign(id, request.getDepartmentPhone());
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
                                 @RequestParam Integer priority) {
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
                .fullName(request.getDeptName())   // deptName saved here
                .phone(request.getDeptId())        // deptId saved in phone field
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DEPARTMENT_AUTHORITY)
                .phoneVerified(true)
                .emailVerified(false)
                .build();

        userRepository.save(department);

        return "Department authority created successfully";
    }


}
