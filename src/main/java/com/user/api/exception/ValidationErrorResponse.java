package com.user.api.exception;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorResponse {
    private int status;
    private String error;
    private List<FieldErrorDetail> details;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FieldErrorDetail {
        private String field;
        private String message;
    }
}
