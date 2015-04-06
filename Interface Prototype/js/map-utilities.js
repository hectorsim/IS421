// create a map in the "map" div, set the view to a given place and zoom
var map = L.map('map').setView([1.3, 103.8], 5);

// add an OpenStreetMap tile layer
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Data retrieved from server
var nodes = {
	"locations":["Changi Airport", 
		"Kuala Lumpur International Airport", 
		"Ho Chi Minh City Airport", 
		"Hong Kong International Airport", 
		"Jakarta Airport"], 
	"path":["Changi Airport", 
		"Kuala Lumpur International Airport", 
		"Ho Chi Minh City Airport", 
		"Hong Kong International Airport", 
		"Jakarta Airport", "Changi Airport"]};
var polyline = L.polyline([], {color: 'red'}).addTo(map);

initializeGraph(nodes, function(markers, path) {
	for (var index in path) {
		polyline.addLatLng(markers[path[index]]).bindPopup("<b>$200</b>");
	}
});

function initializeGraph(nodes, callback) {
	var count = 1;
	var markers = {};
	
	for (var index in nodes.locations) {
		var nodeName = nodes.locations[index];
		
		addLocationMarker(nodeName, function(location, marker) {
			markers[location] = marker.getLatLng();
			
			if (count == nodes.locations.length) {
				callback(markers, nodes.path);
			} else {
				count = count + 1;
			}
		});	
	}
};

function addLocationMarker(location, callback) {
	$.getJSON('http://nominatim.openstreetmap.org/search?format=json&limit=5&q=' + location, function(data) {
		var items = [];
		
		$.each(data, function(key, val) {
			var marker = L.marker([ val.lat ,  val.lon ]).addTo(map);
			marker.bindPopup("<b>" + val.display_name + "</b>");
			callback(location, marker);
			return false;
		});
	});
}