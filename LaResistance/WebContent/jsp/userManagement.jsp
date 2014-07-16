<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="css/UserManagementStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="js/HeaderWidget.js"></script>
<script type="text/javascript" src="js/UserManagementWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>User Management</title>

</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl" />

	<div id="header" class="group linkProfile"></div>
	<div id="core"></div>

	<a id="logoutOption" href="${logoutUrl}">Logout</a>
</body>
</html>
