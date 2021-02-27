<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>

<link href="css/VoterStyle.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<script type="text/javascript" src="js/VoteWidget.js"></script>

<link rel="icon" type="image/ico" href="images/favicon.ico">

<title>Voter</title>

</head>
<body>
	<div id="voter"
		style="margin: 0; min-height: 100%; min-width: 100%; background-color: black;">
		<table>
			<tr>
				<td>
					<div id="approve" class="vote_button"
						style="background-color: green;">
						<p>APPROVE</p>
					</div>
				</td>
				<td>
					<div id="deny" class="vote_button"
						style="left: 50%; background-color: red;">
						<p>DENY</p>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
