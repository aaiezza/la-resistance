package org.resist.ance;

import org.resist.ance.mech.Role;

public class Player
{

    private final String name;

    private final Role   role;

    public Player( String name, Role role )
    {
        this.name = name;
        this.role = role;
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
    public String toString()
    {
        return String.format( "%s\n %s", name, role );
    }
}
