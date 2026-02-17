package com.app.FixItNow_backend.service;

import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.entity.ComplaintStatus;
import com.app.FixItNow_backend.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    // ✅ CREATE
    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.PENDING);
        return complaintRepository.save(complaint);
    }

    // ✅ USER VIEW
    public List<Complaint> getComplaintsByUser(String userPhone) {
        return complaintRepository.findByUserPhone(userPhone);
    }

    // ✅ ADMIN VIEW
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // ✅ ADMIN APPROVE + ASSIGN
    public Complaint approveAndAssign(Long id, String departmentPhone) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new IllegalStateException(
                    "Complaint cannot be approved. Current status: " + complaint.getStatus()
            );
        }

        if (departmentPhone == null || departmentPhone.isBlank()) {
            throw new IllegalArgumentException("Department phone must be provided");
        }

        complaint.setStatus(ComplaintStatus.APPROVED);
        complaint.setAssignedDepartmentPhone(departmentPhone);

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
    public Complaint setPriority(Long id, Integer priority) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setPriority(priority);

        return complaintRepository.save(complaint);
    }

    // ✅ ADMIN DELETE
    public void deleteComplaint(Long id, String phone) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!complaint.getUserPhone().equals(phone)) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        complaintRepository.delete(complaint);
    }

    public List<Complaint> getNearbyComplaints(Double lat, Double lng, Double radius) {
        return complaintRepository.findNearbyComplaints(lat, lng, radius);
    }


}
