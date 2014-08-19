<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:url value="/" var="imagePath" />

<html>
<head>
<meta name="viewport" content="initial-scale = 1.00,maximum-scale = 1.00"/>

<link type="text/css" href="${imagePath}css/ResistanceStyle.css" rel="stylesheet"/>

<script type="text/javascript" src="${imagePath}js/lib/jquery-latest.min.js"></script>
<script type="text/javascript" src="${imagePath}js/lib/underscore-min.js"></script>

<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/SignupWidget.js"></script>

<link type="image/ico" href="${imagePath}images/favicon.ico" rel="icon"/>

<title>Enlist Today!</title>

</head>
<body>
    <div id="header" class="group"></div>
    <div id="core">
        <h1>Sign Up for The Resistance!</h1>
        <form:form method="POST" modelAttribute="newUserForm" action="signup" style="display: inline-block;">
            <table>
                <c:if test="${not empty message}">
                <tr>
                <td colspan="2" style="color: red;">${message}</td>
                <tr>
                </c:if>
                <tr>
                    <td><form:label path="first_name">First Name:</form:label></td>
                    <td><form:input class="userForm" type="text" path="first_name"
                            placeholder="Your first name" /></td>
                </tr>
                <tr>
                    <td><form:label path="last_name">Last Name:</form:label></td>
                    <td><form:input class="userForm" type="text" path="last_name"
                            placeholder="Your last name" /></td>
                </tr>
                <tr>
                    <td><form:label path="email">Email:</form:label></td>
                    <td><form:input class="userForm" type="email" path="email"
                            placeholder="Your email" /></td>
                </tr>
                <tr>
                    <td><form:label path="username">Username:</form:label></td>
                    <td><form:input class="userForm" type="text" path="username"
                            placeholder="username" /></td>
                </tr>
                <tr>
                    <td><form:label path="password">Password:</form:label></td>
                    <td><form:input class="userForm" type="password" path="password"
                            placeholder="password" /></td>
                </tr>
                <tr>
                    <td><form:label path="confirmPassword">Confirm Password:</form:label></td>
                    <td><form:input class="userForm" type="password" path="confirmPassword"
                            placeholder="same password" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><p>
                            <input type="submit" value="Sign Up" />
                            <input type="button" value="Clear" />
                        </p>
                        <p>
                            <input type="button" value="Already have a login?" onclick="javascript:window.location='login'" />
                        </p></td>
                </tr>
            </table>
        </form:form>
    </div>
</body>
</html>
