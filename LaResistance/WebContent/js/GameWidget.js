// Wrap code with module pattern
var GameWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeGameWidget = function(parentElement)
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

        var gameInfoBlock = $("<div id='gameInfoBlock'>");

        var subscriptions = [];

        var playerMenu = [];

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

        function updateGame(response)
        {
            activeGame = $.parseJSON(response.body);

            var playerPanel = "";

            var p = 0;
            $(activeGame.players).each(
            function()
            {
                if (this.name == me.username)
                {
                    activeGame.me = this;
                }
                playerPanel += "<span class='" + this.role + "'>(" + this.name
                + ":" + this.role + ")</span>";

                p++;

                if (p < activeGame.maxPlayers)
                {
                    playerPanel += "<br/>";
                }
            });

            playerPanel += "";

            playerMenu = [ {
                name : playerPanel,
                title : 'Players',
                fun : function()
                {}
            } ];

            fillWidget();
        }

        function fillWidget()
        {
            container.children().not("#role").remove();

            if (firstVisit)
            {
                //ROLE REVEALER
                container
                .append($("<div id='role'>Click to Reveal Role!</div>").click(
                function()
                {
                    $(this).toggleClass("revealed");
                    if ($(this).is(".revealed"))
                    {
                        $(this).html(activeGame.me.role);
                        $(this).fadeOut(5000);
                        // SEND OFF a stomp message to let game know we know the role
                        stompClient.send("/app/learnedRole");
                    } else
                    {
                        // If I ever decide this should be kept here
                    }
                }));
                firstVisit = false;
            }

            container.append($("<br/>"));

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

            function teamVoter(infoTable, prepend)
            {
                var youVoted = $("<td colspan='2'><h4>You Voted</h4></td>");

                var approveButton = $("<td id='approve' style='width:50%;'>")
                .append($("<h4>APPROVE</h4>")).click(function()
                {
                    stompClient.send("/app/teamVote", {}, true);
                    $(this).parent().empty().append(youVoted);
                });
                var rejectButton = $("<td id='reject' style='width:50%;'>")
                .append($("<h4>REJECT</h4>")).click(function()
                {
                    stompClient.send("/app/teamVote", {}, false);
                    $(this).parent().empty().append(youVoted);
                });

                var voterRow = $("<tr>").append(
                $("<td colspan='2' style='padding:0;border:0;'>").append(
                $("<table style='border-collapse:collapse;width:100%;'>")
                .append($("<tr>").append(approveButton).append(rejectButton))));

                if (prepend)
                {
                    infoTable.prepend(voterRow);
                } else
                {
                    infoTable.append(voterRow);
                }
            }

            function missionVoter(infoTable, prepend)
            {
                var youVoted = $("<td colspan='2'><h4>You Went on the Mission</h4></td>");

                var approveButton = $("<td id='approve' style='width:50%;'>")
                .append($("<h4>DO YOUR DUTY</h4>")).click(function()
                {
                    stompClient.send("/app/missionVote", {}, true);
                    $(this).parent().empty().append(youVoted);
                });
                var rejectButton = $("<td id='reject' style='width:50%;'>")
                .append($("<h4>SABOTAGE THE MISSION</h4>")).click(function()
                {
                    stompClient.send("/app/missionVote", {}, false);
                    $(this).parent().empty().append(youVoted);
                });

                var voterRow = $("<tr>").append(
                $("<td colspan='2' style='padding:0;border:0;'>").append(
                $("<table style='border-collapse:collapse;width:100%;'>")
                .append($("<tr>").append(approveButton).append(rejectButton))));

                if (prepend)
                {
                    infoTable.prepend(voterRow);
                } else
                {
                    infoTable.append(voterRow);
                }
            }

            function update(infoTable, prepend)
            {
                if (activeGame.updateMessage.length <= 0)
                {
                    return;
                }

                var m = 0;
                var message = $("<tr><td colspan='2' class='messageUpdates'></td></tr>");

                if (prepend)
                    infoTable.prepend(message);
                else
                    infoTable.append(message);

                $(activeGame.updateMessage).each(
                function()
                {
                    m++;

                    if (!_.isEmpty(this))
                        message.children("td").prepend("<p>" + this + "</p>");

                    if (m == activeGame.updateMessage.length)
                    {
                        message.children("td:first-child").children(
                        ":first-child").css("border-bottom",
                        "thick solid black");
                    }
                });
            }

            switch (activeGame.state)
            {
                case "PLAYERS_LEARNING_ROLES":
                    // You can only know you're role right now
                    // until everyone has seen it

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

                    // If you are the leader, you gots a team to choose!
                    if (me.username == activeGame.currentLeader)
                    {
                        $("#userCell").addClass("leader");
                        infoTable
                        .append($("<tr id='leaderChoose'>")
                        .append(
                        $("<td class='leader' colspan='2'>Choose Your Team!<br/>("
                        + activeGame.teamSizeRequirement
                        + ((activeGame.missions[activeGame.currentMissionNumber - 1].MinimumFails > 1) ? "*"
                        : "") + ")</td>")));

                        function setTeamUp()
                        {
                            if ($(".teammate").length >= activeGame.teamSizeRequirement)
                            {
                                $(".member:not(.teammate)")
                                .addClass("disabled");
                                $("#leaderChoose td").html(
                                "Send Team On Mission!");
                                $("#leaderChoose").click(
                                function()
                                {
                                    $.get("missionTeamSubmitted").done(
                                    alertErrors);
                                });
                                return false;
                            } else
                            {
                                $(".member.disabled").removeClass("disabled");
                                $("#leaderChoose").click(function()
                                {});
                                return true;
                            }
                        }
                        ;

                        $(activeGame.players)
                        .each(
                        function()
                        {
                            infoTable
                            .append($("<tr class='member'>")
                            .append($("<td colspan='2'>" + this.name + "</td>"))
                            .click(
                            function()
                            {
                                var addingTeammate = activeGame.team.length < activeGame.teamSizeRequirement;
                                var dismissingTeammate = $(this)
                                .is(".teammate");
                                if (addingTeammate || dismissingTeammate)
                                {
                                    if (dismissingTeammate)
                                    {
                                        stompClient.send(
                                        "/app/dismissTeammate", {}, $(this)
                                        .children(":first-child").html());
                                    } else
                                    {
                                        stompClient.send("/app/addTeammate",
                                        {}, $(this).children(":first-child")
                                        .html());
                                    }
                                }
                            }));
                        });
                        $(".member td").each(function()
                        {
                            if (_.contains(activeGame.team, $(this).html()))
                            {
                                $(this).parent().addClass("teammate");
                            }
                            return setTeamUp();
                        });
                    }

                    teamPickUpdates(infoTable, true);

                    break;
                case "RESISTANCE_VOTES_ON_TEAM":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(false);

                    update(infoTable, true);

                    teamVoter(infoTable, true);

                    teamPickUpdates(infoTable, true);

                    break;
                case "TEAM_VOTES_ON_MISSION":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(false);

                    update(infoTable, true);

                    if (_.contains(activeGame.team, me.username))
                    {
                        missionVoter(infoTable, true);
                    }

                    break;
                case "GAME_OVER":
                    gameInfoBlock.empty();

                    //Game Info
                    var infoTable = displayGameInfo(true);

                    update(infoTable, true);

                    $("#role").remove();

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
            var userCell = $("<th id='userCell' colspan='2'>" + me.username
            + "</th>");

            var infoTable = $("<table>").append(
            $("<thead>").append($("<tr>").append(userCell)));

            infoTable
            .append(
            $("<tr>").append(
            $("<td id='successfulMissions'>Successful Missions:</td>")).append(
            $("<td>" + activeGame.successfulMissions + "</td>")))
            .append(
            $("<tr>")
            .append($("<td id='failedMissions'>Failed Missions:</td>")).append(
            $("<td>" + activeGame.failedMissions + "</td>")))
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
                userCell.contextMenu(playerMenu);
            } else if (activeGame.me.role == "SPY")
            {
                userCell.contextMenu(playerMenu);
            }
            if (!gameover)
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
                stompClient.subscribe("/queue/game/" + QueryString.gameID,
                updateGame),

                stompClient.subscribe("/user/queue/game/" + QueryString.gameID,
                updateGame),

                stompClient.subscribe("/app/game/" + QueryString.gameID) ];
        });

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
    gameWidget = makeGameWidget($("#core"));
});