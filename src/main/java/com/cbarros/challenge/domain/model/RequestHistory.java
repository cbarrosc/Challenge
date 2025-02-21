package com.cbarros.challenge.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("request_history")
public class RequestHistory {
    @Id
    private Long id;
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private String error;
    private boolean success;
}