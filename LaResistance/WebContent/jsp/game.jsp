<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<meta name="viewport" content="initial-scale = 0.75,maximum-scale = 0.75" />

<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="css/GameStyle.css" rel="stylesheet" type="text/css">
<link href="css/contextMenu.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="http://cdn.sockjs.org/sockjs-0.3.js"></script>
<script type="text/javascript" src="js/stomp.js"></script>
<script type="text/javascript" src="js/contextMenu.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.js"></script>

<script type="text/javascript" src="js/HeaderWidget.js"></script>
<c:choose>
	<c:when test="${not empty user}">
		<script type="text/javascript" src="js/GameWidget.js"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="js/GameMonitorWidget.js"></script>
	</c:otherwise>
</c:choose>
<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>${game}</title>

</head>
<body>
	<div id="header" class="group linkProfile"></div>
	<div id="core"></div>

	<p id="p_user" style="display:none">${user}</p>
</body>
</html>
