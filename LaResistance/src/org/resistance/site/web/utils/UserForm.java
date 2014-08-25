package org.resistance.site.web.utils;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * This class is only for passing a signup form object
 *
 * @author Alex Aiezza
 */
@SuppressWarnings ( "serial" )
public class UserForm implements UserDetails
{
    private transient String                  password;

    private String                            first_name;

    private String                            last_name;

    private String                            email;

    private String                            date_joined;

    private String                            last_online;

    private transient String                  confirmPassword;

    private String                            username;

    private boolean                           enabled;

    private ArrayList<SimpleGrantedAuthority> authorities;

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * @return the first_name
     */
    public String getFirst_name()
    {
        return first_name;
    }

    /**
     * @param first_name
     *            the first_name to set
     */
    public void setFirst_name( String first_name )
    {
        this.first_name = first_name;
    }

    /**
     * @return the last_name
     */
    public String getLast_name()
    {
        return last_name;
    }

    /**
     * @param last_name
     *            the last_name to set
     */
    public void setLast_name( String last_name )
    {
        this.last_name = last_name;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail( String email )
    {
        this.email = email;
    }

    /**
     * @return the date_joined
     */
    public String getDate_joined()
    {
        return date_joined;
    }

    /**
     * @param date_joined the date_joined to set
     */
    public void setDate_joined( String date_joined )
    {
        this.date_joined = date_joined;
    }

    /**
     * @return the last_online
     */
    public String getLast_online()
    {
        return last_online;
    }

    /**
     * @param last_online the last_online to set
     */
    public void setLast_online( String last_online )
    {
        this.last_online = last_online;
    }

    /**
     * @return the confirmPassword
     */
    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    /**
     * @param confirmPassword
     *            the confirmPassword to set
     */
    public void setConfirmPassword( String confirmPassword )
    {
        this.confirmPassword = confirmPassword;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername( String username )
    {
        this.username = username;
    }

    public void setEnabled( boolean enabled )

    {
        this.enabled = enabled;
    }

    public boolean isBlank()
    {
        return isEmpty( first_name ) && isEmpty( last_name ) && isEmpty( getUsername() ) &&
                isEmpty( email );
    }

    public void clearPassword()
    {
        password = "";
        confirmPassword = "";
    }

    /*
     * public void setAuthorities( ArrayList<String> authorities ) { for (
     * String auth : authorities ) { this.authorities.add( new
     * SimpleGrantedAuthority( auth ) ); } }
     */

    public void setAuthorities( ArrayList<SimpleGrantedAuthority> authorities )
    {
        this.authorities = authorities;
    }

    @Override
    public ArrayList<SimpleGrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public String toString()
    {
        return username;
    }
}
