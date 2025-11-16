var player;
var posStart;
var epochStart;

var onPlayerStateChange = function(event) {
	if (event.data == YT.PlayerState.PLAYING && !posStart) {
		posStart = player.getCurrentTime() + 8;
		epochStart = Date.now()/1000;
		console.log("got player position");
	}
}

var onPlayerReady = function() {
	player.mute();
	player.playVideo();
}


var seekTo = function(epochTime) {
	player.seekTo(epochTime - epochStart + posStart, true);
}

var onYouTubePlayerAPIReady = function() {
	player = new YT.Player('ytplayer', {
		width: '100%',
		height: 'auto',
		videoId: 'y1GQYld14QE',
		allow: "autoplay",
		events: {
			'onStateChange': onPlayerStateChange,
			'onReady': onPlayerReady
          	}
	});
}



$(document).ready(function() {
	var scrollBlock = 0;
	var scrollPos = 0;
	var scrollAnimation = false;
	
	var url = "https://railinfo.bahnbilder.ch/api/ch/edgepos?";
	var params = "Zürich HB-Zürich Hardbrücke=0.4&Zürich HB-Zürich Altstetten=0.2&Zürich HB-Zürich Wiedikon=0.3";
	$.get(url + params, function(data) {
		if (data.length == 0) {
			return false;
		}
		for (i in data.passes) {
			var pass = data.passes[i];

			var productSpan = $("<span/>").addClass("product").addClass("product-"+pass.shortName.replace(/[0-9].*/, "").toLowerCase()).text(pass.shortName);

			var passText = $("<span/>").addClass("trainPass");
			passText.append(pass.localTime.substring(0, 5) + " ");
			passText.append((pass.forward ? ">" : "<") + " ");
			passText.append(productSpan);
			passText.append(" " + pass.tripShortName + " " + pass.tripBegins + " - " + pass.tripEnds);
			passText.data("epochTime", pass.epochTime);

			//.text(pass.localTime + " " + (pass.forward ? ">" : "<") + " " + pass.shortName + " " + pass.tripShortName + " " + pass.tripBegins + " - " + pass.tripEnds).data("epochTime", pass.epochTime);
			var timetableRow = $("<div/>").append(passText);
			passText.click(function(){
				var epochTime = $(this).data("epochTime");
				seekTo(epochTime);
			});
			$("#timetable").append(timetableRow);
		}
		return false;
	});


	var color = function color(factor) {
		var red, green, blue;
		red = 255;
		green = 25 + Math.round(230 * (1-factor));
		blue = 25 + Math.round(230 * (1-factor));
		redHex = ("00" + red.toString(16)).substr(-2);
		greenHex = ("00" + green.toString(16)).substr(-2);
		blueHex = ("00" + blue.toString(16)).substr(-2);
		return "#" + redHex + greenHex + blueHex;
	};


	var highlight = function() {
		if (!posStart || !epochStart) {
			return false;
		}
		var playerTime = player.getCurrentTime() + epochStart - posStart;
		var bestMatch = false;
		var bestMatchTime = 1000000000;
	        $(".trainPass").each(function() {
	            	var epochTime = $(this).data("epochTime");
			var timeDiff = epochTime - playerTime;
			if (Math.abs(timeDiff) < bestMatchTime) {
				bestMatchTime = Math.abs(timeDiff);
				bestMatch = $(this);
			}
	            	if (timeDiff > 0 && timeDiff < 120 ) {
	                	factor = 1 - timeDiff / 120;
	                	$(this).css("color", color(factor));
	        	} else if (timeDiff < 0 && timeDiff > -360) {
	                	factor = 1 + timeDiff / 360;
	                	$(this).css("color", color(factor));
			} else {
	                	$(this).css("color", "");
	        	}
		});
		if (bestMatch && scrollBlock < Date.now()) {
			var scrollTarget = $("#timetable").scrollTop() + bestMatch.position().top - $("#timetable").height()/2;
			if (scrollTarget != scrollPos) {
				$("#timetable").animate({
					scrollTop: scrollTarget
				}, 2000, "swing", function() { scrollAnimation = false; });
				scrollPos = scrollTarget;
			}
		}
	};

	$("#timetable").on('scroll', function() {
		// jQuery animation may cause these events, so ignore if scrolling animation is going on
		if (!scrollAnimation) {
			scrollBlock = Date.now() + 10000;
		}
	});
	$("#timetable").on('mousewheel DOMMouseScroll touchmove', function() {
		$("#timetable").stop();
		scrollBlock = Date.now() + 10000;
	});

	window.setInterval(highlight, 1000);
});
