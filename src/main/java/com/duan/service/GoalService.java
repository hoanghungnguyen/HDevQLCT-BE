package com.duan.service;

import com.duan.dto.GoalDto;
import com.duan.model.Goal;
import com.duan.model.User;
import com.duan.repository.GoalRepository;
import com.duan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalDto createGoal(GoalDto dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        Goal goal = Goal.builder()
                .user(user)
                .name(dto.getName())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(dto.getCurrentAmount() != null ? dto.getCurrentAmount() : BigDecimal.ZERO)
                .deadline(dto.getDeadline())
                .build();

        return mapToDto(goalRepository.save(goal));
    }

    public List<GoalDto> getGoalsByUser(Integer userId) {
        return goalRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GoalDto addMoneyToGoal(Integer goalId, BigDecimal amountToAdd, Integer userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Mục tiêu không tồn tại!"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật mục tiêu này!");
        }

        if (amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền nạp vào phải lớn hơn 0!");
        }

        BigDecimal newAmount = goal.getCurrentAmount().add(amountToAdd);
        
        // Không cho phép nạp lậm (vượt quá mục tiêu) để tránh số liệu sai lệch nếu cần
        if (newAmount.compareTo(goal.getTargetAmount()) > 0) {
            goal.setCurrentAmount(goal.getTargetAmount());
        } else {
            goal.setCurrentAmount(newAmount);
        }

        return mapToDto(goalRepository.save(goal));
    }

    public void deleteGoal(Integer goalId, Integer userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Mục tiêu không tồn tại!"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa mục tiêu này!");
        }

        goalRepository.delete(goal);
    }

    private GoalDto mapToDto(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .userId(goal.getUser().getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .deadline(goal.getDeadline())
                .build();
    }
}
