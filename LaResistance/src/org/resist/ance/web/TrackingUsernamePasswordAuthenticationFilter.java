package org.resist.ance.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.resist.ance.web.utils.ShabaJdbcUserDetailsManager;
import org.resist.ance.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class TrackingUsernamePasswordAuthenticationFilter extends
        UsernamePasswordAuthenticationFilter
{
    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final UserTracker                 USER_TRACKER;

    @Autowired
    private TrackingUsernamePasswordAuthenticationFilter(
        UserTracker userTracker,
        ShabaJdbcUserDetailsManager userManager )
    {
        USER_TRACKER = userTracker;
        USER_MAN = userManager;
    }

    /**
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#successfulAuthentication(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain,
     *      org.springframework.security.core.Authentication)
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult ) throws IOException, ServletException
    {
        super.successfulAuthentication( request, response, chain, authResult );
        USER_TRACKER.addUser( USER_MAN.loadShabaUserByUsername( ( (UserDetails) authResult
                .getPrincipal() ).getUsername() ) );
    }
}
