package com.duan.controller;

import com.duan.dto.TransactionDto;
import com.duan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<com.duan.dto.BalanceStatsDto> getBalanceStats(@PathVariable Integer userId) {
        return ResponseEntity.ok(transactionService.getBalanceStats(userId));
    }
}
