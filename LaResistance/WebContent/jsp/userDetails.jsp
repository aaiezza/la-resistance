<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<c:url value="../" var="imagePath" />

<html>
<head>

<link href="${imagePath}css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<link href="${imagePath}css/UserDetailsStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="${imagePath}js/HeaderWidget.js"></script>
<script type="text/javascript" src="${imagePath}js/UserDetailsWidget.js"></script>

<link rel="icon" type="image/ico" href="${imagePath}images/favicon.ico">

<title>${user}'s Details</title>

</head>
<body>
	<c:url value="userManagement" var="userManagementUrl" />
	<c:url value="j_spring_security_logout" var="logoutUrl" />

	<div id="header" class="group linkProfile"></div>
	<div id="core">
	<form:form method="POST" modelAttribute="newUserForm" action="${imagePath}updateUser" >
		<div id="user_tableBlock">
			<table id="detailsTable">
				<tbody>
					<tr id="usernameRow" class="permanent">
						<td><form:label for="username" path="username">Username</form:label></td>
						<td><form:input type="text" id="username" path="username"/></td>
					</tr>
					<tr id="firstNameRow">
						<td><form:label for="first_name" path="first_name">First Name</form:label></td>
						<td><form:input type="text" id="first_name" path="first_name"/></td>
					</tr>
					<tr id="lastNameRow">
						<td><form:label for="last_name" path="last_name">Last Name</form:label></td>
						<td><form:input type="text" id="last_name" path="last_name"/></td>
					</tr>
					<tr id="emailRow">
						<td><form:label for="email" path="email">Email</form:label></td>
						<td><form:input type="email" id="email" path="email"/></td>
					</tr>
					<tr id="authoritiesRow" class="<c:if test='${not admin}'>permanent</c:if>">
						<td><label for="authorities">Authorities</label></td>
						<td>
							<table id="authorities">
								<c:forEach items="${availableAuthorities}" var="auth">
					                <tr>
					                    <td><input type="checkbox" name="auths" value="${auth}" id="authorities" auth="${auth}" class="authBox" /></td>
					                    <td><label for="${auth}">${auth}</label></td>
				                    </tr>
								</c:forEach>
							</table>
						</td>
					</tr>
					<tr id="enabledRow" class="<c:if test='${not admin}'>permanent</c:if>">
						<td><form:label for="enabled" path="enabled">Enabled</form:label></td>
						<td><form:checkbox id="enabled" path="enabled"/></td>
					</tr>
					<tr id="passwordRow">
						<td><form:label for="password" path="password">Password</form:label></td>
						<td><form:input type="password" id="password" path="password"/></td>
					</tr>
					<tr id="confirmPasswordRow" style="display: none;">
						<td><form:label for="confirmPassword" path="confirmPassword">Confirm Password</form:label></td>
						<td><form:input type="password" id="confirmPassword" path="confirmPassword"/></td>
					</tr>
					<c:if test="${not empty message}">
					<tr>
						<td colspan="2" style="color: red">${message}</td>
					</tr>
					</c:if>
					<tr id="buttonsRow" class="permanent">
						<td><input type="button" id="changePasswordButton"
							value="Change Password"></td>
						<td><input type="submit" id="updateFieldsButton"
							value="Update With New Info"></td>
					</tr>
				</tbody>
			</table>
		</div>
		</form:form>
	</div>

	<c:if test="${admin}">
		<a id="userManagementOption" href="${imagePath}${userManagementUrl}">Manage
			Users</a>
	</c:if>
	<a id="logoutOption" href="${imagePath}${logoutUrl}">Logout</a>
	<p id="user" style="display: none;">${user}</p>
</body>
</html>
