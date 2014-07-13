package org.resist.ance.web;

import static org.resist.ance.web.LoginController.JUST_JOINING_US;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.SignUpFormValidator;
import org.resist.ance.web.utils.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController
{
    final static String               QUERY_NUMBER_OF_USERS_SQL_FORMAT = "SELECT COUNT(username) FROM users WHERE username='%s'";

    private final static String       NEW_USER_SQL_FORMAT              = "INSERT INTO users (username, password, enabled, first_name, last_name, email) VALUES ( '%s', '%s', %b, '%s', '%s', '%s' );";

    private final static String       NEW_USER_ROLE_SQL_FORMAT         = "INSERT INTO user_role (username, role) VALUES( '%s', '%s' );";

    private final static String       DELETE_USER_SQL_FORMAT           = "DELETE FROM user WHERE username=%s;";

    private final static String       DELETE_USER_ROLE_SQL_FORMAT      = "DELETE FROM user_role WHERE username=%s;";

    private final static String       SELECT_ALL_USERS_SQL             = "SELECT users.username, password, enabled, first_name, last_name, email, role FROM users LEFT JOIN user_role ON users.username=user_role.username;";

    private final static String       UPDATE_USER_ENABLITY_SQL_FORMAT  = "UPDATE users SET enabled=%2$b WHERE username=%1$s;";

    private final static String       UNFINISHED_SIGN_UP_FORM          = "_unifinshedSignUpForm_";

    private final Log                 LOGGER;

    protected DriverManagerDataSource dataSource;

    protected AuthenticationManager   authMan;

    @Autowired
    public UserController(
        @Qualifier ( "Signup_Logger" ) Log logger,
        DriverManagerDataSource dataSource,
        @Qualifier ( "authMan" ) AuthenticationManager authMan )
    {
        LOGGER = logger;
        this.dataSource = dataSource;
        this.authMan = authMan;
    }

    @RequestMapping ( method = POST, value = "retrieveUsers" )
    @ResponseBody
    public Collection<User> getUsers() throws SQLException, IllegalAccessException
    {
        checkForAdminRights();

        // Call dataSource to get all users and their roles
        HashMap<String, User> users = new HashMap<String, User>();

        Statement sql = dataSource.getConnection().createStatement();

        sql.executeQuery( SELECT_ALL_USERS_SQL );

        ResultSet rs = sql.getResultSet();

        while ( rs.next() )
        {
            User user = new User();

            user.setUsername( rs.getString( "username" ) );
            user.setEnabled( rs.getBoolean( "enabled" ) );
            user.setFirst_name( rs.getString( "first_name" ) );
            user.setLast_name( rs.getString( "last_name" ) );
            user.setEmail( rs.getString( "email" ) );

            if ( users.containsKey( user.getUsername() ) )
            {
                users.get( user.getUsername() ).addRole( rs.getString( "role" ) );
            } else
            {
                user.addRole( rs.getString( "role" ) );
                users.put( user.getUsername(), user );
            }
        }

        return users.values();
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
    public User populateNewUserForm( User user, HttpSession session )
    {
        User form = (User) session.getAttribute( UNFINISHED_SIGN_UP_FORM );

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

        return new User();
    }

    // TODO Test this path
    @RequestMapping ( method = POST, value = "doesUserExist/{someusername}" )
    @ResponseBody
    public boolean doesUserExist( @PathVariable ( "someusername" ) String username )
            throws SQLException
    {
        Statement sql = dataSource.getConnection().createStatement();

        sql.executeQuery( String.format( QUERY_NUMBER_OF_USERS_SQL_FORMAT, username ) );

        ResultSet rs = sql.getResultSet();

        if ( rs == null || !rs.next() )
        {
            sql.close();
            throw new SQLException( "No Result" );
        }

        boolean userExists = rs.getInt( 1 ) != 0;

        sql.close();
        return userExists;
    }

    @RequestMapping ( method = POST, value = "signup" )
    public ModelAndView signup( User form, BindingResult result, HttpServletRequest request )
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

        // TODO SEE IF THIS IS EVEN NECESSARY!
        authenticateUserAndSetSession( form, request );

        LOGGER.info( String.format( "FRESH MEAT -> %s", form ) );

        return new ModelAndView( "redirect:login", "message", message );
    }

    private String createNewUser( User user )
    {
        // ADD new user to database
        try
        {
            Statement sql = dataSource.getConnection().createStatement();

            sql.addBatch( String.format( NEW_USER_SQL_FORMAT, user.getUsername(),
                user.getPassword(), true, user.getFirst_name(), user.getLast_name(),
                user.getEmail(), "ROLE_USER" ) );

            sql.addBatch( String.format( NEW_USER_ROLE_SQL_FORMAT, user.getUsername(), "ROLE_USER" ) );

            sql.executeBatch();
            sql.close();
        } catch ( SQLException e )
        {
            LOGGER.error( e.getMessage() );
            return String.format( "SQL Error Code: %d<br>%s", e.getErrorCode(), e.getMessage() );
        }

        return null;
    }

    private void authenticateUserAndSetSession( User user, HttpServletRequest request )
    {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword() );

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails( new WebAuthenticationDetails( request ) );
        Authentication authenticatedUser = authMan.authenticate( token );

        SecurityContextHolder.getContext().setAuthentication( authenticatedUser );
    }

    @RequestMapping ( method = POST, value = "deleteUser/{someusername}" )
    private void deleteUser( @PathVariable ( "someusername" ) String username )
            throws SQLException, IllegalAccessException
    {
        // CHECK FOR ROLE ADMIN ON CURRENTLY LOGGED IN USER
        UserDetails userDetails = checkForAdminRights();

        // USER THAT IS LOGGED IN CANNOT DELETE HIMSELF!

        // DELETE user from to database
        Statement sql = dataSource.getConnection().createStatement();

        sql.addBatch( String.format( DELETE_USER_SQL_FORMAT, username ) );

        sql.addBatch( String.format( DELETE_USER_ROLE_SQL_FORMAT, username ) );

        sql.executeBatch();
        sql.close();
    }

    @RequestMapping ( method = POST, value = "alterEnablityOfUser/{someusername}" )
    private void alterEnablityOfUser(
            @PathVariable ( "someusername" ) String username,
            boolean enabled ) throws SQLException, IllegalAccessException
    {
        // CHECK FOR ROLE ADMIN ON CURRENTLY LOGGED IN USER
        UserDetails userDetails = checkForAdminRights();

        // USER THAT IS LOGGED IN CANNOT DELETE HIMSELF!

        // DELETE user from to database
        Statement sql = dataSource.getConnection().createStatement();

        sql.addBatch( String.format( DELETE_USER_SQL_FORMAT, username ) );

        sql.addBatch( String.format( DELETE_USER_ROLE_SQL_FORMAT, username ) );

        sql.executeBatch();
        sql.close();
    }

    private UserDetails checkForAdminRights() throws IllegalAccessException
    {
        UserDetails userDetails = getUserDetails();

        if ( userDetails == null || !userDetails.isEnabled() ||
                !userDetails.getAuthorities().toString().contains( "ROLE_ADMIN" ) )
        {
            throw new IllegalAccessException( "INVALID CREDENTIALS" );
        }

        return userDetails;
    }

    static UserDetails getUserDetails()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetails userDetails = null;
        if ( principal instanceof UserDetails )
        {
            userDetails = (UserDetails) principal;
        }
        
        return userDetails;
    }
}
