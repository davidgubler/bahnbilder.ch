$(document).ready(function() {
	$("select[name=period]").change(function() {
        window.location.href = "/stats/" + $("select[name=period]").val();
	});

	$("span.request-domain").click(function(e) {
	    $(e.target).parent().find("div.request-list").toggle();
	});
});