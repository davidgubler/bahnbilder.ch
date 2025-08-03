$(document).ready(function() {
	$(".editable").click(function(e) {
		if (e.originalEvent.shiftKey) {
			var elem = $(this);
			if(elem.hasClass("selected")){
				$(this).removeClass("selected");
			} else {
				$(this).addClass("selected");
			}
			if ($(".editable.selected").length > 0) {
				$("div.multiedit").show();
			} else {
				$("div.multiedit").hide();
			}
			return false;
		}
	});
	$("#multiedit").click(function() {
		var ids = [];
		$(".editable.selected").map(function() {
			var htmlId = $(this).attr("id");
			ids.push(htmlId.split('-')[1]);
		});
		if (ids.length < 1) {
			return false;
		}
		window.location.href="/"+ids.join(",") + "/_edit?returnUrl=" + encodeURIComponent(window.location.pathname + window.location.search);
		return false;
	});
});
