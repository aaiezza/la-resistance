// Wrap code with module pattern
var ChatWidget = function()
{
    var global = this;

    // ///////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeChatWidget = function( parentElement, stompClient )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var chatLog = $( "<textarea id='chatLog' readonly>" );
        
        var chatInput = $( "<input id='chat' type='text'>" );
        
        var chatButton = $( "<input id='chatSubmit' type='button' value='say'>" );
        
        var KEY = {
          ENTER: 13      
        };
        
        var stickyScroll = 0;
        
        stompClient.subscribe('/queue/chat', updateChat );
        
        stompClient.subscribe('/app/chat');

        stompClient.subscribe('/user/queue/chatSpecial', updateAdminChat );
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function updateChat( response ) {
            $($.parseJSON(response.body)).each( function() {
                chatLog.append(this).append("\n");
            });
            scrollDown();                              
        }
        
        function updateAdminChat(response) {
            var messages = $.parseJSON( response.body ).messages;
            if (messages.length <= 0)
            {
                messages = [ "No Messages!" ];
            }
            chatLog.append("\t~ ~ ~\n");
            $(messages).each(function(i, message)
            {
                chatLog.append("  ").append(message).append("\n");
            });
            chatLog.append("\t~ ~ ~\n");
        
            stickyScroll = 0;
            scrollDown();                              
        }
        
        function sayItSocket( sayIt )
        {
            sayIt = sayIt.trim();
            $("#chat").val("");

            if (!sayIt)
            {
                return;
            }
            stompClient.send( "/app/say", {}, JSON.stringify( sayIt ) )
        }
        
        function scrollDown()
        {
            if ( chatLog.scrollTop() >= stickyScroll )
            {
                chatLog.scrollTop( chatLog.prop( "scrollHeight" ) );
                stickyScroll = chatLog.scrollTop();
            }
        }
        
        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        container.append( chatLog ).append( chatInput ).append( chatButton );
                
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