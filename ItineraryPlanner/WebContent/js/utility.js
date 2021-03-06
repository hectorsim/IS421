$(function() {
	var dialog, form,
	location = $("#locationName"),
	amount = $("#satisfactionAmount"),
	tips = $(".validateTips");
	
	$( "#startLocation" ).selectmenu();
	$( "#locationName" ).selectmenu();
	$( "#processingOptions").buttonset();
	
	// Validating Fields
	function updateTips(t) {
		tips.text(t).addClass("ui-state-highlight").show();
		setTimeout(function(){
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	}
	
	// Validate Location
	function checkLocation() {
		if (location.val().length == 0) {
			location.parent().addClass("ui-state-error");
			updateTips("Please select a country for your itinerary");
			return false;
		} else {
			return true;
		}
	}
	
	function addLocation() {
		var valid = true;
		location.parent().removeClass("ui-state-error");
		
		valid = checkLocation();
		if (valid) {
			$("#locations tbody").append("<tr>" +
			"<td>" + "<input type='checkbox' name='locationList' value='" + location.val() + ":" + amount.val() + "' checked hidden>" + $("#locationName option:selected").text() + "</td>" +
			"<td>" + amount.val() + "</td>" +
			"<td></td>" +
			"</tr>");
			$("#locations tbody tr").hover(hoverCell, unhoverCell);
			dialog.dialog("close")
		} 
		
		return valid;
	}
	
	// Dialog box
	dialog = $("#dialog-Add").dialog({
		autoOpen: false,
		height: 350,
		width: 500,
		modal: true,
		buttons: {
			"Add Location": addLocation,
			Cancel: function() {
				dialog.dialog("close");
			}
		}, 
		close: function() {
			// Reset all value and cache
			form[0].reset();
			location.parent().removeClass("ui-state-error");
		}
	});
	
	form = dialog.find( "form" ).on( "submit", function( event ) {
		event.preventDefault();
		addLocation();
	});
	
	$( "#create-location" ).on( "click", function() {
		dialog.dialog( "open" );
		tips.hide();
		$(".holder").html($( "#locationName option:selected" ).text());
		amount.val($("#slider-range-max").slider("value"));
	});
	
	location.change(function() {
		tips.hide();
		location.parent().removeClass("ui-state-error");
	});
	
	// TR on hover
	$('#locations tbody tr').hover(hoverCell, unhoverCell);
	
	function hoverCell() {
		$(this).addClass('highlight');
		$(this)
		.find("td:last-child")
		.html('<img src="images/deleteimg.png" class="actionBtn" href="javascript:void(0);" onClick="deleterow(this)" />')
		.show()
		.width(10); 
	}
	
	function unhoverCell() {  
		$(this).removeClass('highlight');
		$(this).find("td:last-child").html("&nbsp;").hide(); 
	}

	deleterow = function (ele){
		$(ele).parent().parent().remove();
	}

	// Slider Jquery
	$("#slider-range-max").slider({
		range: "max",
		min: 0,
		max: 100,
		value: 100, 
		slide: function(event, ui) {
			amount.val(ui.value);
		}
	});
	
	// Checkbox for select destination
	$("#isDestionation").click(function() {
		if (!$(this).is(':checked')) {
			$("#addLocation").hide();
        } else {
        	$("#addLocation").show();
        }
        
	})
});