package com.user.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation to validate email format.
 * 
 * Applied at the field level (e.g., in DTOs like {@code UserRequestDto}).
 * Uses {@link ValidEmailValidator} to perform regex-based email format validation.
 *
 * Usage:
 * <pre>
 *     @ValidEmail
 *     private String email;
 * </pre>
 * 
 * Optional override of default message via {@code message()}.
 * 
 * @author Pritam Singh
 */
@Documented
@Constraint(validatedBy = ValidEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    /**
     * Default error message when validation fails.
     *
     * @return the validation message
     */
	 String message() default "Email format is invalid"; 
	 /**
	 * Groups used by Bean Validation (unused in most cases).
	 *
	 * @return array of validation groups
	 */
	 Class<?>[] groups() default {}; 
	 /**
	 * Custom payload types (e.g., severity levels or metadata).
	 *
	 * @return array of payloads
	 */
	 Class<? extends Payload>[] payload() default {};
}

