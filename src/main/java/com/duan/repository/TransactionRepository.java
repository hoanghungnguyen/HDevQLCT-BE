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
    boolean existsByCategoryId(Integer categoryId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Integer userId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    List<Transaction> findByUserIdAndMonthAndYear(@Param("userId") Integer userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT MONTH(t.transactionDate) as month, " +
           "SUM(CASE WHEN t.category.type = com.duan.model.TransactionType.income THEN t.amount ELSE 0 END) as income, " +
           "SUM(CASE WHEN t.category.type = com.duan.model.TransactionType.expense THEN t.amount ELSE 0 END) as expense " +
           "FROM Transaction t WHERE t.user.id = :userId AND YEAR(t.transactionDate) = :year " +
           "GROUP BY MONTH(t.transactionDate) ORDER BY month ASC")
    List<Object[]> getMonthlyTrend(@Param("userId") Integer userId, @Param("year") int year);

    @Query("SELECT t.category.name as categoryName, SUM(t.amount) as totalAmount " +
           "FROM Transaction t WHERE t.user.id = :userId AND t.category.type = com.duan.model.TransactionType.expense " +
           "AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year " +
           "GROUP BY t.category.name ORDER BY totalAmount DESC")
    List<Object[]> getExpenseByCategory(@Param("userId") Integer userId, @Param("month") int month, @Param("year") int year);
}
