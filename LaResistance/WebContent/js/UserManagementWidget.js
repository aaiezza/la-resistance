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
        
        var deleteUsersButton = $( "<input id='deletedUsers' type='button' value='Delete Selected Users'>" );
        
        var updateUserButton = $( "<input id='updateUser' type='button' value='Update Selected User'>" );

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function retrieveUsers()
        {
            $( "#userTable tr:not(:first-child)" ).remove();

            $.ajax(
            {
                async : false,
                url : "retrieveUsers",
                type : 'POST'
            }).done( function( users ) {
                $( users ).each( function( i, user ) {
                    authorities = $( "<td id='authorities'>" );
                    
                    $( user.authorities ).each( function( i, authority ){ 
                        authorities.append( $( "<p>" ).html( authority.authority ) );
                    } );
                    
                    $( "#userTable" )
                    .append( $("<tr class='userRow' user='" + user.username + "'>")
                    .append( $("<td>")
                        .append( $( "<span>" + user.username + "</span>" )
                            .prepend( $( "<input class='selectUser' user='" + user.username + "' type='checkbox'>" ) ) ) )
                    .append( $("<td><span>" + user.enabled + "</span></td>" ) )
                    .append( $("<td><span>" + user.first_name + "</span></td>" ) )
                    .append( $("<td><span>" + user.last_name + "</span></td>" ) )
                    .append( $("<td><span>" + user.email + "</span></td>" ) )
                    .append( authorities ) );
                });
            });
            updateClickabilityOfButtons();
        };

        function deleteUsers()
        {
            if ( confirm( "Are you sure you want to delete these users?" ) )
            {
                $.each( _.map( $( ".selectUser:checked" ), function( checkbox ) {
                    var user = $( checkbox ).attr( "user" );
                    return $( ".userRow[user=" + user + "]" );
                }), function( i, user ) {
                    if ( $( user ).children( "#authorities:contains(ROLE_ADMIN)" ).length )
                    {
                        alert( "You can't delete an admin!" );
                    } else
                    {
                        $.ajax(
                        {
                            async : false,
                            url : "deleteUser/" + $(user).attr("user"),
                            type : "POST"
                        }).done(retrieveUsers);
                    }
                });
            }
        };
        
        function updateUser()
        {
            var user = $( ".selectUser:checked" ).attr( "user" );
            location = "userDetails/" + user;
        };
        
        function updateClickabilityOfButtons()
        {
            switch ( $( ".selectUser:checked" ).length )
            {
                case 0:
                    deleteUsersButton.prop( "disabled", true );
                    updateUserButton.prop( "disabled", true );
                    break;
                case 1: 
                    deleteUsersButton.prop( "disabled", false );
                    updateUserButton.prop( "disabled", false );
                    break;
                default:
                    deleteUsersButton.prop( "disabled", false );
                    updateUserButton.prop( "disabled", true );
            }
        };
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
        
        ////////////////////
        // USER SELECTION //
        ////////////////////
        $( ".selectUser" ).click( function( event )
        {
            event.stopPropagation();
            var user = $(this).attr( "user" );
            $( ".userRow[user=" + user + "]" ).toggleClass( "selected" );
            updateClickabilityOfButtons();
        });
        
        $( ".userRow" ).children().click( function()
        {
            var user = $(this).parent().attr( "user" );
            $( ".selectUser[user=" + user + "]" ).click();
        });
        
        ///////////////////////
        // USER MODIFICATION //
        ///////////////////////
        deleteUsersButton.click( deleteUsers );
        
        updateUserButton.click( updateUser );
        
        $( "#user_tableBlock" ).append( deleteUsersButton ).append( updateUserButton );
        
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
                retrieveUsers();
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
        sortList : [ [ 0, 0 ] ]
    });

});