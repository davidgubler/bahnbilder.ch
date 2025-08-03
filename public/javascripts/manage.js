var editmap;
var marker;

var extractMainLocation = function(location) {
	if (!location) {
		return false;
	}
	if (location.indexOf("-") > 0) {
		location = location.substring(0, location.indexOf("-"));
	}
	if (location.indexOf(",") > 0) {
		location = location.substring(0, location.indexOf(","));
	}
	return location;
};


var positionMap = function(location, country) {
	var zoom = 5;
	var addr = false;
	if (location && country) {
		addr = extractMainLocation(location) + ", " + country;
		zoom = 14;
	} else if (location) {
		addr = extractMainLocation(location);
		zoom = 12;
	} else if (country) {
		addr = extractMainLocation(country);
		zoom = 5;
	}
	if (addr) {
		geocoder.geocode({
			'address' : addr
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			editmap.panTo(results[0].geometry.location);
			editmap.setZoom(zoom);
		}
		});
	} else {
	var myLatlng = new google.maps.LatLng(47, 8);
		editmap.setZoom(6);
		editmap.panTo(myLatlng);
	}
};


var updateMap = function() {
	var latitude = $('#lat').val();
	var longitude = $('#lng').val();
	if (latitude && longitude) {
		return;
	}
	var location = $("select[name=location]").val() > 0 ? $("select[name=location] option:selected").text() : false;
	var country = $("select[name=country]").val() > 0 ? $("select[name=country] option:selected").text() : false;
	positionMap(location, country);
};

$(document).ready(function() {
	// initialize map
	geocoder = new google.maps.Geocoder();
	var mapOptions = {
		mapTypeId : google.maps.MapTypeId.HYBRID,
	};
	editmap = new google.maps.Map(document.getElementById('editmap'), mapOptions);
	google.maps.event.addListener(editmap, 'click', function(event) {
		if (marker) {
			marker.setPosition(event.latLng);
		} else {
			marker = new google.maps.Marker({
				position : event.latLng,
				map : editmap,
				draggable : true,
			});
		}
		$('#lat').val(event.latLng.lat());
		$('#lng').val(event.latLng.lng())
	});

	// set marker to correct starting position
	var latitude = $('#lat').val();
	var longitude = $('#lng').val();
	if (latitude && longitude) {
		var markerPos = new google.maps.LatLng(latitude, longitude);
		marker = new google.maps.Marker({
			position : markerPos,
			map : editmap,
			draggable : true,
		});
		editmap.panTo(markerPos);
		editmap.setZoom(18);
	} else {
		updateMap();
	}

	// respond to changes of contry and location
	$("select[name=location]").change(updateMap);
	$("select[name=country]").change(updateMap);

	// fetch location suggestions
	$.get(window.location.href.split('?')[0] + "/_suggest_locations", function(data) {
		if (data.length == 0) {
			return false;
		}
		var target = $("<small/>").attr("style", "text-align: right; display: block; margin-top: -7px;");
		$("label[for=newLocation]").append(target);
		target.append(txtSuggestions + ":<br/>");
		for (var i = 0; i < data.length; ++i) {
			var suggestion = data[i];
			var content;
			if (suggestion.numId) {
				content = $("<a/>").attr("href", "javascript:void(0)").attr("data-id", suggestion.numId).text(suggestion.name).click(function(){
					$("select[name=location]").val($(this).data("id"));
				});
			} else {
				content = suggestion.name;
			}
			target.append(content);
			target.append($("<br/>"));
		}
		return false;
	});
	
	// fetch train suggestions
	$.get(window.location.href.split('?')[0] + "/_suggest_trains", function(data) {
		if (data.length == 0) {
			return false;
		}
		var target = $("<small/>").attr("style", "display: block;");
		target.append(txtSuggestions + ":<br />");
		for (var i = 0; i < data.length; ++i) {
			content = $("<a/>").attr("href", "javascript:void(0)").text(data[i]).click(function(){
				$("textarea[name=description]").val($(this).text());
			});
			target.append(content);
			target.append("<br />");
		}
		$("textarea[name=description]").after(target);
		return false;
	});
});
