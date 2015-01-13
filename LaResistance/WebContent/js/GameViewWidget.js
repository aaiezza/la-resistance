// Wrap code with module pattern
var GameViewWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameViewWidget = function(parentElement, gameDisplayed,
    gameLinkFocusSetter, stompClient)
    {
        ///////////////////
        // /// Fields /////
        ///////////////////

        var activeGame = gameDisplayed;

        var container = parentElement;

        var me = $.parseJSON($("#p_user").html());

        me.admin = false;

        var gameStarted = false;

        $(me.authorities).each(function()
        {
            if (this.authority == "ROLE_ADMIN")
            {
                me.admin = true;
                return false;
            }
        });

        var sub3 = stompClient.subscribe("/app/game/" + activeGame.gameID,
        updateGame);

        var sub1 = stompClient.subscribe("/topic/game/" + activeGame.gameID,
        updateGame);

        var sub2 = stompClient.subscribe("/user/topic/game/"
        + activeGame.gameID, updateGame);

        var subscriptions = [ sub1, sub2, sub3 ];

        var playerMenu = [ {
            name : 'Remove From Game',
            img : 'images/delete.png',
            title : 'remove user button',
            fun : function()
            {}
        } ];

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
            updateGame();
        }

        function updateGame(response)
        {
            if (response)
                activeGame = $.parseJSON(response.body);
            fillWidget();
        }

        function joinGame()
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post("joinGame/" + activeGame.gameID).done(alertErrors);
        }

        function unJoinGame(username)
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post(
            "unJoinGame/" + activeGame.gameID
            + (_.isString(username) ? "?username=" + username : "")).done(
            alertErrors);
        }

        function updateMaxPlayers(event)
        {
            if (event)
            {
                event.preventDefault();
            }
            $.post(
            "updateMaxPlayers/" + activeGame.gameID + "/" + $(this).val())
            .done(alertErrors);
        }

        function updateBotPlayers(event)
        {
            if (event)
            {
                event.preventDefault();
            }
            $.post(
            "updateBotPlayers/" + activeGame.gameID + "/" + $(this).val())
            .done(alertErrors);
        }

        function cancelGame()
        {
            $.post("cancelGame/" + activeGame.gameID).done(alertErrors);
            activeGame = undefined;
        }

        function startGame()
        {
            gameLinkFocusSetter(activeGame.gameID);
            $.post("startGame/" + activeGame.gameID).done(function(response)
            {
                alertErrors(response);
            });
        }

        function fillWidget()
        {
            container.empty();

            if (!activeGame)
                return;

            var hosting = activeGame.host.name == me.username;

            // General Information
            container.append(
            $("<h3>").append("HOST: ").append(activeGame.host.name)).append(
            $("<h3>").append("We have (").append(activeGame.players.length)
            .append(" of ").append(activeGame.maxPlayers).append(") members!"));

            if (activeGame.botCount > 0)
            {
                container.append($("<h3>").append(
                "There " + ((activeGame.botCount == 1) ? "is " : "are "))
                .append(activeGame.botCount).append(
                " artificial member"
                + ((activeGame.botCount == 1) ? "!" : "s!")));
            }

            // Display current players
            container.append($("<p id='currentPlayers'>").append(
            "Current Members: "));

            var length = activeGame.players.length - 1;
            $(activeGame.players).each(
            function(i)
            {
                var player = $("<span>" + this.name + "</span>").addClass(
                "player")
                .toggleClass("host", this.name == activeGame.host.name);
                $("#currentPlayers").append(player);
                if (i < length)
                {
                    $("#currentPlayers").append(", ");
                }
            });

            // If Hosting the game, you can adjust number of players
            if (hosting && activeGame.state == "AWAITING_PLAYERS")
            {
                var playerAdjuster = $("<select id='playerAdjuster'/>").on(
                "change", updateMaxPlayers);

                for (var n = 5; n <= 10; n++)
                {
                    playerAdjuster.append($("<option>" + n + "</option>").prop(
                    "selected", n == activeGame.maxPlayers));
                }

                var botAdjuster = $("<select id='botAdjuster'/>").on("change",
                updateBotPlayers);

                for (var n = 0; n <= activeGame.maxPlayers - 1; n++)
                {
                    botAdjuster.append($("<option>" + n + "</option>").prop(
                    "selected", n == activeGame.botCount));
                }

                container
                .append($(
                "<div style='border:2px solid black;padding:5px;display:inline-block;margin-bottom:11px;'>")
                .append(
                "<label for='playerAdjuster'>Change number of Players</label><br/>")
                .append(playerAdjuster)
                .append(
                "<br/><label for='botAdjuster'>Change number of bots</label><br/>")
                .append(botAdjuster));

                // If Hosting the game, you can remove a player from the game
                $(".player").not(".host").each(function()
                {
                    var menu = _.clone(playerMenu[0]);
                    var player = $(this).html();
                    menu.fun = function()
                    {
                        unJoinGame(player);
                    }
                    $(this).contextMenu([ menu ]);
                });
            }

            //If the game is over, you only want to cancel it
            if (activeGame.state == "GAME_OVER")
            {
                container.append("<h1>"
                + activeGame.updateMessage[activeGame.updateMessage.length - 1]
                + "</h1>");
                appendGamePortal();
                container.append("<br/><br/>").append(
                $(
                "<input id='cancel' type='button' value='Retire Resistance'/>")
                .click(cancelGame));
            } else if (activeGame.state == "AWAITING_PLAYERS")
            {

                // If Hosting the game, you can choose to start the game or cancel it
                // Otherwise, you can join or unjoin it
                if (hosting)
                {
                    container
                    .append($(
                    "<br><input id='start' type='button' value='Start Resistance'>")
                    .click(startGame));
                } else
                {
                    var alreadyJoined = false;
                    $(activeGame.players).each(function()
                    {
                        if (this.name == me.username)
                        {
                            alreadyJoined = true;
                            return false;
                        }
                    });

                    if (alreadyJoined)
                    {
                        container
                        .append($(
                        "<input id='unJoin' type='button' value='Leave Resistance'>")
                        .click(unJoinGame));
                    } else
                    {
                        if (activeGame.players.length != activeGame.maxPlayers)
                        {
                            container
                            .append($(
                            "<input id='join' type='button' value='Join Resistance'>")
                            .click(joinGame));
                        }
                    }
                }
            }
            // If the game is in progress, you are given the link to view it
            else
            {
                container.append("<h1>GAME IN PROGRESS</h1>");
                appendGamePortal();
            }

            if ((hosting || me.admin) && activeGame.state != "GAME_OVER")
            {
                container
                .append("<br/><br/>")
                .append(
                $("<input id='cancel' type='button' value='Cancel Resistance'>")
                .click(cancelGame));
            }
        }

        function appendGamePortal()
        {
            var gamePortal = $(
            "<a href='" + activeGame.monitorURL + "'>" + activeGame.gameID
            + "</a>").click(function(event)
            {
                event.preventDefault();
                window.open(activeGame.monitorURL, '_blank');
                window.focus();
            });
            container.append(gamePortal);
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////

        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            clear : function()
            {
                if (this.hasSubscriptions())
                    this.unsubscribe();
                container.empty();
            },
            clearIfGameCanceled : function()
            {
                if (!activeGame)
                    return;

                this.clear();
            },
            hasSubscriptions : function()
            {
                return subscriptions;
            },
            unsubscribe : function()
            {
                sub1.unsubscribe();
                sub2.unsubscribe();
                subscriptions = null;
                return;

                $(subscriptions).each(function()
                {
                    this.unsubscribe();
                });
            },
            isEmpty : function()
            {
                return container.empty();
            }
        };
    };
}();