<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/HeaderTitle.js"></script>
<script type="text/javascript" src="js/LoginWidget.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<title>La Login | La Resistance</title>
</head>
<body>
	<div id="header" class="group">
		<div id="header-inner" class="group">

			<div id="logo">
				<img alt="resist" src="images/PSMfist.jpg">
			</div>
			<h2 id="title">Are you ready to join the Resistance?</h2>
			<span id="options">
			</span>
		</div>
	</div>
	<div style="text-align: center; padding: 30px; border: 1px solid green; position: fixed; top: 10%; left: 10%;">
		<form method="post" action="<c:url value='j_spring_security_check' />">
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
					<td> </td>
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
