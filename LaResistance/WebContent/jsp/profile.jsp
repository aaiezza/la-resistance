<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta name="viewport" content="initial-scale = 1.0,maximum-scale = 1.0" />

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript"
    src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript"
    src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="js/HeaderWidget.js"></script>
<script type="text/javascript" src="js/ProfileWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>${username}'s Profile Page</title>

</head>
<body>
    <c:url value="gameLobby" var="gameLobbyUrl" />
    <c:url value="userManagement" var="userManagementUrl" />
    <c:url value="j_spring_security_logout" var="logoutUrl" />
    <c:url value="userDetails/${username}" var="userDetailsUrl" />

    <div id="header" class="group"></div>
    <div id="core">
        <h2>You are now logged in ${username}</h2>
        <h3 style="margin-bottom:40px;">
            <c:if test="${user}">
                <a href='${gameLobbyUrl}'>Game Lobby</a> |
            </c:if>
            <c:if test="${user}">
                <a href='${userDetailsUrl}'>Update your Info</a>
            </c:if>
        </h3>

        <form action="https://www.paypal.com/cgi-bin/webscr" method="post"
            target="_top">
            <input type="hidden" name="cmd" value="_s-xclick"> <input
                type="hidden" name="encrypted"
                value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
            <input type="image"
                src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif"
                border="0" name="submit"
                alt="PayPal - The safer, easier way to pay online!">
        </form>

    </div>

    <c:if test="${admin}">
        <a id="userManagementOption" href="${userManagementUrl}">Manage
            Users</a>
    </c:if>
    <c:if test="${user}">
        <a id="logoutOption" href="${logoutUrl}">Logout</a>
    </c:if>
</body>
</html>
