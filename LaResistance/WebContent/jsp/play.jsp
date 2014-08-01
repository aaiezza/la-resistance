<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://underscorejs.org/underscore-min.js"></script>
<script type="text/javascript" src="js/PlayWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<style type="text/css">
body, div {
	margin: 0;
}

div {
	position: fixed;
}

.vote_button {
	margin: 4%;
	width: 42%;
	height: 92%;
	position: fixed;
	float: left;
	display: table-cell;
}

p {
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
	color: white;
	font-size: 400%;
	font-weight: bold;
	text-rendering: optimizeLegibility;
	margin: 10%;
}
</style>

<title>Role Assigner</title>

</head>
<body>
	<div id="voter"
		style="margin: 0; min-height: 100%; min-width: 100%; background-color: black;">
		<table>
			<tr>
				<td>
				<p>Your Role IS: <span id="role">${role}</span></p>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
