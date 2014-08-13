package org.resistance.site.web.chat;

import java.util.HashMap;

public class NaughtyWordTransformer
{
    public final HashMap<String, String> FILTHY_MAP = new HashMap<String, String>();

    public NaughtyWordTransformer()
    {
        FILTHY_MAP.put( "fuck", "frack" );
        FILTHY_MAP.put( "shit", "\uD83D\uDCA9" );
        FILTHY_MAP.put( "sh!t", "\uD83D\uDCA9" );
        FILTHY_MAP.put( "pussy", "kitten" );
        FILTHY_MAP.put( "damn", "darn" );
        FILTHY_MAP.put( "bitch", "meany" );
        FILTHY_MAP.put( "b!tch", "meany" );
        FILTHY_MAP.put( "dick", "bearded dragon" );
        FILTHY_MAP.put( "d!ck", "bearded dragon" );
        FILTHY_MAP.put( "cock", "bearded dragon" );
        FILTHY_MAP.put( "c0ck", "bearded dragon" );
        FILTHY_MAP.put( "vagina", "taco" );
        FILTHY_MAP.put( "vag!na", "taco" );
        FILTHY_MAP.put( "penis", "toe" );
        FILTHY_MAP.put( "pen!s", "toe" );
        FILTHY_MAP.put( "fag", "I " );
        FILTHY_MAP.put( "nigger", "narf" );
        FILTHY_MAP.put( "nigga", "narfa" );
        FILTHY_MAP.put( "spic", "span" );
        FILTHY_MAP.put( "wop", "whopper" );
        FILTHY_MAP.put( "black", "white" );
        FILTHY_MAP.put( "white", "black" );
        FILTHY_MAP.put( "cunt", "bunny" );
        FILTHY_MAP.put( "ass", "butt" );
        FILTHY_MAP.put( "tit", "boo-bee" );
    }

    public final String makeNice( String naughtyWords, boolean includeNestedWords )
    {
        String betterWordz [] = naughtyWords.split( " " );

        StringBuilder betterWords = new StringBuilder();

        for ( String word : betterWordz )
        {
            if ( includeNestedWords )
            {
                word = searchNestedWords( word );
            } else
            {
                word = FILTHY_MAP.getOrDefault( word.toLowerCase(), word );
            }
            betterWords.append( word ).append( " " );
        }

        return betterWords.toString().trim();
    }

    private final String searchNestedWords( String word )
    {
        for ( String filth : FILTHY_MAP.keySet() )
        {
            if ( word.toLowerCase().contains( filth.toLowerCase() ) )
            {
                word = word.toLowerCase().replaceAll( filth, FILTHY_MAP.get( filth ) );
                break;
            }
        }
        return word;
    }
}
