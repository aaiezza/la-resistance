package org.resist.ance.web.utils;

import static org.apache.commons.lang.StringUtils.isEmpty;


/**
 * This class is only for passing a signup form object
 * 
 * @author Alex Aiezza
 */
public class UserForm
{
    private String password;

    private String first_name;

    private String last_name;

    private String email;

    private String confirmPassword;

    private String username;

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

}
