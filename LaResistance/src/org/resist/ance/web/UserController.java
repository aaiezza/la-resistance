package org.resist.ance.web;

import static org.resist.ance.web.LoginController.JUST_JOINING_US;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.ShabaJdbcUserDetailsManager;
import org.resist.ance.web.utils.ShabaUser;
import org.resist.ance.web.utils.SignUpFormValidator;
import org.resist.ance.web.utils.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alex Aiezza
 */
@Controller
public class UserController
{

    private final static String               UNFINISHED_SIGN_UP_FORM = "_unifinshedSignUpForm_";

    private final Log                         LOGGER;

    private final ShabaJdbcUserDetailsManager USER_MAN;

    @Autowired
    public UserController(
        @Qualifier ( "Signup_Logger" ) Log logger,
        ShabaJdbcUserDetailsManager userMan )
    {
        LOGGER = logger;
        USER_MAN = userMan;
    }

    @RequestMapping ( method = POST, value = "retrieveUsers" )
    @ResponseBody
    public Collection<ShabaUser> getUsers() throws SQLException, IllegalAccessException
    {
        return USER_MAN.getUsers();
    }

    @RequestMapping ( method = GET, value = "userManagement" )
    public ModelAndView getUserManagementPage() throws SQLException
    {
        return new ModelAndView( "userManagement" );
    }

    @RequestMapping ( method = GET, value = "/signup" )
    public ModelAndView getSignupPage( HttpSession session )
    {
        if ( session.getAttribute( JUST_JOINING_US ) != null )
        {
            return new ModelAndView( "redirect:profile" );
        }
        return new ModelAndView( "signup" );
    }

    @ModelAttribute ( "newUserForm" )
    public UserForm populateNewUserForm( UserForm user, HttpSession session )
    {
        UserForm form = (UserForm) session.getAttribute( UNFINISHED_SIGN_UP_FORM );

        if ( !user.isBlank() )
        {
            user.clearPassword();
            return user;
        }

        if ( form != null )
        {
            form.clearPassword();
            return form;
        }

        return new UserForm();
    }

    // TODO Test this path
    @RequestMapping ( method = POST, value = "doesUserExist/{someusername}" )
    @ResponseBody
    public boolean doesUserExist( @PathVariable ( "someusername" ) String username )
            throws SQLException
    {
        return USER_MAN.userExists( username );
    }

    @RequestMapping ( method = POST, value = "signup" )
    public ModelAndView signup( UserForm form, BindingResult result, HttpServletRequest request )
            throws SQLException
    {
        // VALIDATE THE FORM
        SignUpFormValidator suValidator = new SignUpFormValidator();

        if ( !form.isBlank() )
        {
            request.getSession().setAttribute( UNFINISHED_SIGN_UP_FORM, form );
        }

        suValidator.validate( form, result );

        // CHECK IF USERNAME IS TAKEN
        try
        {
            if ( doesUserExist( form.getUsername() ) )
            {
                result.rejectValue( "username", "Username is already taken" );
            }
        } catch ( SQLException e )
        {
            result.reject( "" + e.getErrorCode(), e.getMessage() );
        }

        // PRINT OUT ALL ERRORS IF ANY
        if ( result.hasErrors() )
        {
            StringBuilder out = new StringBuilder();

            for ( ObjectError er : result.getAllErrors() )
            {
                out.append( er.getCode() ).append( "<br>" );
            }

            return new ModelAndView( "signup", "message", out );
        }

        // IF NO ERRORS, CREATE AND AUTHENTICATE USER
        String message = createNewUser( form );

        if ( message != null )
        {
            return new ModelAndView( "signup", "message", message );
        }

        LOGGER.info( String.format( "FRESH MEAT -> %s", form ) );

        return new ModelAndView( "redirect:login", "message", message );
    }

    private String createNewUser( UserForm user )
    {
        // ADD new user to database
        try
        {
            USER_MAN.createUser( user );
        } catch ( Exception e )
        {
            LOGGER.error( e.getMessage() );
            return e.getMessage();
        }

        return null;
    }

    @RequestMapping ( method = POST, value = "deleteUser/{someusername}" )
    private void deleteUser( @PathVariable ( "someusername" ) String username )
            throws SQLException, IllegalAccessException
    {
        USER_MAN.deleteUser( username );
    }

    @RequestMapping (
        method = POST,
        value = "updateUser",
        headers = { "userToUpdate" } )
    private void updateUser(
            @RequestHeader ShabaUser userToUpdate )
    {
        USER_MAN.updateUser( userToUpdate );
    }
}
