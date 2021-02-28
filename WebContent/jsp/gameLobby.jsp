<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="css/GameLobbyStyleFlex.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/HeaderTitle.js"></script>
<script type="text/javascript" src="js/GameLobbyWidget.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>Resistance Lobby</title>
</head>
<body>
	<c:url value="/j_spring_security_logout" var="logoutUrl" />
	<div id="header" class="group">
		<div id="header-inner" class="group">

			<div id="logo">
			<a href="profile">
				<img alt="resist" src="images/PSMfist.jpg">
			</a>
			</div>
			<h2 id="title">Resistance Lobby</h2>
			<span id="options"> <a href="${logoutUrl}">Logout</a>
			</span>
		</div>
	</div>
	<div style="padding: 30px; margin-top: 45px; height: 93%;">
		<div id="gameLobby"></div>
	</div>
</body>
</html>
