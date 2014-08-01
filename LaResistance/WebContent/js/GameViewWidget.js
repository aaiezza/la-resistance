// Wrap code with module pattern
var GameViewWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameViewWidget = function( parentElement, activeGame, gameLinkFocusSetter )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var me = $("#p_user").html();
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function alertErrors(response)
        {
            if (response.length)
            {
                $(response).each(function()
                {
                    alert(this);
                });
            }
        }
        
        function updateGame( forceGet )
        {
            $.get( "updateGame/" + activeGame.gameID + "?forceGet=" + forceGet ).done( function(response) {
                
                if ( response == "timeout" )
                {
                    updateGame(false);
                    return;
                }
                
                makeGameViewWidget( container, response[0], gameLinkFocusSetter );
                
            });
        }
        
        function joinGame()
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post("joinGame/" + activeGame.gameID).done( alertErrors );
        }
        
        function unJoinGame()
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post("unJoinGame/" + activeGame.gameID).done( alertErrors );
        }

        function cancelGame()
        {
            $.post("cancelGame/" + activeGame.gameID).done( alertErrors );
        }
        
        function startGame()
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post("startGame/" + activeGame.gameID).done( alertErrors );
        }
        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        container.empty();
        
        container
        .append( $("<h3>").append("HOST: ").append( activeGame.host.name ) )
        .append( $("<h3>").append("Only taking ").append( activeGame.maxPlayers ).append(" new members!") )
        .append( $("<p id='currentPlayers'>").append("Current Members: ") );
        
        var length = activeGame.players.length - 1;
        $( activeGame.players ).each( function( i ) {
            $("#currentPlayers").append( this.name );
            if ( i < length )
            {
                $("#currentPlayers").append(", ");
            }
        });
        
        if ( activeGame.host.name == me )
        {
            container.append( $("<input id='cancel' type='button' value='Start Resistance'>").click( startGame ) );
            container.append( $("<input id='cancel' type='button' value='Cancel Resistance'>").click( cancelGame ) );
        }
        else
        {
            var alreadyJoined = false;
            $( activeGame.players ).each( function(){if ( this.name == me ){alreadyJoined = true; return false;}});
            
            if ( alreadyJoined )
            {
                container.append( $("<input id='unJoin' type='button' value='Leave Resistance'>").click( unJoinGame ) );
            } else
            {
                container.append( $("<input id='join' type='button' value='Join Resistance'>").click( joinGame ) );
            }
        }
        
        updateGame(false);
        
        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            update : function()
            {

            },
            empty : function()
            {
                container.empty();
            }
        };
    };
}();