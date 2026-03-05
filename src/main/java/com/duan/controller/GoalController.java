package com.duan.controller;

import com.duan.dto.GoalDto;
import com.duan.security.CustomUserDetails;
import com.duan.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<?> createGoal(
            @RequestBody GoalDto goalDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            return ResponseEntity.ok(goalService.createGoal(goalDto, userDetails.getUser().getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<GoalDto>> getMyGoals(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userDetails.getUser().getId()));
    }

    @PutMapping("/{id}/add-money")
    public ResponseEntity<?> addMoneyToGoal(
            @PathVariable Integer id,
            @RequestBody Map<String, BigDecimal> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            BigDecimal amount = payload.get("amount");
            if (amount == null) {
                return ResponseEntity.badRequest().body("Vui lòng cung cấp số tiền 'amount'");
            }
            return ResponseEntity.ok(goalService.addMoneyToGoal(id, amount, userDetails.getUser().getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            goalService.deleteGoal(id, userDetails.getUser().getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
