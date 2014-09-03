<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:url value="/" var="imagePath" />

<html>
<head>

<meta name="viewport" content="width=device-width, initial-scale = 0.75, maximum-scale = 1.00"/>

<link type="text/css" href="${imagePath}css/ResistanceStyle.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/GameLobbyStyle.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/GameViewStyle.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/ChatViewStyle.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/lib/contextMenu.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/lib/emoji.css" rel="stylesheet"/>

<script type="text/javascript" src="${imagePath}js/lib/jquery-latest.min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/jquery-ui.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/underscore-min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/sockjs-0.3.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/stomp.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/contextMenu.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/jquery.tablesorter.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/emoji.js"></script>

<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/GameLobbyWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/GameViewWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/ChatWidget.js"></script>

<link type="image/ico" href="${imagePath}images/favicon.ico" rel="icon"/>

<title>Resistance Lobby</title>

</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl"/>

    <div id="header" class="group linkProfile"></div>
    <div id="core"></div>

    <a id="logoutOption" href="${logoutUrl}">Logout</a>
    <p id="p_user" style="display:none">${user}</p>
</body>
</html>
