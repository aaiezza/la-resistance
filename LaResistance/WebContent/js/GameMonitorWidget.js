// Wrap code with module pattern
var GameMonitorWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameMonitorWidget = function(parentElement)
    {
        ///////////////////
        // /// Fields /////
        ///////////////////
        var QueryString = function()
        {
            // This function is anonymous, is executed immediately and
            // the return value is assigned to QueryString!
            var query_string = {};
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i = 0; i < vars.length; i++)
            {
                var pair = vars[i].split("=");
                // If first entry with this name
                if (typeof query_string[pair[0]] === "undefined")
                {
                    query_string[pair[0]] = pair[1];
                    // If second entry with this name
                } else if (typeof query_string[pair[0]] === "string")
                {
                    var arr = [ query_string[pair[0]], pair[1] ];
                    query_string[pair[0]] = arr;
                    // If third or later entry with this name
                } else
                {
                    query_string[pair[0]].push(pair[1]);
                }
            }
            return query_string;
        }();

        var container = parentElement;

        var activeGame;

        var firstVisit = true;

        var gameInfoBlock = $("<div id='gameInfoBlock'>");

        var subscriptions = [];

        var lobbySock = new SockJS("http://" + location.host
        + ":8081/resist/stompshake", null, {
            /* protocols_whitelist : [ "websocket" ], */
            debug : true
        });
        var stompClient = Stomp.over(lobbySock);

        window.onbeforeunload = function()
        {
            stompClient.close();
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

        function updateGame(response)
        {
            activeGame = $.parseJSON(response.body);

            if (firstVisit)
            {
                activeGame = $.parseJSON(activeGame);
                firstVisit = false;
            }

            if (!$("#board"))
            {
                container.prepend($("<img id='board' src='images/boards/"
                + activeGame.players.length + "_player (1024x756).jpg'>"));
            }

            var successes = 0;

            var failures = 0;

            $(activeGame.missions).each(function()
            {
                if (this.successful == undefined)
                {
                    return false;
                }

                if (this.successful)
                {
                    successes++;
                } else
                {
                    failures++;
                }
            });

            activeGame.successes = successes;
            activeGame.failures = failures;

            fillWidget();
        }

        function fillWidget()
        {
            container.empty();

            container.append("<br/>");

            function teamPickUpdates(infoTable, prepend)
            {
                // TEAM PICK UPDATES!
                var teamRow = $("<tr>").append(
                $("<td><h3>" + activeGame.currentLeader
                + "</h3>Has Elected the Following Team:</td>")).append(
                $("<td>").append("<table id='leadersTeam'>"));

                if (prepend)
                {
                    infoTable.prepend(teamRow);
                } else
                {
                    infoTable.append(teamRow);
                }

                $(activeGame.team).each(
                function()
                {
                    $("#leadersTeam").append(
                    $("<tr>").append("<td>" + this + "</td>"));
                });

                if (activeGame.team.length == activeGame.teamSizeRequirement)
                {
                    $("#leadersTeam").addClass("teamFull");
                }
            }

            function update(infoTable, prepend)
            {
                if (_.isEmpty(activeGame.updateMessage))
                {
                    return;
                }

                if (prepend)
                {
                    infoTable.prepend(activeGame.updateMessage);
                } else
                {
                    infoTable.append(activeGame.updateMessage);
                }
            }

            switch (activeGame.state.name)
            {
                case "PLAYERS_LEARNING_ROLES":

                    // TODO MORE GENERIC WAITING_ON... FUNCTION?

                    var waitingOn = $("<h2>Waiting on </h2>");
                    var p = false;
                    $(activeGame.players).each(function()
                    {
                        if (!this.roleLearned && !_.isEmpty(this.name))
                        {
                            if (p)
                                waitingOn.append(", ");
                            waitingOn.append(this.name);
                            p = true;
                        }
                    });
                    container.append(waitingOn);
                    return;
                case "LEADER_CHOOSING_TEAM":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(false);

                    // If we came from another mission earlier, this will give the update
                    update(infoTable, true);

                    teamPickUpdates(infoTable, true);

                    break;
                case "RESISTANCE_VOTES_ON_TEAM":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(false);

                    teamPickUpdates(infoTable, true);

                    break;
                case "TEAM_VOTES_ON_MISSION":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(false);

                    update(infoTable, true);

                    break;
                case "GAME_OVER":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(true);

                    update(infoTable, true);

                    gameInfoBlock.before($(
                    "<div id='role'>Return To Lobby</div><br/>").click(
                    function()
                    {
                        window.close();
                    }));

                default:
                    console.log("DEFUALT GAME STATE?!");
                    break;
            }
        }

        function displayGameInfo(gameover)
        {
            var infoTable = $("<table>").append($("<thead>").append($("<tr>")));

            infoTable
            .append(
            $("<tr>").append(
            $("<td id='successfulMissions'>Successful Missions:</td>")).append(
            $("<td>" + activeGame.successes + "</td>")))
            .append(
            $("<tr>")
            .append($("<td id='failedMissions'>Failed Missions:</td>")).append(
            $("<td>" + activeGame.failures + "</td>")))
            .append(
            $("<tr>")
            .append(
            $("<td><span style='font-size:small;'>Consecutive Failures to Assemble a Team:</span></td>"))
            .append($("<td>" + activeGame.teamVoteTracker + "</td>")));

            container.append(gameInfoBlock.append(infoTable));

            if (gameover)
            {
                $(activeGame.players).each(
                function()
                {
                    infoTable.append($("<tr class='" + this.role + "'>")
                    .append($("<td>" + this.name + "</td>")).append(
                    $("<td>" + this.role + "</td>")));
                });
            } else
            {
                infoTable.append($("<tr>")
                .append($("<td>Current Leader:</td>")).append(
                $("<td>" + activeGame.currentLeader + "</td>")));
            }

            return infoTable;
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        stompClient.connect({}, function(frame)
        {
            console.log('Connected: ' + frame);

            subscriptions = [
                stompClient.subscribe("/topic/game/" + QueryString.gameID,
                updateGame),

                stompClient.subscribe("/app/gameMonitor/" + QueryString.gameID,
                updateGame) ];
        });

        $("body").css("overflow", "hidden");

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
            },
            getActiveGame : function()
            {
                return activeGame;
            }
        };
    };
}();
$(document).ready(function()
{
    gameMonitorWidget = makeGameMonitorWidget($("#core"));
});