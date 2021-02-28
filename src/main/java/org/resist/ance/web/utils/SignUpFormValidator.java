package org.resist.ance.web.utils;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SignUpFormValidator implements Validator {
  @Override
  public boolean supports(Class<?> clazz) {
    return User.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "first_name", "First Name required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "last_name", "Last Name Required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email Required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username Required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password required");

    User form = (User) target;

    if (!StringUtils.isEmpty(form.getPassword())) {
      if (!form.getPassword().equals(form.getConfirmPassword())) {
        errors.rejectValue("passwordConfirm", "passwordconfirm.mustmatch");
      }
    }
  }
}
