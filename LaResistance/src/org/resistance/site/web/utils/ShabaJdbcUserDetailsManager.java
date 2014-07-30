package org.resistance.site.web.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.Assert;

/**
 * @author Alex Aiezza
 */
public class ShabaJdbcUserDetailsManager extends JdbcUserDetailsManager
{
    /**
     * 
     */
    public final static String           QUERY_NUMBER_OF_USERS_SQL   = "SELECT COUNT(username) FROM users WHERE username = ?";

    /**
     * 
     */
    public final static String           QUERY_USER_BY_USERNAME      = "SELECT users.username, password, enabled, first_name, last_name, email, role FROM users LEFT JOIN user_role ON users.username=user_role.username WHERE users.username = ?";

    /**
     * 
     */
    public final static String           NEW_USER_SQL                = "INSERT INTO users (username, password, enabled, first_name, last_name, email) VALUES ( ?, ?, ?, ?, ?, ? )";

    /**
     * 
     */
    public final static String           NEW_USER_ROLE_SQL           = "INSERT INTO user_role (username, role) VALUES( ?, ? )";

    /**
     * 
     */
    public final static String           DELETE_USER_SQL             = "DELETE FROM users WHERE username = ?";

    /**
     * 
     */
    public final static String           DELETE_USER_ROLE_SQL        = "DELETE FROM user_role WHERE username = ?";

    /**
     * 
     */
    public final static String           SELECT_ALL_USERS_SQL        = "SELECT users.username, password, enabled, first_name, last_name, email, role FROM users LEFT JOIN user_role ON users.username=user_role.username";

    /**
     * 
     */
    public final static String           SELECT_ALL_ROLES_SQL        = "SELECT role FROM roles";

    /**
     * 
     */
    public final static String           DELETE_USER_AUTHORITIES_SQL = "delete from user_role where username = ?";

    /**
     * 
     */
    public final static String           UPDATE_USER_SQL             = "UPDATE users SET enabled = ?, first_name = ?, last_name = ?, email = ? WHERE username = ?";

    private AuthenticationManager        authenticationManager;

    private UserCache                    userCache                   = new NullUserCache();

    public final static GrantedAuthority ADMIN                       = new SimpleGrantedAuthority(
                                                                             "ROLE_ADMIN" );

    public final static GrantedAuthority USER                        = new SimpleGrantedAuthority(
                                                                             "ROLE_USER" );

    /**
     * @param user
     */
    public void createUser( final UserForm user )
    {
        getJdbcTemplate().update( NEW_USER_SQL, new PreparedStatementSetter()
        {
            public void setValues( PreparedStatement ps ) throws SQLException
            {
                ps.setString( 1, user.getUsername() );
                ps.setString( 2, user.getPassword() );
                ps.setBoolean( 3, true );
                ps.setString( 4, user.getFirst_name() );
                ps.setString( 5, user.getLast_name() );
                ps.setString( 6, user.getEmail() );
            }
        } );

        if ( getEnableAuthorities() )
        {
            getJdbcTemplate().update( NEW_USER_ROLE_SQL, user.getUsername(), USER.getAuthority() );
        }
    }

    /**
     * @param user
     */
    public void updateUser( final ShabaUser user )
    {
        try
        {
            if ( !getShabaUser().getUsername().equals( user.getUsername() ) )
            {
                checkForAdminRights();
            }

            getJdbcTemplate().update( UPDATE_USER_SQL, new PreparedStatementSetter()
            {
                public void setValues( PreparedStatement ps ) throws SQLException
                {
                    ps.setBoolean( 1, user.isEnabled() );
                    ps.setString( 2, user.getFirst_name() );
                    ps.setString( 3, user.getLast_name() );
                    ps.setString( 4, user.getEmail() );
                    ps.setString( 5, user.getUsername() );
                }
            } );

            if ( getEnableAuthorities() )
            {

                deleteUserAuthorities( user.getUsername() );
                for ( GrantedAuthority auth : user.getAuthorities() )
                {
                    insertUserAuthorities( user, auth );
                }
            }

            userCache.removeUserFromCache( user.getUsername() );
        } catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }

    /**
     * @see org.springframework.security.provisioning.JdbcUserDetailsManager#deleteUser(java.lang.String)
     */
    public void deleteUser( String username )
    {
        try
        {
            if ( checkForAdminRights( loadShabaUserByUsername( username ) ) )
            {
                return;
            }

            checkForAdminRights();

            if ( getEnableAuthorities() )
            {
                deleteUserAuthorities( username );
            }
            getJdbcTemplate().update( DELETE_USER_SQL, username );
            userCache.removeUserFromCache( username );
        } catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }

    /**
     * @param user
     */
    private void insertUserAuthorities( ShabaUser user, GrantedAuthority auth )
    {
        getJdbcTemplate().update( NEW_USER_ROLE_SQL, user.getUsername(), auth.getAuthority() );
    }

    /**
     * @param username
     */
    private void deleteUserAuthorities( String username )
    {
        getJdbcTemplate().update( DELETE_USER_AUTHORITIES_SQL, username );
    }

    public void changePassword( String oldPassword, String newPassword )
            throws AuthenticationException
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if ( currentUser == null )
        {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user." );
        }

        String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user
        // with the supplied password.
        if ( authenticationManager != null )
        {
            logger.debug( "Reauthenticating user '" + username + "' for password change request." );

            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken( username,
                    oldPassword ) );
        } else
        {
            logger.debug( "No authentication manager set. Password won't be re-checked." );
        }

        logger.debug( "Changing password for user '" + username + "'" );

        getJdbcTemplate().update( DEF_CHANGE_PASSWORD_SQL, newPassword, username );

        SecurityContextHolder.getContext().setAuthentication(
            createNewAuthentication( currentUser, newPassword ) );

        userCache.removeUserFromCache( username );
    }

    public void changePassword( String username, String oldPassword, String newPassword )
            throws AuthenticationException
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if ( currentUser == null )
        {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user." );
        }

        if ( currentUser.getName().equals( username ) )
        {
            changePassword( oldPassword, newPassword );
            return;
        }

        if ( !currentUser.getAuthorities().contains( ADMIN ) )
        {
            throw new AccessDeniedException(
                    "Only Administrators can change the password of another user" );
        }

        // If an authentication manager has been set, re-authenticate the user
        // with the supplied password.
        if ( authenticationManager != null )
        {
            logger.debug( "Reauthenticating user '" + currentUser.getName() +
                    "' for password change request." );

            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                    currentUser.getName(), oldPassword ) );
        } else
        {
            logger.debug( "No authentication manager set. Password won't be re-checked." );
        }

        logger.debug( "Changing password for user '" + username + "'" );

        getJdbcTemplate().update( DEF_CHANGE_PASSWORD_SQL, newPassword, username );

        userCache.removeUserFromCache( username );
    }

    public List<ShabaUser> getUsers()
    {
        List<ShabaUser> users = getJdbcTemplate().query( SELECT_ALL_USERS_SQL,
            new ShabaUserListExtractor() );

        return users;
    }

    public List<String> getAvailableAuthorities()
    {
        return getJdbcTemplate().queryForList( SELECT_ALL_ROLES_SQL, String.class );
    }

    public boolean checkForAdminRights( ShabaUser user )
    {
        return user != null && user.getAuthorities().contains( ADMIN );
    }

    public UserDetails checkForAdminRights() throws IllegalAccessException
    {
        ShabaUser user = getShabaUser();

        if ( user == null || !user.isEnabled() || !user.getAuthorities().contains( ADMIN ) )
        {
            throw new IllegalAccessException( "INVALID CREDENTIALS" );
        }

        return user;
    }

    public ShabaUser getShabaUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ShabaUser shabaUser = null;
        if ( auth != null && auth.getPrincipal() instanceof UserDetails )
        {
            shabaUser = loadShabaUserByUsername( ( (UserDetails) auth.getPrincipal() )
                    .getUsername() );
        }

        return shabaUser;
    }

    protected ShabaUser createUserDetails(
            String username,
            ShabaUser userFromUserQuery,
            List<GrantedAuthority> combinedAuthorities )
    {
        String returnUsername = userFromUserQuery.getUsername();

        if ( !isUsernameBasedPrimaryKey() )
        {
            returnUsername = username;
        }

        return new ShabaUser( returnUsername, userFromUserQuery.getPassword(),
                userFromUserQuery.isEnabled(), userFromUserQuery.getFirst_name(),
                userFromUserQuery.getLast_name(), userFromUserQuery.getEmail(), true, true, true,
                combinedAuthorities );
    }

    public ShabaUser loadShabaUserByUsername( String username ) throws UsernameNotFoundException
    {
        List<ShabaUser> users = loadShabaUsersByUsername( username );

        if ( users.size() == 0 )
        {
            logger.debug( "Query returned no results for user '" + username + "'" );

            throw new UsernameNotFoundException( messages.getMessage( "JdbcDaoImpl.notFound",
                new Object [] { username }, String.format( "Username %s not found", username ) ) );
        }

        ShabaUser user = users.get( 0 ); // contains no GrantedAuthority[]

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        if ( getEnableAuthorities() )
        {
            dbAuthsSet.addAll( loadUserAuthorities( user.getUsername() ) );
        }

        if ( getEnableGroups() )
        {
            dbAuthsSet.addAll( loadGroupAuthorities( user.getUsername() ) );
        }

        List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>( dbAuthsSet );

        addCustomAuthorities( user.getUsername(), dbAuths );

        if ( dbAuths.size() == 0 )
        {
            logger.debug( "User '" + username +
                    "' has no authorities and will be treated as 'not found'" );

            throw new UsernameNotFoundException( messages.getMessage( "JdbcDaoImpl.noAuthority",
                new Object [] { username },
                String.format( "User %s has no GrantedAuthority", username ) ) );
        }

        return createUserDetails( username, user, dbAuths );
    }

    /**
     * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of
     * UserDetails objects. There should normally only be one matching user.
     */
    protected List<ShabaUser> loadShabaUsersByUsername( String username )
    {
        return getJdbcTemplate().query( QUERY_USER_BY_USERNAME, new String [] { username },
            new ShabaUserMapper() );
    }

    private class ShabaUserMapper implements ParameterizedRowMapper<ShabaUser>
    {
        @Override
        public ShabaUser mapRow( ResultSet rs, int rowNum ) throws SQLException
        {
            Collection<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();

            String auths = rs.getString( "role" );

            roles.add( new SimpleGrantedAuthority( auths ) );

            ShabaUser user = new ShabaUser( rs.getString( "username" ), rs.getString( "password" ),
                    rs.getBoolean( "enabled" ), rs.getString( "first_name" ),
                    rs.getString( "last_name" ), rs.getString( "email" ), true, true, true, roles );

            return user;
        }
    }

    private class ShabaUserListExtractor implements ResultSetExtractor<List<ShabaUser>>
    {
        private final ShabaUserMapper rowMapper;

        private int                   rowsExpected;

        public ShabaUserListExtractor()
        {
            this( new ShabaUserMapper(), 0 );
        }

        public ShabaUserListExtractor( ShabaUserMapper rowMapper, int rowsExpected )
        {
            Assert.notNull( rowMapper, "RowMapper is required" );
            this.rowMapper = rowMapper;
            this.rowsExpected = rowsExpected;
        }

        @Override
        public List<ShabaUser> extractData( ResultSet rs ) throws SQLException
        {
            HashMap<String, ShabaUser> results = ( this.rowsExpected > 0
                                                                        ? new HashMap<String, ShabaUser>(
                                                                                rowsExpected )
                                                                        : new HashMap<String, ShabaUser>() );
            int rowNum = 0;
            while ( rs.next() )
            {
                ShabaUser user = rowMapper.mapRow( rs, rowNum++ );

                if ( results.containsKey( user.getUsername() ) )
                {
                    ShabaUser inUser = results.get( user.getUsername() );
                    ArrayList<GrantedAuthority> combinedAuthorities = new ArrayList<GrantedAuthority>();

                    combinedAuthorities.addAll( inUser.getAuthorities() );
                    combinedAuthorities.addAll( user.getAuthorities() );

                    results.put( user.getUsername(),
                        createUserDetails( user.getUsername(), user, combinedAuthorities ) );
                } else
                {
                    results.put( user.getUsername(), user );
                }
            }

            return new ArrayList<ShabaUser>( results.values() );
        }
    }
}
