// Wrap code with module pattern.
var SignupWidget = function()
{
	var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
	global.makeSignupWidget = function( parentElement )
	{
        ////////////////////////
        /////    Fields    /////
        ////////////////////////

		var container = parentElement;


        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////


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
            update : function()
            {
                
            },
            log : function( message )
            {
                
            }
        };
	};
}();

$(document).ready(function()
{
});