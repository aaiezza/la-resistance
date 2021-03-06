<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/HeaderTitle.js"></script>
<script type="text/javascript" src="js/SignupWidget.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>La Sign up | La Resistance</title>
</head>
<body>
	<div id="header" class="group">
		<div id="header-inner" class="group">

			<div id="logo">
				<img alt="resist" src="images/PSMfist.jpg">
			</div>
			
			<h2 id="title">Enlist Today!</h2>
			<span id="options">
			</span>
		</div>
	</div>
	<div style="text-align: center; padding: 30px; border: 1px solid green; position: fixed; top: 10%; left: 10%;">
		<h1>Sign Up for The Resistance!</h1>
		<form:form method="post" modelAttribute="newUserForm" action="signup">
			<table>
				<tr>
					<td colspan="2" style="color: red">${message}</td>
				</tr>
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
							<input type="button" value="clear" onclick="javascript:$('input.userForm').attr( 'value', '' )"/>
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
