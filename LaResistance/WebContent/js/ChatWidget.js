// Wrap code with module pattern
var ChatWidget = function()
{
    var global = this;

    // ///////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeChatWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var chatLog = $( "<textarea id='chatLog' disabled>" );
        
        var chatInput = $( "<input id='chat' type='text'>" );
        
        var chatButton = $( "<input id='chatSubmit' type='button' value='say'>" );
        
        var KEY = {
          ENTER: 13      
        };
        
        var stickyScroll = 0;
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function sayIt( say )
        {
            say = say.trim();
            $("#chat").val("");

            if (!say)
            {
                return;
            }
            
            $.ajax({
                url: "sayIt",
                type: "POST",
                dataType: "json",
                headers: { "say": say }
            }).done(function(response)
            {
                if (Object.keys(response).length > 0)
                {
                    if (Object.keys(response.messages).length <= 0)
                    {
                        response.messages = [ "No Messages!" ];
                    }
                    chatLog.append("\t~ ~ ~\n");
                    $(response.messages).each(function(i, message)
                    {
                        chatLog.append("  ").append(message).append("\n");
                    });
                    chatLog.append("\t~ ~ ~\n");
                }
                
                stickyScroll = 0;
                scrollDown();
            });
        }

        function updateChat()
        {
            $.get("updateChat").done( function(log)
            {
                $(log).each( function() {
                    chatLog.append(this).append("\n");
                });
                scrollDown();
                
            }).fail(function()
            {
                location.reload();
            }).always( updateChat );
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
            sayIt($("#chat").val());
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
                updateChat();
            },
            log : function(message)
            {
            }
        };
    };
}();