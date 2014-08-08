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

        $(me.authorities).each(function()
        {
            if (this.authority == "ROLE_ADMIN")
            {
                me.admin = true;
                return false;
            }
        });

        var subscriptions = [];

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

            // If Hosting the game, you can adjust number of players
            if (hosting)
            {
                var playerAdjuster = $("<select id='playerAdjuster'/>").on(
                "change", updateMaxPlayers);
                for (var n = 5; n <= 10; n++)
                {
                    playerAdjuster.append($("<option>" + n + "</option>").prop(
                    "selected", n == activeGame.maxPlayers));
                }
                container
                .append($(
                "<div style='border:2px solid black;padding:5px;display:inline-block;margin-bottom:11px;'>")
                .append(
                "<label for='playerAdjuster'>Change number of Players</label><br/>")
                .append(playerAdjuster));
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

            // If Hosting the game, you can remove a player from the game
            if (hosting)
            {
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
                container
                .append("<h1>" + activeGame.updateMessage + "</h1>")
                .append(
                $("<input id='cancel' type='button' value='Retire Resistance'>")
                .click(cancelGame));
            }
            // If the game is in progress, you are given the link to view it
            else if (activeGame.state != "AWAITING_PLAYERS")
            {
                container.append("<h1>GAME IN PROGRESS</h1>").append(
                "<a href='" + activeGame.monitorURL + "'>" + activeGame.gameID
                + "</a>");
            } else
            {

                // If Hosting the game, you can choose to start the game or cancel it
                // Otherwise, you can join or unjoin it
                if (activeGame.host.name == me.username)
                {
                    container
                    .append($(
                    "<input id='start' type='button' value='Start Resistance'>")
                    .click(startGame));
                    container
                    .append($(
                    "<input id='cancel' type='button' value='Cancel Resistance'>")
                    .click(cancelGame));
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

                    // If you are an admin, you can cancel a game for a player
                    if (me.admin)
                    {
                        container
                        .append($(
                        "<input id='cancel' type='button' value='Cancel Resistance'>")
                        .click(cancelGame));
                    }
                }
            }
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        subscriptions = [
            stompClient.subscribe("/queue/game/" + activeGame.gameID,
            updateGame),

            stompClient.subscribe("/user/queue/game/" + activeGame.gameID,
            updateGame),

            stompClient.subscribe("/app/game/" + activeGame.gameID), ];

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
                this.unsubscribe();
                container.empty();
            },
            clearIfGameCanceled : function()
            {
                if (!activeGame)
                    return;

                this.clear();
            },
            unsubscribe : function()
            {
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