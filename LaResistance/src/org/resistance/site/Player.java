package org.resistance.site;

import org.resistance.site.mech.Role;

/**
 * @author Alex Aiezza
 */
public class Player
{

    private final String name;

    protected Role       role;

    public Player( String name )
    {
        this.name = name;
    }

    public Role getRole()
    {
        return role;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode() + role.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof Player )
        {
            return name.equals( ( (Player) o ).name ) && role.equals( ( (Player) o ).role );
        }
        return false;
    }

    @Override
    public String toString()
    {
        return String.format( "%s\n %s", name, role );
    }
}
