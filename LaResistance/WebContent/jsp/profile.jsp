z<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="js/HeaderWidget.js"></script>
<script type="text/javascript" src="js/ProfileWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>${username}'s Profile Page</title>

</head>
<body>
	<c:url value="gameLobby" var="gameLobbyUrl" />
	<c:url value="results" var="resultsUrl" />
	<c:url value="userManagement" var="userManagementUrl" />
	<c:url value="j_spring_security_logout" var="logoutUrl" />
	<c:url value="userDetails/${username}" var="userDetailsUrl" />

	<div id="header" class="group"></div>
	<div id="core">
		<h2>You are now logged in ${username}</h2>
		<h3>
			<c:if test="${user}">
				<a href='${gameLobbyUrl}'>Game Lobby</a> |
			</c:if>
			<c:if test="${user}">
				<a href='${userDetailsUrl}'>Update your Info</a>
			</c:if>
			<c:if test="${admin}">
				| <a href="${resultsUrl}">Vote Results</a>
			</c:if>
		</h3>
	</div>

	<c:if test="${admin}">
		<a id="userManagementOption" href="${userManagementUrl}">Manage Users</a>
	</c:if>
	<c:if test="${user}">
		<a id="logoutOption" href="${logoutUrl}">Logout</a>
	</c:if>
</body>
</html>
