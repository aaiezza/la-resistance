// Wrap code with module pattern.
var PlayWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makePlayWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function assignPlayers()
        {
            // SEND REQUEST TO SERVER
            $.post("play", null, "json").done(function(response)
            {
                if (typeof response == "string")
                {
                    window.location.reload(false);
                }
                $("#role").html(response.role);
            });
        };

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
            refresh : function()
            {
            },
            log : function(message)
            {

            }
        };
    };
}();

$(document).ready(function()
{
    gamePlayWidget = makePlayWidget($("#gameLobby"));
});