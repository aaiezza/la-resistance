package org.resistance.site;

import org.resistance.site.mech.Role;

/**
 * @author Alex Aiezza
 */
public class Player implements Cloneable
{
    private final String     GAME_ID;

    private final String     name;

    protected transient Role role;

    private boolean          roleLearned = false;

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

    public void setRoleLearned()
    {
        roleLearned = true;
    }

    public boolean isRoleLearned()
    {
        return roleLearned;
    }

    @Override
    protected Player clone() throws CloneNotSupportedException
    {
        Player p = new Player( name, GAME_ID );
        
        p.roleLearned = roleLearned;
        
        return p;
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
        return String.format( "(%s, %s)", name, role );
    }
}
