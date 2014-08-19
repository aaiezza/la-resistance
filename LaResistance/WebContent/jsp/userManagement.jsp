<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:url value="/" var="imagePath"/>

<html>
<head>

<link type="text/css" href="${imagePath}css/ResistanceStyle.css" rel="stylesheet"/>
<link type="text/css" href="${imagePath}css/UserManagementStyle.css" rel="stylesheet"/>

<script type="text/javascript" src="${imagePath}js/lib/jquery-latest.min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/underscore-min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/jquery.tablesorter.js"></script>

<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/UserManagementWidget.js"></script>

<link type="image/ico" href="${imagePath}images/favicon.ico" rel="icon"/>

<title>User Management</title>

</head>
<body>
    <c:url value="j_spring_security_logout" var="logoutUrl" />

    <div id="header" class="group linkProfile"></div>
    <div id="core"></div>

    <a id="logoutOption" href="${imagePath}${logoutUrl}">Logout</a>
</body>
</html>
