// Wrap code with module pattern
var GameLobbyWidget = function()
{
    var global = this;

    if (window.orientation == 0)
    {
        alert("For best results in the lobby, rotate device");
    }

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameLobbyWidget = function(parentElement)
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;

        var logoutOption = $("#logoutOption");

        var gameAndChatBlock = $("<div id='gameAndChatBlock'>").addClass(
        "gameLobby");

        var gameBlock = $("<div id='gameBlock'>");

        var gameList = $("<div id='gameList'>").addClass("gameLobby");

        var gameView = $("<div id='gameView'>").addClass("gameLobby");

        var chatView = $("<div id='chatView'>").addClass("gameLobby");

        var userList = $("<div id='userBlock'>").append(
        $("<table id='userList'>").addClass("gameLobby")).addClass("gameLobby");

        var createGameButton = $("<input id='newGameButton' type='button' value='Start a Resistance'>");

        var activeGames = [];

        var gameViewWidget;

        var chatWidget;

        var me = $.parseJSON($("#p_user").html());

        var gameIDtoFocusOn;

        var subscriptions = [];

        var lobbySock = new SockJS("http://" + location.host
        + ":8081/resist/lobbyUpdate", null, {
            /* protocols_whitelist : [ "websocket" ], */
            debug : true
        });
        var stompClient = Stomp.over(lobbySock);

        stompClient.ws.onclose = function()
        {
            alert("Poopsicles");
        };

        stompClient.connect({}, function(frame)
        {
            console.log('Connected: ' + frame);
            chatWidget = makeChatWidget(chatView, stompClient);

            subscriptions = [
                stompClient.subscribe('/queue/activeUsers', updateActiveUsers),

                stompClient.subscribe('/app/activeUsers'),

                stompClient.subscribe('/queue/activeGames', updateActiveGames),

                stompClient.subscribe('/user/queue/activeGames',
                updateActiveGames),

                stompClient.subscribe('/app/activeGames') ];
        });

        window.onbeforeunload = function()
        {
            if (stompClient.connected)
            {
                $(subscriptions).each(function()
                {
                    this.unsubscribe();
                });
            }
        }

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

        function updateActiveGames(response)
        {
            var res = $($.parseJSON(response.body));

            activeGames = [];
            $(".game").remove();

            $(res).each(function()
            {
                activeGames.push(this);
            });

            if (gameViewWidget)
                gameViewWidget.clearIfGameCanceled();

            $(activeGames).each(
            function(i, game)
            {
                var gameLink = $("<h2>").append(game.gameID).addClass("game");
                gameLink.click(function()
                {
                    if (!$(this).hasClass("selected"))
                    {
                        if (gameViewWidget)
                            gameViewWidget.unsubscribe();
                        gameViewWidget = makeGameViewWidget(gameView, game,
                        setGameIDtoFocusOn, stompClient);
                        $(".game").removeClass("selected");
                        $(this).addClass("selected");
                    } else
                    {
                        $(".game").removeClass("selected");
                        gameViewWidget.clear();
                    }
                });

                $("#gameList").prepend(gameLink);

                var imInGame = false;

                $(game.players).each(function()
                {
                    if (this.name == me.username)
                    {
                        imInGame = true;
                        return false;
                    }
                });

                if (imInGame)
                {
                    setGameIDtoFocusOn(game.gameID);
                }

                if (!gameViewWidget && game.host.name == me.username)
                {
                    setGameIDtoFocusOn(game.gameID);
                }
            });

            var gameID = getGameIDtoFocusOn();

            if (gameID && _.contains(_.map(activeGames, function(game)
            {
                return game.gameID;
            }), gameID))
            {
                $(".game").each(function()
                {
                    if ($(this).html() == gameID)
                    {
                        $(this).click();
                        return false;
                    }
                });
            }
        }

        function updateActiveUsers(response)
        {
            var res = $($.parseJSON(response.body));

            $("#userList tbody").remove();

            $(res).each(
            function()
            {
                $("#userList").append(
                $("<tr><td>" + this.username + "</td>").addClass("user")
                .toggleClass("me", this.username == me.username));
            });

            $("#userList").trigger("update");//.trigger("sorton",[[[0,0]]]);
        }

        function setGameIDtoFocusOn(gameID)
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
            $.post("createGame").done(alertErrors);
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption(logoutOption);

        gameList.append(createGameButton);

        gameBlock.append(gameList).append(gameView);

        gameAndChatBlock.append(gameBlock.append(userList)).append(chatView);

        container.append(gameAndChatBlock);

        $("#userList").append(
        $("<thead><tr><th class='header'>Users Online</th>")).append(
        $("<tbody>"));

        createGameButton.click(createGame);

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
            },
            log : function(message)
            {

            },
            getChatWidget : function()
            {
                return chatWidget;
            }
        };
    };
}();

$(document).ready(function()
{
    gameLobbyWidget = makeGameLobbyWidget($("#core"));
    gameLobbyWidget.init();
});