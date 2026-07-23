package com.reimbursement.repository;

import com.reimbursement.entity.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementRepository extends JpaRepository<Reimbursement, Integer> {

    List<Reimbursement> findByUserUserId(int userId);
}
