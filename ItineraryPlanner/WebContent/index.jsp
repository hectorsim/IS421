<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Itinerary Planner</title>
<link href="css/jquery-ui.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet" />
</head>
<body>
	<div class="title-container">
		<div>
			<img class="logo" src="images/logo.png" />
		</div>
		<div class="title">
			IS421: Enterprise Analysis for Decision Support <br /> Itinerary
			Planner
		</div>
	</div>
	<div class="header"></div>

	<form method="post" action="itineraryplanning.do"
		enctype="multipart/form-data">

		<div class="content-container">
			<div id="parameters">
				<h1>Parameters</h1>
				<label for="budget">Fixed Budget : $</input> <input id="budget"
					type="text" value="" /> <br />
				<br /> <label for="budget">Number of days trip : </input> <input
						id="budget" type="text" value="" />
			</div>

			<div id="location-contain">
				<h1>Starting Location</h1>
				<select id="startLocation" name="startLocation"
					class="custom-select">
					<option value="">Select a country</option>
					<option value="Singapore" selected>Singapore</option>
					<option value="Malaysia">Malaysia</option>
					<option value="Hong Kong">Hong Kong</option>
					<option value="Indonesia">Indonesia</option>
					<option value="Vietnam">Vietnam</option>
				</select> <br />
				<br />
				<br />
				<h1>Destinations</h1>
				<table id="locations">
					<col width=30%>
					<col width=70%>
					<thead>
						<tr>
							<th>Name of country</th>
							<th colspan=2>Satisfaction Value</th>
						</tr>
					<tbody>
					</tbody>
					</thead>
				</table>
				<button type="button" id="create-location">Add Location</button>
			</div>

		</div>
		<div class="button-container">
			<input type="submit" class="generateButton" id="generate-button"
				value="Generate" />
		</div>
	</form>
	<!-- Dialog Box -->
	<div id="dialog-Add" class="dialog-container"
		title="Create a new location">
		<p class="validateTips">Please select a country.</p>

		<form id="locationForm">
			<fieldset>
				<label id="lblLocationName" for="locationName">Name of
					Country : <label> <select name="locationName"
						id="locationName" class="custom-select">
							<option value="">Select a country</option>
							<option value="Singapore">Singapore</option>
							<option value="Malaysia">Malaysia</option>
							<option value="Hong Kong">Hong Kong</option>
							<option value="Indonesia">Indonesia</option>
							<option value="Vietnam">Vietnam</option>
					</select>
						<p>
							<label for="satisfactionLevel" />Satisfaction Value :</label> <input
					type="text" id="satisfactionAmount" readonly />
					</p> <!-- Jquery Slider -->
					<div id="slider-range-max"></div>
			</fieldset>
		</form>
	</div>

</body>

<script src="js/jquery.js"></script>
<script src="js/jquery-ui.min.js"></script>
<script src="js/utility.js"></script>
</html>