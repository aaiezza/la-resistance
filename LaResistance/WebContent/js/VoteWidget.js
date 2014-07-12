// Wrap code with module pattern
var VoteWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeVoteWidget = function(parentElement)
    {
        //////////////////
        ///// Fields /////
        //////////////////

        var container = parentElement;

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function vote( _vote )
        {
            // SEND REQUEST TO SERVER
            $.ajax(
            {
                type : 'POST',
                headers :
                {
                    "vote" : _vote,
                },
                url : 'vote',
                success : disableVoting,
                cache : false
            });
        };
        
        function disableVoting( response )
        {
            $( ".vote_button" ).hide();
            $( container ).append( "<p>" + response.response );
        };

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        $("#approve").click(function()
        {
            vote( true );
        });

        $("#deny").click(function()
        {
            vote( false );
        });

        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        
        return {
            getRootEl : function()
            {
                return container;
            },
            vote : function( _vote )
            {
                vote( _vote );
            },
            disableVoting : disableVoting,
            log : function( message )
            {
                
            }
        };
    };
}();

$(document).ready(function()
{
    voteWidget = makeVoteWidget($("#voter"));
});