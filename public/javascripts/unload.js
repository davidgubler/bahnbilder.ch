var formSubmitting = false;
window.addEventListener("beforeunload", function (e) {
	if (formSubmitting) {
		return undefined;
	}
	var confirmationMessage = 'If you leave before saving, your changes will be lost.';
	(e || window.event).returnValue = confirmationMessage; //Gecko + IE
	return confirmationMessage; //Gecko + Webkit, Safari, Chrome etc.
});

$(document).ready(function() {
	$("button[type=submit]").click(function() {
		formSubmitting = true;
	});
});
