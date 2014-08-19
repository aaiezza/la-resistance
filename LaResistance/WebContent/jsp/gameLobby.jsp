<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale = 0.750, maximum-scale = 0.750" />

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="css/GameLobbyStyle.css" rel="stylesheet" type="text/css">
<link href="css/GameViewStyle.css" rel="stylesheet" type="text/css">
<link href="css/ChatViewStyle.css" rel="stylesheet" type="text/css">
<link href="css/contextMenu.css" rel="stylesheet" type="text/css">
<link href="css/emoji.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="http://cdn.sockjs.org/sockjs-0.3.js"></script>
<script type="text/javascript" src="js/stomp.js"></script>
<script type="text/javascript" src="js/contextMenu.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="js/emoji.js"></script>

<script type="text/javascript" src="js/HeaderWidget.js"></script>
<script type="text/javascript" src="js/GameLobbyWidget.js"></script>
<script type="text/javascript" src="js/GameViewWidget.js"></script>
<script type="text/javascript" src="js/ChatWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>Resistance Lobby</title>

</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl" />

    <div id="header" class="group linkProfile"></div>
    <div id="core"></div>

    <a id="logoutOption" href="${logoutUrl}">Logout</a>
    <p id="p_user" style="display:none">${user}</p>
</body>
</html>
