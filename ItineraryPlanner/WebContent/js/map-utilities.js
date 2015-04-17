// create a map in the "map" div, set the view to a given place and zoom
var map = L.map('map').setView([1.3, 103.8], 5);

// add an OpenStreetMap tile layer
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Trigger for user interface set up
initializeGraph(function(markers, path) {
	var count = 1;
	// For every path in the graph
	for (var index in path) {
		if (count < path.length) {
			var currentLocation = path[index];
			var nextLocation = path[parseInt(index) + 1];
			
			// Connect current location to the next location
			var pointList = [markers[currentLocation.location], markers[nextLocation.location]];
			
			var flightPath = new L.Polyline(pointList, {
			    color: 'black',
			    weight: 3,
			    opacity: 0.5,
			    smoothFactor: 1
			}).bindPopup("<b>Flight Cost: </b>" + currentLocation.flightPrice);
			
			flightPath.addTo(map);
			count = count + 1;
		}
	}
});

// Initialization of network onto the map
function initializeGraph(callback) {
	$.get('itineraryplanning.do', function (results) {  
		var result_json = jQuery.parseJSON(results);
		console.log("Results Output : " + JSON.stringify(result_json));
		
		var error = result_json.Error;
		
		if (error == undefined) {
			var allLocations = result_json.allLocations;
			var nodes = result_json.path;
			var count = 1;
			var markers = {};
			
			for (var index in allLocations) {
				var nodeDetail = allLocations[index];
				
				addLocationMarker(nodeDetail, function(location, marker) {
					markers[location] = marker.getLatLng();
					
					if (count == allLocations.length) {
						callback(markers, nodes);
					} else {
						count = count + 1;
					}
				});
			}
		} else {
			alert("No Optimal Solution Found!");
		}
	});
};

// Add location marker on the map after google search
function addLocationMarker(locationDetail, callback) {
	var nodeName = locationDetail.location; 
	
	$.getJSON('http://nominatim.openstreetmap.org/search?format=json&limit=5&q=' + nodeName, function(data) {
		
		if (data.length == 0) {
			console.log(nodeName + " cannot be found");
		}
		$.each(data, function(key, val) {
			var marker = L.marker([ val.lat ,  val.lon ]).addTo(map);
			marker.bindPopup("<b>Location: </b>" + val.display_name + "<br/>" +
					"<b>Cost of Living: </b>" +  locationDetail.costOfLiving + "<br/>" +
					"<b>Number of stays: </b>" + locationDetail.noOfDaysStay);
			callback(nodeName, marker);
			return false;
		});
	});
}