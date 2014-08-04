// Wrap code with module pattern
var GameViewWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameViewWidget = function( parentElement, gameDisplayed, gameLinkFocusSetter, stompClient )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var activeGame = gameDisplayed;
        
        var container = parentElement;
        
        var me = $("#p_user").html();
        
        var subscriptions = [];
        
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
        
        function updateGame( response )
        {
            activeGame = $.parseJSON(response.body);
            fillWidget();
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
            $.post("startGame/" + activeGame.gameID).done( function(response) {alertErrors(response);} );
        }
        
        function fillWidget()
        {
            container.empty();
            
            container
            .append( $("<h3>").append("HOST: ").append( activeGame.host.name ) )
            .append( $("<h3>").append("We have (").append( activeGame.players.length ).append(" of ").append( activeGame.maxPlayers ).append(") members!") )
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
        }
        
        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        subscriptions = [
        stompClient.subscribe("/queue/game/" + activeGame.gameID, updateGame),
        
        stompClient.subscribe("/user/queue/game/" + activeGame.gameID, updateGame),
        
        stompClient.subscribe("/app/game/" + activeGame.gameID),
        ];
        
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
            clearIfGameCanceled : function( activeGames )
            {
                var stillHere = false;
                $(_.map(activeGames, function(game){return game.gameID;})).each( function() {
                    if ( this == activeGame.gameID )
                    {
                        stillHere = true;
                        return false;
                    }
                });
                
                if ( stillHere )
                {
                    gameLinkFocusSetter( activeGame.gameID );
                }
                
                container.empty();
            },
            unsubscribe : function()
            {
                $(subscriptions).each( function(){ this.unsubscribe(); });
            }
        };
    };
}();