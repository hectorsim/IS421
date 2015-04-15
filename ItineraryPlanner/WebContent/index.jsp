<%@ page import="com.ItineraryPlanner.Constants,com.entity.Graph,com.entity.Vertex,java.util.ArrayList;" language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Itinerary Planner</title>
<link href="css/jquery-ui.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet" />
<%
	Graph map = Constants.GRAPH;
	ArrayList<Vertex> locationList = map.getListOfVertex();
%>
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

	<form method="post" action="itineraryplanning.do">

		<div class="content-container">
			<div id="parameters">
				<h1>Parameters</h1>
				<label for="budget">Fixed Budget : $</label>
				<input id="budget" name="budget" type="text" value="" />
				<br/><br/>
				<label for="noOfDays">Number of days trip : </label>
				<input id="noOfDays" name="noOfDays" type="text" value="" />
			</div>

			<div id="location-contain">
				<h1>Starting Location</h1>
				<select id="startLocation" name="startLocation"
					class="custom-select">
					<option value="">Select a country</option>
					<%
						for(Vertex i : locationList) {
					%>
						<option value="<%=i.getId()%>"><%=i.getLocationName() %></option>
					<%
						}
					%>
				</select> <br />
				<br />
				<br />
				<h1><input id="isDestionation" type="checkbox" name="isDestination" /><label for="isDestionation">Select Specific Destinations</label></h1>
				<div id="addLocation" style="display:none;">
					<h1>Destinations</h1>
					<table name="locations" id="locations">
						<col width=50%>
						<col width=50%>
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
							<%
								for(Vertex i : locationList) {
							%>
								<option value="<%=i.getId()%>"><%=i.getLocationName() %></option>
							<%
								}
							%>
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