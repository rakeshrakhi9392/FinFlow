package com.reimbursement.repository;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.enums.ReimbursementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReimbursementRepository extends JpaRepository<Reimbursement, Integer> {

    @Query("""
            SELECT DISTINCT r FROM Reimbursement r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.department
            LEFT JOIN FETCH r.expenseCategory
            LEFT JOIN FETCH r.budget
            WHERE r.user.userId = :userId
            ORDER BY r.dateSubmitted DESC, r.reimbursementId DESC
            """)
    List<Reimbursement> findByUserUserId(@Param("userId") int userId);

    @Query("""
            SELECT DISTINCT r FROM Reimbursement r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.department
            LEFT JOIN FETCH r.expenseCategory
            LEFT JOIN FETCH r.budget
            WHERE r.status IN :statuses
            ORDER BY r.dateSubmitted DESC, r.reimbursementId DESC
            """)
    List<Reimbursement> findByStatusIn(@Param("statuses") Collection<ReimbursementStatus> statuses);

    @Query("""
            SELECT DISTINCT r FROM Reimbursement r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.department
            LEFT JOIN FETCH r.expenseCategory
            LEFT JOIN FETCH r.budget
            ORDER BY r.dateSubmitted DESC, r.reimbursementId DESC
            """)
    List<Reimbursement> findAllWithDetails();

    @Query("""
            SELECT DISTINCT r FROM Reimbursement r
            LEFT JOIN FETCH r.approvalHistory h
            LEFT JOIN FETCH h.actor
            WHERE r.reimbursementId = :id
            """)
    Optional<Reimbursement> findByIdWithHistory(@Param("id") int id);
}
