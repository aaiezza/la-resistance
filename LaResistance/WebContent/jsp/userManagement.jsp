<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:url value="/" var="imagePath" />

<html>
<head>

<link href="${imagePath}css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="${imagePath}css/UserManagementStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="${imagePath}js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/UserManagementWidget.js"></script>

<link rel="icon" type="${imagePath}image/ico" href="images/favicon.ico">

<title>User Management</title>

</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl" />

	<div id="header" class="group linkProfile"></div>
	<div id="core"></div>

	<a id="logoutOption" href="${imagePath}${logoutUrl}">Logout</a>
</body>
</html>
