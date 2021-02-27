package org.resist.ance.web.utils;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class User
{
    private String first_name;

    private String last_name;

    private String username;

    private String email;

    private String password;

    private String confirmPassword;

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

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public void setConfirmPassword( String confirmPassword )
    {
        this.confirmPassword = confirmPassword;
    }

    public void clearPassword()
    {
        password = "";
        confirmPassword = "";
    }

    public boolean isBlank()
    {
        return isEmpty( first_name ) && isEmpty( last_name ) && isEmpty( username ) &&
                isEmpty( email );
    }

    public String toString()
    {
        return String.format( "%s (%s %s)", username, first_name, last_name );
    }

}
