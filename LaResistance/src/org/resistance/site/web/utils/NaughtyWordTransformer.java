package org.resistance.site.web.utils;

import java.util.HashMap;

public class NaughtyWordTransformer
{
    public final HashMap<String, String> FILTHY_MAP = new HashMap<String, String>();
    
    public NaughtyWordTransformer()
    {
        FILTHY_MAP.put( "fuck", "pajamas" );
        FILTHY_MAP.put( "shit", "poop" );
        FILTHY_MAP.put( "pussy", "kitten" );
        FILTHY_MAP.put( "damn", "darn" );
        FILTHY_MAP.put( "bitch", "meany" );
        FILTHY_MAP.put( "dick", "a dragon" );
        FILTHY_MAP.put( "d!ck", "a dragon" );
        FILTHY_MAP.put( "vagina", "taco" );
        FILTHY_MAP.put( "penis", "finger" );
        FILTHY_MAP.put( "fag", "I " );
    }

    public final String makeNice( String naughtyWords )
    {
        String betterWordz [] = naughtyWords.split( " " );

        StringBuilder betterWords = new StringBuilder();
        
        for( String word : betterWordz )
        {
            for( String filth : FILTHY_MAP.keySet() )
            {
                if ( word.toLowerCase().contains( filth.toLowerCase() ) )
                {
                    word = word.toLowerCase().replaceAll( filth, FILTHY_MAP.get( filth ) );
                }
            }
            betterWords.append( FILTHY_MAP.getOrDefault( word.toLowerCase(), word ) ).append( " " );
        }

        return betterWords.toString();
    }
}
