package com.duan.dto;

import com.duan.model.TransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Integer id;
    private Integer userId;
    private String name;
    private TransactionType type;
    private String icon;
}
