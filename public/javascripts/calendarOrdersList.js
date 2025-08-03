$(document).ready(function() {
	$("select").change(function(s) {
	    var name = s.target.name;
	    var form = s.target.parentElement.parentElement.parentElement;
	    console.log(form);
	    form.submit();
	});
});
