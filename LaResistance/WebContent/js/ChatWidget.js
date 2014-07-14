// Wrap code with module pattern
var ChatWidget = function()
{
    var global = this;
    
    function currentTimeMillis()
    {
        var serverTime = 0;
        $.ajax(
        {
            async : false,
            url : "serverTime"
        }).done(function(time)
        {
            serverTime = time;
        });

        return parseInt(serverTime);
    };

    // ///////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeChatWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var chatUpdated = currentTimeMillis();
        
        var chatLog = $( "<textarea id='chatLog' disabled>" );
        
        var chatInput = $( "<input id='chat' type='text'>" );
        
        var KEY = {
          ENTER: 13      
        };
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function sayIt( say )
        {
            $("#chat").val("");
            
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
                    scrollDown();
                }
            });
        }

        function updateChat()
        {
            $.get("updateChat?lastUpdate=" + chatUpdated).done(function(log)
            {
                if (log == "")
                {
                    console.log("UH OH!!!");
                } else
                {
                    chatLog.append(log);
                }

                chatUpdated = currentTimeMillis();
                scrollDown();
                updateChat();
            }).fail(function()
            {
                // NEED TO USE <code> tag instead of <textarea>
                //chatLog.append($("<span style='color:red;'>\nSERVER DOWN\n"));
                chatLog.append( "\n\nSERVER DOWN\n" )
            });
        }
        
        function scrollDown()
        {
            chatLog.scrollTop( chatLog.prop( "scrollHeight" ) );
        }
        
        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        container.append( chatLog ).append( chatInput );
        
        chatInput.keypress( function ( event ) {
            if ( event.which == KEY.ENTER ) {
                event.preventDefault();
                sayIt($("#chat").val());
             }
        } );
        
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
                //setTimeout( function(){updateChat()}, 500 );
            },
            log : function(message)
            {
            }
        };
    };
}();