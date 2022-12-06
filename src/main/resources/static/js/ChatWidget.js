// Wrap code with module pattern
var ChatWidget = function()
{
    var global = this;

    // ///////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeChatWidget = function(parentElement, stompClient)
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;

        emoji.img_path = "https://raw.githubusercontent.com/github/gemoji/master/images/emoji/unicode/";

        var chatLog = $("<table id='chatLog'>");

        var chatLogBlock = $("<div id='chatLogBlock'>");

        var chatInput = $("<input id='chat' type='text'>");

        var chatButton = $("<input id='chatSubmit' type='button' value='say'>");

        var KEY = {
            ENTER : 13
        };

        var stickyScroll = 0;

        stompClient.subscribe('/app/chat', updateChat);

        stompClient.subscribe('/user/topic/chatSpecial', updateSpecialChat);

        var subscriptionToChat;

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function updateChat(response)
        {
            $($.parseJSON(response.body)).each(
            function()
            {
                var saidLine = $("<tr><td class='said'>"
                + emoji.replace_unified(this) + "</td></tr>");
                chatLog.append(saidLine);
            });
            scrollDown();

            /*
             * This is down here because we don't want to subscribe to this
             * channel until after the system chat message is sent saying this
             * user has entered the lobby
             */
            if (!subscriptionToChat)
                subscriptionToChat = stompClient.subscribe('/topic/chat',
                updateChat);
        }

        function updateSpecialChat(response)
        {
            var messages = $.parseJSON(response.body).messages;
            if (messages.length <= 0)
            {
                chatLog.empty();
                return;
            }
            chatLog
            .append($("<tr><td style='padding: 1px 1px 1px 15px;'>~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~</td></tr>"));
            $(messages).each(
            function(i, message)
            {
                chatLog.append($("<tr><td style='padding: 1px 1px 1px 30px;'>"
                + emoji.replace_unified(message) + "</td></tr>"));
            });
            chatLog
            .append($("<tr><td style='padding: 1px 1px 1px 15px;'>~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~</td></tr>"));

            stickyScroll = 0;
            scrollDown();
        }

        function sayItSocket(sayIt)
        {
            sayIt = sayIt.trim();
            $("#chat").val("");

            if (!sayIt)
            {
                return;
            }
            stompClient.send("/app/say", {}, sayIt)
        }

        function scrollDown()
        {
            if (chatLogBlock.scrollTop() >= stickyScroll)
            {
                chatLogBlock.scrollTop(chatLogBlock.prop("scrollHeight"));
                stickyScroll = chatLogBlock.scrollTop();
            }
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        container.append(chatLogBlock.append(chatLog)).append(chatInput)
        .append(chatButton);

        chatButton.click(function()
        {
            sayItSocket($("#chat").val());
        });

        chatInput.keypress(function(event)
        {
            if (event.which == KEY.ENTER)
            {
                event.preventDefault();
                chatButton.click();
            }
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

            },
            log : function(message)
            {
                return stompClient;
            }
        };
    };
}();