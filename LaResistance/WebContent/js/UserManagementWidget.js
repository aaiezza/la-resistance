// Wrap code with module pattern
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

        var logoutOption = $( "#logoutOption" );
        
        var userTable = $( "<table id='userTable'><tr id='userTableHeader'>" );

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function retrieveUsers()
        {
            $.post( "retrieveUsers" ).done( function( users ) {
                $( users ).each( function ( username, user ) {
                    $( "#userTable" )
                    .append( $("<tr>")
                    .append( $("<td><span>" + user.username + "</span></td>" ) )
                    .append( $("<td><span>" + user.enabled + "</span></td>" ) )
                    .append( $("<td><span>" + user.first_name + "</span></td>" ) )
                    .append( $("<td><span>" + user.last_name + "</span></td>" ) )
                    .append( $("<td><span>" + user.email + "</span></td>" ) )
                    .append( $("<td><span>" + user.roles + "</span></td>" ) ) );
                });
            });
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption( logoutOption );
        
        container.append( userTable );
        
        $( "#userTableHeader" )
        .append( $("<td><h3>Username</h3></td>" ) )
        .append( $("<td><h3>Enabled</h3></td>" ) )
        .append( $("<td><h3>First Name</h3></td>" ) )
        .append( $("<td><h3>Last Name</h3></td>" ) )
        .append( $("<td><h3>Email</h3></td>" ) )
        .append( $("<td><h3>Roles</h3></td>" ) );
        
        retrieveUsers();

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
    userManagementWidget = makeUserManagementWidget($("#core"));

});