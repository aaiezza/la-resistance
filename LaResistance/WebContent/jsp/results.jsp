<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/HeaderWidget.js"></script>
<script type="text/javascript" src="js/ResultsWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>Vote Results</title>
</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl" />

    <div id="header" class="group linkProfile"></div>
	<div id="core">
		<h2>
			APPROVES: <span id="approves">${approves}</span>
		</h2>
		<h2>
			DENIES: <span id="denies">${denies}</span>
		</h2>
		<input id="resetButton" type="button" value="reset" />
	</div>

	<a id="logoutOption" href="${logoutUrl}">Logout</a>
</body>
</html>
