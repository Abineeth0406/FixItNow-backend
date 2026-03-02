package com.app.FixItNow_backend.repository;

import com.app.FixItNow_backend.entity.Complaint;
import com.app.FixItNow_backend.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserEmail(String userEmail);

    List<Complaint> findByAssignedDepartmentPhone(String assignedDepartmentPhone);

    long countByStatus(ComplaintStatus status);

    @Query(value = """
SELECT * FROM complaint c
WHERE (
    6371 * acos(
        cos(radians(:lat)) *
        cos(radians(c.latitude)) *
        cos(radians(c.longitude) - radians(:lng)) +
        sin(radians(:lat)) *
        sin(radians(c.latitude))
    )
) <= :radius
""", nativeQuery = true)
    List<Complaint> findNearbyComplaints(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius
    );

}
