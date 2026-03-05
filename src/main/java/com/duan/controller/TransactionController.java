package com.duan.controller;

import com.duan.dto.TransactionDto;
import com.duan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.duan.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @RequestBody TransactionDto transactionDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDto, userDetails.getUser().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Integer id,
            @RequestBody TransactionDto transactionDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDto, userDetails.getUser().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.deleteTransaction(id, userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getMyTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userDetails.getUser().getId()));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDto>> filterTransactions(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserAndMonth(userDetails.getUser().getId(), month, year));
    }

    @GetMapping("/stats")
    public ResponseEntity<com.duan.dto.BalanceStatsDto> getMyBalanceStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getBalanceStats(userDetails.getUser().getId()));
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<com.duan.dto.MonthlyTrendDto>> getMonthlyTrend(
            @RequestParam int year,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getMonthlyTrend(userDetails.getUser().getId(), year));
    }

    @GetMapping("/stats/category")
    public ResponseEntity<List<com.duan.dto.CategoryExpenseDto>> getCategoryExpense(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getCategoryExpense(userDetails.getUser().getId(), month, year));
    }
}
