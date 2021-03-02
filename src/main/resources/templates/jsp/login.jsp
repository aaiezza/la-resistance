<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/" var="imagePath" />

<html>
<head>

<meta name="viewport" content="initial-scale = 1.00,maximum-scale = 1.00"/>

<link type="text/css" href="${imagePath}css/ResistanceStyle.css" rel="stylesheet"/>

<script type="text/javascript" src="${imagePath}js/lib/jquery-latest.min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/underscore-min.js"></script>

<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/LoginWidget.js"></script>

<link type="image/ico" href="${imagePath}images/favicon.ico" rel="icon"/>

<title>La Login | La Resistance</title>

</head>
<body>
    <div id="header" class="group"></div>
    <div id="core">
        <form method="post" action="<c:url value='j_spring_security_check' />" style="display: inline-block;">
            <table>
                <tr>
                    <c:if test="${not empty message}">
                        <td colspan="2"
                            style="<c:if test='${not success}'>color: red;</c:if><c:if test='${success}'>background-color: #bce8f1;</c:if>">${message}</td>
                    </c:if>
                </tr>
                <tr>
                    <td>User Name:</td>
                    <td><input type="text" name="username" /></td>
                </tr>
                <tr>
                    <td>Password:</td>
                    <td><input type="password" name="password" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><div>
                            <input type="submit" value="Login" /> <input type="button"
                                value="Sign Up" onclick="javascript:window.location='signup'" />
                        </div></td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>
