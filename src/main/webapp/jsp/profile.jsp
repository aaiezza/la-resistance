<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/HeaderTitle.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>${username}'s Profile Page</title>
</head>
<body>
	<c:url value="/j_spring_security_logout" var="logoutUrl" />
	<c:url value="/results" var="resultsUrl" />
	<c:url value="/userManagement" var="userManagementUrl" />
	<c:url value="/gameLobby" var="gameLobbyUrl" />

	<div id="header" class="group">
		<div id="header-inner" class="group">

			<div id="logo">
				<img alt="resist" src="images/PSMfist.jpg">
			</div>
			<h2 id="title">Profile Page</h2>
			<span id="options">
				<c:if test="${admin}">
					<a href="${userManagementUrl}">Manage Users</a> |
				</c:if>
				<a href="${logoutUrl}">Logout</a>
			</span>
		</div>
	</div>
	<div style="padding: 30px; margin-top: 45px; height: 93%; text-align: center;">

		<h2>You are now logged in ${username}</h2>
		<h3>
			<c:if test="${user}">
				<a href="${gameLobbyUrl}">Game Lobby</a>
			</c:if>
			<c:if test="${admin}">
				<a href="${resultsUrl}">Vote Results</a>
			</c:if>
		</h3>
	</div>
</body>
</html>
