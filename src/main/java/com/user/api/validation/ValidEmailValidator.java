package com.user.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator logic for {@link ValidEmail} annotation.
 * Uses a regular expression to match valid email formats.
 * <p>
 * Can be applied to fields of type {@code String} only.
 * </p>
 * 
 * Example valid formats:
 * <ul>
 *   <li>john.doe@example.com</li>
 *   <li>pritam.singh@company.org</li>
 * </ul>
 * 
 * Invalid examples:
 * <ul>
 *   <li>pritam.singh@example</li>
 *   <li>john@.com</li>
 * </ul>
 * 
 * @author Pritam Singh
 */
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

	/**
     * Regular expression for validating email formats.
     */
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Validates the email format using a regex.
     *
     * @param email the email string to validate
     * @param context the validator context
     * @return true if email is valid, false otherwise
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
