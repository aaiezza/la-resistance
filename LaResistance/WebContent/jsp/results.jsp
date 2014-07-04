<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link href="css/ResistanceStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript"
	src="http://epeli.github.io/underscore.string/dist/underscore.string.min.js"></script>
<link rel="icon" type="image/ico" href="images/favicon.ico">
<script type="text/javascript" src="js/ResultsWidget.js"></script>
<script type="text/javascript">

</script>

<title>Vote Results</title>
</head>
<body>
	<h2>APPROVES: <span id="approves">${approves}</span></h2>
	<h2>DENIES: <span id="denies">${denies}</span></h2>
	<input id="resetButton" type="button" value="reset"/>
</body>
</html>
