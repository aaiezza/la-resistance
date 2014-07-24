package org.resist.ance.web.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsernameToShabaUserConverter implements Converter<String, ShabaUser>
{
    private final ShabaJdbcUserDetailsManager USER_MAN;

    @Autowired
    private UsernameToShabaUserConverter( ShabaJdbcUserDetailsManager userManager )
    {
        USER_MAN = userManager;
    }

    @Override
    public ShabaUser convert( String username )
    {
        return USER_MAN.loadShabaUserByUsername( username.equals( "" ) ? SecurityContextHolder
                .getContext().getAuthentication().getName() : username );
    }
}
