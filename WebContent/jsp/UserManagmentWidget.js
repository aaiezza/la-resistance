// Wrap code with module pattern.
var UserManagementWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeUserManagementWidget = function( parentElement )
    {
        //////////////////
        ///// Fields /////
        //////////////////

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
    userManagementWidget = userManagementWidget($("#userManagement"));
});