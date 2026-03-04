package com.duan.repository;

import com.duan.model.Transaction;
import com.duan.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByCategoryId(Integer categoryId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Integer userId, @Param("type") TransactionType type);
}
