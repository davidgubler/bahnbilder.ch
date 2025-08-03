$(document).ready(function() {
	$(".incomplete").click(function() {
		var elem = $(this);
		if(elem.hasClass("selected")){
			$(this).removeClass("selected");
		} else {
			$(this).addClass("selected");
		}
		return false;
	});

	$("#selectAll").click(function() {
		$(".incomplete").addClass("selected");
		return false;
	});

	$("#selectNone").click(function() {
		$(".incomplete").removeClass("selected");
		return false;
	});
	$("#modify").click(function() {
		var ids = [];
		$(".incomplete.selected").map(function() {
			var htmlId = $(this).attr("id");
			ids.push(htmlId.split('-')[1]);
		});
		if (ids.length < 1) {
			return false;
		}
		window.location.href="/"+ids.join(",") + "/_edit?returnUrl=" + encodeURIComponent(window.location.pathname + window.location.search);
		return false;
	});
	$("#autodetect").click(function() {
		var ids = [];
		$(".incomplete.selected").map(function() {
			var htmlId = $(this).attr("id");
			ids.push(htmlId.split('-')[1]);
		});
		if (ids.length < 1) {
			return false;
		}
		window.location.href="/"+ids.join(",") + "/_autodetect?returnUrl=" + encodeURIComponent(window.location.pathname + window.location.search);
		return false;
	});
	$("#delete").click(function() {
		var ids = [];
		$(".incomplete.selected").map(function() {
			var htmlId = $(this).attr("id");
			ids.push(htmlId.split('-')[1]);
		});
		if (ids.length < 1) {
			return false;
		}
		window.location.href="/"+ids.join(",") + "/_delete?returnUrl=" + encodeURIComponent(window.location.pathname + window.location.search);
		return false;
	});
});
