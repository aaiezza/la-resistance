// Wrap code with module pattern
var GameLobbyWidget = function()
{
    var global = this;
    
    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameLobbyWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var logoutOption = $( "#logoutOption" );
        
        var gameAndChatBlock = $( "<div id='gameAndChatBlock'>" ).addClass( "gameLobby" ).addClass( "leftPane" );
        
        var gameBlock = $( "<div id='gameBlock'>" );
        
        var gameList = $( "<div id='gameList'>").addClass( "gameLobby" );
        
        var gameView = $( "<div id='gameView'>" ).addClass( "gameLobby" );
        
        var chatView = $( "<div id='chatView'>" ).addClass( "gameLobby" );
        
        var userList = $( "<div id='userBlock'>" ).append( $("<table id='userList'>").addClass( "gameLobby" ) ).addClass( "gameLobby" ).addClass( "rightPane" );

        var createGameButton = $( "<input id='newGameButton' type='button' value='Start a Resistance'>" );
        
        var chatWidget = makeChatWidget( chatView ).init();
        
        var activeGames = [];
        
        var gameViewWidget;
        
        var me = $("#p_user").html();
        
        var gameIDtoFocusOn;

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
        
        function setGameIDtoFocusOn( gameID )
        {
            gameIDtoFocusOn = gameID;
        }
        
        function getGameIDtoFocusOn()
        {
            var id = gameIDtoFocusOn;
            gameIDtoFocusOn = null;
            return id;
        }
        
        function createGame()
        {
            $.post("createGame").done( alertErrors );
        }
        
        function retrieveActiveGames( forceGet )
        {
            // SEND REQUEST TO SERVER
            $.get("activeGames/" + forceGet).done( function(response)
            {
                if ( response == "timeout" )
                {
                    return;
                }
                
                activeGames = [];
                $(".game").remove();
                if ( gameViewWidget ) gameViewWidget.empty();
                
                $(response).each( function() {
                    activeGames.push(this);
                });
                
                $(activeGames).each( function( i, game ) {
                    var gameLink = $("<h2>").append(game.gameID).addClass("game");
                    gameLink.click( function() {
                        $(".game").removeClass("selected");
                        $(this).addClass("selected");

                        gameViewWidget = makeGameViewWidget( gameView, game, setGameIDtoFocusOn );
                    });
                    
                    $("#gameList").prepend( gameLink );
                });
                
                var gameID = getGameIDtoFocusOn();
                
                if ( gameID && _.contains( _.map( activeGames, function(game){ return game.gameID; } ), gameID ) )
                {
                    $(".game").each( function() {
                        if( $(this).html() == gameID )
                        {
                            $(this).click();
                            return false;
                        }
                    });
                }

            }).always( function(){retrieveActiveGames(false);} );
        };

        function getUsersOnline( forceGet )
        {
            $.get("usersOnline/" + forceGet)
            .done( function( response ) {
                
                if ( response == "timeout" )
                {
                    return;
                }

                $("#userList tbody").remove();
                
                $( response ).each( function() {
                    $("#userList")
                    .append( $("<tr><td>" + this.username + "</td>").addClass("user").toggleClass( "me", this.username == me )
                    );
                });
                
                $("#userList").trigger("update")//.trigger("sorton",[[[0,0]]]);
            })
            .always( function(){ getUsersOnline(false) } );
        };

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption( logoutOption );

        gameList.append(createGameButton);
        
        gameBlock.append(gameList).append(gameView);
        
        gameAndChatBlock.append(gameBlock).append(chatView);
        
        container.append(gameAndChatBlock).append(userList);
        
        $("#userList").append( $("<thead><tr><th class='header'>Users Online</th>") ).append( $("<tbody>") );
        
        createGameButton.click( createGame );
        
        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            init : function()
            {
                $("#userList").tablesorter();
                getUsersOnline(true);
                retrieveActiveGames(true);
            },
            log : function(message)
            {

            }
        };
    };
}();

$(document).ready(function()
{
    gameLobbyWidget = makeGameLobbyWidget($("#core"));
    gameLobbyWidget.init();
});