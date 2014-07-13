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
        
        var userTable = $( "<table id='userTable'><thead><tr id='userTableHeader'>" );

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function retrieveUsers()
        {
            $( "#userTable tr:not(:first-child)" ).remove();
            
            $.post( "retrieveUsers" ).done( function( users ) {
                $( users ).each( function ( i, user ) {
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

        function deleteUsers()
        {
            
        }
        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        headerWidget.addOption( logoutOption );
        
        container.append( $( "<div>" ).append( userTable ).attr( "id", "user_tableBlock" ) );
        
        $( "#userTableHeader" )
        .append( $("<th class='header'>Username</th>" ) )
        .append( $("<th class='header'>Enabled</th>" ) )
        .append( $("<th class='header'>First Name</th>" ) )
        .append( $("<th class='header'>Last Name</th>" ) )
        .append( $("<th class='header'>Email</th>" ) )
        .append( $("<th class='header'>Roles</th>" ) );
        
        $( "#userTable" ).append( $("<tbody>") );
        
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
    $("#userTable").tablesorter(
    {
        sortList : [ [ 0, 0 ] ]/*,
        textExtraction : function(node)
        {
            return node.childNodes[0].innerHTML;
        }*/
    });

});