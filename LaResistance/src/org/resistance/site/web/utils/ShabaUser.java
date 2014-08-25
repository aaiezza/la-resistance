package org.resistance.site.web.utils;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * @author Alex Aiezza
 */
public class ShabaUser extends User
{
    private static final long serialVersionUID = 1L;

    private final String      first_name;

    private final String      last_name;

    private final String      email;

    private final String      date_joined;

    private final String      last_online;

    public ShabaUser(
        String username,
        String password,
        String first_name,
        String last_name,
        String email,
        String date_joined,
        String last_online,
        Collection<? extends GrantedAuthority> authorities )
    {
        this( username, password, true, first_name, last_name, email, date_joined, last_online,
                true, true, true, authorities );
    }

    public ShabaUser(
        String username,
        String password,
        boolean enabled,
        String first_name,
        String last_name,
        String email,
        String date_joined,
        String last_online,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities )
    {
        super( username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities );

        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.date_joined = date_joined;
        this.last_online = last_online;
    }

    /**
     * @return the first_name
     */
    public String getFirst_name()
    {
        return first_name;
    }

    /**
     * @return the last_name
     */
    public String getLast_name()
    {
        return last_name;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @return the date_joined
     */
    public String getDate_joined()
    {
        return date_joined;
    }

    /**
     * @return the last_online
     */
    public String getLast_online()
    {
        return last_online;
    }

    @Override
    public String toString()
    {
        return String.format( "%s (%s %s)", getUsername(), first_name, last_name );
    }

    // No need to also override hashcode since User super class already does.
    @Override
    public boolean equals( Object rhs )
    {
        if ( rhs instanceof User )
        {
            return getUsername().equals( ( (User) rhs ).getUsername() );
        }
        if ( rhs instanceof String )
        {
            return getUsername().equals( rhs );
        }
        return false;
    }

    public static final ShabaUser ShabaUserFromForm( UserForm user )
    {
        return new ShabaUser( user.getUsername(), user.getPassword(), user.isEnabled(),
                user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getDate_joined(),
                user.getLast_online(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), user.getAuthorities() );


    }
}
