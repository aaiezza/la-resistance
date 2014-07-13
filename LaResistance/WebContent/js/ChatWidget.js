// Wrap code with module pattern
var ChatWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeChatWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        var lastUpdate = Date.now();
        
        var lastUpdated = Date.now();
        
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
                headers: { "say": say }
            });
            updateChat();
        }

        function updateChat()
        {
            lastUpdated = Date.now();
            $.get( "updateChat?lastUpdate=" + lastUpdate ).done(function( log ) {
                if ( log == "" || lastUpdated < lastUpdate )
                {
                    return;
                }
                lastUpdate = Date.now();
                chatLog.append( log );
                chatLog.scrollTop = chatLog.scrollHeight;
                
                $(document).ajaxStop();
            });
            setTimeout( function(){updateChat()}, 200 );
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
                setTimeout( function(){updateChat()}, 500 );
            },
            log : function(message)
            {
            }
        };
    };
}();