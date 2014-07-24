// Wrap code with module pattern
var ProfileWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeProfileWidget = function( parentElement )
    {
        ////////////////////////
        /////    Fields    /////
        ////////////////////////

        var container = parentElement;
        
        var manageUsersOption = $( "#userManagementOption" );
        
        var logoutOption = $( "#logoutOption" );
        
        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////


        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption( logoutOption );
        
        headerWidget.addOption( manageUsersOption );
        
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
    profileWidget = makeProfileWidget( $ ( "#core" ) );
});