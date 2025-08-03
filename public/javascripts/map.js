var infowindow;
var map;
var markers = [];
var markerCount = 0;
var closeTimeout;

var addMarker = function (data) {
    console.log(data);
	var marker = new google.maps.Marker({'position': new google.maps.LatLng(data.lat, data.lng)});
	google.maps.event.addListener(marker, 'mouseover', function() { showInfoWindow(marker, data.photoId, data.lat, data.lng); } );
	google.maps.event.addListener(marker, 'click', function() { showInfoWindow(marker, data.photoId, data.lat, data.lng); } );
	google.maps.event.addListener(marker, 'mouseout', function() { closeTimeout = setTimeout(hideInfoWindow, 300000); } );
	markers[markerCount] = marker;
	markerCount++;
};

var showInfoWindow = function ( marker, pictureId, lat, lng ) {
	clearTimeout( closeTimeout );
	if ( infowindow ) {
		infowindow.close();
	}
	var moreEncoded = $('<div/>').text(txtMore).html();
	var contentHtml = "<div id=\"maptooltipid\" class=\"maptooltip\"><a href=\""+pictureId+"\" target=\"_blank\"><img src=\"/photos/small/"+pictureId+".jpg\" /></a><br /><br /><a href=\"search?lat="+lat+"&lng="+lng+"\" target=\"_blank\">"+moreEncoded+"...</a></div>";
	infowindow = new google.maps.InfoWindow({ content: contentHtml });
	infowindow.open( map, marker );
};

var hideInfoWindow = function () {
	clearTimeout( closeTimeout );
	if ( infowindow ) {
		infowindow.close();
	}
};

$(document).ready(function() {
    var mapOptions = {
        gestureHandling: 'greedy'
    };
    var zoom = parseInt(window.localStorage.getItem('map-zoom'));
    if (isNaN(zoom)) {
        zoom = 3;
    }
    mapOptions.zoom = zoom;
    var lat = parseFloat(window.localStorage.getItem('map-lat'));
    var lng = parseFloat(window.localStorage.getItem('map-lng'));
    if (isNaN(lat) || isNaN(lng)) {
        lat = 47;
        lng = 8;
    }
    mapOptions.center = new google.maps.LatLng(lat, lng);

    map = new google.maps.Map(document.getElementById('mapCanvas'), mapOptions);
    google.maps.event.addListener(map, "tilesloaded", () => {
        window.localStorage.setItem('map-zoom', map.getZoom());
        window.localStorage.setItem('map-lat', map.getCenter().lat());
        window.localStorage.setItem('map-lng', map.getCenter().lng());
    });
    markerData.map(addMarker);
    var mcOptions = { gridSize: 50, maxZoom: 10 };
    var mc = new MarkerClusterer(map, markers, mcOptions);
});