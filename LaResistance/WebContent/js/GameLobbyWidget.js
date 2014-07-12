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
        
        var playerList = $( "<div id='playerList'>" ).addClass( "gameLobby" ).addClass( "rightPane" );

        var createGameButton = $("<input id='newGameButton' type='button' value='Start a Resistance'>");

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function retrieveActiveGames()
        {
            // SEND REQUEST TO SERVER
            $.post("gameList", null, "json").done(function(response)
            {
                if (typeof response == "string")
                {
                    window.location.reload(false);
                }
                $("#approves").html(response.approves);
                $("#denies").html(response.denies);
            });
        };
        
        function createNewGame()
        {
            // SEND REQUEST TO SERVER
            $.post("gameList", null, "json").done(function(response)
            {
                if (typeof response == "string")
                {
                    window.location.reload(false);
                }
                $("#approves").html(response.approves);
                $("#denies").html(response.denies);
            });
        };

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption( logoutOption );

        gameList.append(createGameButton);
        
        gameBlock.append(gameList).append(gameView);
        
        gameAndChatBlock.append(gameBlock).append(chatView);
        
        container.append(gameAndChatBlock).append(playerList);
        
        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            refresh : function()
            {
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
});