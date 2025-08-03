$(document).ready(function() {
	$("select").change(function(s) {
	    var name = s.target.name;
	    if (s.target.value == "other") {
	        $("span#" + name + "OtherWrapper").show();
	    } else {
	        $("span#" + name + "OtherWrapper").hide();
	    }
	});
});
