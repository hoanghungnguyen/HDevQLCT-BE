package com.duan.service;

import com.duan.dto.TransactionDto;
import com.duan.model.Category;
import com.duan.model.Transaction;
import com.duan.model.User;
import com.duan.repository.CategoryRepository;
import com.duan.repository.TransactionRepository;
import com.duan.repository.UserRepository;
import com.duan.dto.BalanceStatsDto;
import com.duan.dto.MonthlyTrendDto;
import com.duan.dto.CategoryExpenseDto;
import com.duan.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionDto createTransaction(TransactionDto dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(dto.getAmount())
                .note(dto.getNote())
                .transactionDate(dto.getTransactionDate() != null ? dto.getTransactionDate() : LocalDate.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        dto.setId(savedTransaction.getId());
        dto.setTransactionDate(savedTransaction.getTransactionDate());
        return dto;
    }

    public TransactionDto updateTransaction(Integer id, TransactionDto dto, Integer userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only update your own transactions");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        transaction.setCategory(category);
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        transaction.setTransactionDate(dto.getTransactionDate() != null ? dto.getTransactionDate() : LocalDate.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDto(savedTransaction);
    }

    public void deleteTransaction(Integer id, Integer userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own transactions");
        }

        transactionRepository.delete(transaction);
    }

    public List<TransactionDto> getTransactionsByUserAndMonth(Integer userId, int month, int year) {
        return transactionRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public List<TransactionDto> getTransactionsByUser(Integer userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .type(transaction.getCategory().getType() != null ? transaction.getCategory().getType().name() : null)
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    public BalanceStatsDto getBalanceStats(Integer userId) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.income);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.expense);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return BalanceStatsDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }

    public List<MonthlyTrendDto> getMonthlyTrend(Integer userId, int year) {
        List<Object[]> results = transactionRepository.getMonthlyTrend(userId, year);
        // Khởi tạo List 12 tháng với giá trị 0
        List<MonthlyTrendDto> trend = new java.util.ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            trend.add(new MonthlyTrendDto(i, year, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        // Đổ data từ DB vào
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            BigDecimal income = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            BigDecimal expense = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            MonthlyTrendDto monthDto = trend.get(month - 1);
            monthDto.setIncome(income);
            monthDto.setExpense(expense);
        }

        return trend;
    }

    public List<CategoryExpenseDto> getCategoryExpense(Integer userId, int month, int year) {
        List<Object[]> results = transactionRepository.getExpenseByCategory(userId, month, year);
        
        // Tính tổng chi tiêu trong tháng để ra %
        BigDecimal totalExpenseMonth = results.stream()
                .map(row -> new BigDecimal(row[1].toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream().map(row -> {
            String categoryName = (String) row[0];
            BigDecimal totalAmount = new BigDecimal(row[1].toString());
            
            double percentage = 0.0;
            if (totalExpenseMonth.compareTo(BigDecimal.ZERO) > 0) {
                percentage = totalAmount.divide(totalExpenseMonth, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
            }

            return CategoryExpenseDto.builder()
                    .categoryName(categoryName)
                    .totalAmount(totalAmount)
                    .percentage(Math.round(percentage * 10.0) / 10.0) // Làm tròn 1 chữ số thập phân
                    .build();
        }).collect(Collectors.toList());
    }
}
