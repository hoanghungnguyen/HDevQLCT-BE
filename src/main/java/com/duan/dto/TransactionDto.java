package com.duan.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Integer id;
    private Integer userId;
    private Integer categoryId;
    private BigDecimal amount;
    private String note;
    private LocalDate transactionDate;
}
