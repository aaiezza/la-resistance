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

        var activeGame;

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

        // TODO use the menu productively
        var playerMenu = [ {
            name : 'Remove From Game',
            img : 'images/delete.png',
            title : 'remove user button',
            fun : function()
            {}
        } ];

        var lobbySock = new SockJS("http://" + location.host
            + ":8081/LaResistance/lobbyUpdate", null, {
            protocols_whitelist : [ "websocket" ],
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
            fillWidget();
        }

        function fillWidget()
        {
            container.empty();

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

                stompClient.subscribe("/app/game/" + QueryString.gameID), ];
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
    gameMonitorWidget = makeGameMonitorWidget($("#core"));
});