package org.resistance.site;

import org.resistance.site.mech.Role;

/**
 * @author Alex Aiezza
 */
public class Player
{
    private final String GAME_ID;

    private final String name;

    protected transient Role       role;

    public Player( String name, String gameID )
    {
        this.GAME_ID = gameID;
        this.name = name;
    }

    public String getGameID()
    {
        return GAME_ID;
    }

    public Role getRole()
    {
        return role;
    }

    void setRole( Role role )
    {
        this.role = role;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof Player )
        {
            return name.equals( ( (Player) o ).name );
        }
        return false;
    }

    @Override
    public String toString()
    {
        return String.format( "%s\n %s", name, role );
    }
}
