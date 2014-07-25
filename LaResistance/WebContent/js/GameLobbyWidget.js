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

        function updateUsersOnline()
        {
            $.post("updateUsersOnline")
            .done( function( response ) {
                
                $("#userList tbody").remove();
                
                $( response.users ).each( function() {
                    $("#userList")
                    .append( $("<tr><td>" + this.username + "</td>").addClass("user")
                    );
                });
                
                $("#userList").trigger("update");//.trigger("sorton",[[[0,0]]]);
                
                updateUsersOnline();

            })
            .fail(function() {
                // NEED TO USE <code> tag instead of <textarea>
                //chatLog.append($("<span style='color:red;'>\nSERVER DOWN\n"));
                location.reload();
            });
        };

        function getUsersOnline()
        {
            $.post("usersOnline")
            .done( function( response ) {
                $( response.users ).each( function() {
                    $("#userList")
                    .append( $("<tr><td>" + this.username + "</td>").addClass("user")
                    );
                });
                $("#userList").tablesorter({sortList: [[0,0]]});
                updateUsersOnline();
            })
            .fail(function() {
                // NEED TO USE <code> tag instead of <textarea>
                //chatLog.append($("<span style='color:red;'>\nSERVER DOWN\n"));
                location.reload();
            });
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
        
        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            updateUserList : function()
            {
                getUsersOnline();
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
    gameLobbyWidget.updateUserList();
});