$(document).ready(function() {
	$("select").change(function(s) {
	    target = $(s.target);
	    window.location.href = target.data("base-url") + "/" + target.val();
	});
});
