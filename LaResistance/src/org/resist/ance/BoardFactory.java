package org.resist.ance;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BoardFactory
{
    private final Scanner BoardDB;

    @Autowired
    private BoardFactory( @Value ( "${Board_DB_XML}" ) String boardDB )
    {
        BoardDB = new Scanner( boardDB );
    }

    public static final Board MakeBoard( int numberOfPlayers )
    {
        Board board = new Board( null );

        return board;
    }
}
