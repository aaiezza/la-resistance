// Wrap code with module pattern
var GameLobbyWidget = function()
{
    var global = this;

    //    if (window.orientation == 0)
    //    {
    //        alert("For best results in the lobby, rotate device");
    //    }

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

        var gameListBlock = $("<div id='gameListBlock'>").addClass("gameLobby");

        var gameList = $("<div id='gameList'>").addClass("gameLobby");

        var gameView = $("<div id='gameView'>").addClass("gameLobby");

        var chatView = $("<div id='chatView'>").addClass("gameLobby");

        var userBlock = $("<div id='userBlock'>").append(
        $("<table id='userList'>").addClass("gameLobby")).addClass("gameLobby");

        var createGameBlock = $("<div id='createGameBlock'>");

        var createGameButton = $("<input id='newGameButton' type='button' value='Start a Resistance'>");

        var activeGames = [];

        var gameViewWidget;

        /*
         * Used to see if this is the firstLoad of the page. If it is, of the
         * active games, if any, the one where the current user is the host will
         * be selected.
         */
        var firstLoad = true;

        /*
         * Tracks which game the user has selected of all the active games in
         * the list. Then, if there is a change to the list it will not affect
         * which game the user has selected.
         */
        var selectedGame;

        var chatWidget;

        var me = $.parseJSON($("#p_user").html());

        var gameIDtoFocusOn;

        var subscriptions = [];

        var lobbySock;

        var stompClient;

        function makeConnection()
        {
            lobbySock = new SockJS('/resist/stompshake', null, {
                /* protocols_whitelist : [ "websocket" ], */
                debug : true
            });
            stompClient = Stomp.over(lobbySock);

            stompClient.connect({}, function(frame)
            {
                console.log('Connected: ' + frame);
                chatWidget = makeChatWidget(chatView, stompClient);

                subscriptions = [
                    stompClient.subscribe('/topic/activeUsers',
                    updateActiveUsers),

                    stompClient
                    .subscribe('/app/activeUsers', updateActiveUsers),

                    stompClient.subscribe('/topic/activeGames',
                    updateActiveGames),

                    stompClient.subscribe('/user/topic/activeGames',
                    updateActiveGames),

                    stompClient
                    .subscribe('/app/activeGames', updateActiveGames) ];
            });
        }

        makeConnection();

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
                        {
                            gameViewWidget.clear();
                        }
                        gameViewWidget = makeGameViewWidget(gameView, game,
                        setGameIDtoFocusOn, stompClient);
                        $(".game").removeClass("selected");
                        $(this).addClass("selected");
                        selectedGame = game;
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

                if (imInGame && firstLoad)
                {
                    setGameIDtoFocusOn(game.gameID);
                    firstLoad = false;
                } else if (selectedGame && game.gameID == selectedGame.gameID)
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
            if (id)
                $("h2:contains('" + id + "')")[0].scrollIntoView(false);
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

        createGameBlock.append(createGameButton);

        gameListBlock.append(gameList).append(createGameBlock);

        gameBlock.append(gameListBlock).append(gameView);

        // Create ability to hide userList
        var hideUsersDiv = $("<div id='hideUsers'>").prependTo(userBlock)
        .append("<h1 id='hideUsersIcon' class='hideIcon'>></h1>");

        hideUsersDiv.click(function(e)
        {
            e.stopPropagation();

            var showing = !$("#userList").is(":hidden");

            $("#userList").toggle("slide", {
                direction : 'right'
            });

            userBlock.animate({
                width : (showing ? '17px' : '17.5%'),
                'min-width' : (showing ? '17px' : '200px')
            });

            gameView.animate({
                width : (showing ? '100%' : '65%')
            });

            gameView.css('max-width',
            ($(window).width() - (showing ? 217 : 400)) + "px");

            gameListBlock.animate({
                width : (showing ? '30%' : '17.5%')
            });

            $("#hideUsersIcon").html(showing ? '<' : '>');

        });

        gameAndChatBlock.append(gameBlock.append(userBlock)).append(chatView);

        container.append(gameAndChatBlock);

        $("#userList").append(
        $("<thead><tr><th class='header'>Users Online</th>")).append(
        $("<tbody>"));

        createGameButton.click(createGame);

        // FIX WINDOW RESIZING BUG
        $(window).resize(
        function()
        {
            gameView.css('max-width', ($(window).width() - ($("#userList").is(
            ":hidden") ? 217 : 400))
            + "px");
        });

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