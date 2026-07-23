package com.reimbursement.repository;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.enums.ReimbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReimbursementRepository extends JpaRepository<Reimbursement, Integer> {

    List<Reimbursement> findByUserUserId(int userId);

    List<Reimbursement> findByStatusIn(Collection<ReimbursementStatus> statuses);

    @Query("""
            SELECT DISTINCT r FROM Reimbursement r
            LEFT JOIN FETCH r.approvalHistory h
            LEFT JOIN FETCH h.actor
            WHERE r.reimbursementId = :id
            """)
    Optional<Reimbursement> findByIdWithHistory(int id);
}
