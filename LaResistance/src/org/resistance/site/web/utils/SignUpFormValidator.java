package org.resistance.site.web.utils;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Alex Aiezza
 */
public class SignUpFormValidator implements Validator
{
    @Override
    public boolean supports( Class<?> clazz )
    {
        return UserForm.class.equals( clazz );
    }

    public void validateRequiredFields( Object target, Errors errors )
    {
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "first_name", "First Name required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "last_name", "Last Name Required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", "Email Required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "username", "Username Required" );
    }

    @Override
    public void validate( Object target, Errors errors )
    {
        validateRequiredFields( target, errors );

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "Password required" );

        UserForm form = (UserForm) target;

        if ( !StringUtils.isEmpty( form.getPassword() ) )
        {
            if ( !form.getPassword().equals( form.getConfirmPassword() ) )
            {
                errors.rejectValue( "confirmPassword", "passwordconfirm.mustmatch" );
            }
        }

        if ( form.getUsername().contains( " " ) )
        {
            errors.rejectValue( "username", "Username contains space" );
        }
    }
}
