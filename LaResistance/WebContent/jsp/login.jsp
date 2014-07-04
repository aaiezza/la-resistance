<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript"
	src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/LoginWidget.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>La Login | La Resistance</title>
</head>
<body>
	<div
		style="text-align: center; padding: 30px; border: 1px solid green; position: fixed; top: 10%; left: 10%;">
		<h1>Login Here</h1>
		<form method="post" action="<c:url value='j_spring_security_check' />">
			<table>
				<tr>
					<td colspan="2" style="color: red">${message}</td>

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
					<td> </td>
					<td><div>
							<input type="submit" value="Login" />
							<input type="button" value="Sign Up" onclick="javascript:window.location='signup'"/>
				    </div></td>
				</tr>
			</table>

		</form>
	</div>
</body>
</html>
