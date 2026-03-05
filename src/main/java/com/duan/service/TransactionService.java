package com.duan.service;

import com.duan.dto.TransactionDto;
import com.duan.model.Category;
import com.duan.model.Transaction;
import com.duan.model.User;
import com.duan.repository.CategoryRepository;
import com.duan.repository.TransactionRepository;
import com.duan.repository.UserRepository;
import com.duan.dto.BalanceStatsDto;
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
}
