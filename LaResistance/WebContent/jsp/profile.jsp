<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript"
	src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>Profile Page | www.beingjavaguys.com</title>
</head>
<body>
	<div id="header" class="group">
		<div id="header-inner" class="group">

			<div id="logo">
				<img alt="resist" src="images/PSMfist.jpg">
			</div>
		</div>
	</div>
	<c:url value="/j_spring_security_logout" var="logoutUrl" />
	<c:url value="/results" var="resultsUrl" />
	<div
		style="text-align: center; padding: 30px; position: fixed; top: 10%; left: 10%;">

		<h2>Profile Page | You are now logged in ${username}</h2>
		<h3>
		    <a href="${resultsUrl}">Vote Results</a>
			<a href="${logoutUrl}">Logout</a>
		</h3>
	</div>
</body>
</html>
