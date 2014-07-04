// Wrap code with module pattern.
var ResultsWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeResultsWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function retrieveResults()
        {
            // SEND REQUEST TO SERVER
            $.post("results", null, "json")
            .done( function (response) {
                if ( typeof response == "string" )
                {
                    window.location.reload( false );
                }
                $("#approves").html( response.approves );
                $("#denies").html( response.denies );
            });
        };
        
        /**
         * This private function will call to the server to reset the vote
         * counter
         */
        function reset()
        {
            // SEND REQUEST TO SERVER
            $.post( "reset" );
        };

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        $("#resetButton").click( reset );
        
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
                return setInterval( function() {retrieveResults()}, 500 );
            },
            log : function( message )
            {
                
            }
        };
    };
}();

$(document).ready(function()
{
            resultsWidget = makeResultsWidget($("body"));
            resultsWidget.refresh();
});