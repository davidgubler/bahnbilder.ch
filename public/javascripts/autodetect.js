$(document).ready(function() {
	$('span.solution').click(function() {
		var span = $(this);
		var photoId = span.data('photoid');
		var solutionIds = span.data('solutionids');
		var selected = !span.hasClass('solution-selected');
		$('.solution-'+photoId).removeClass('solution-selected');
		var input = $('input[name='+photoId+']');
		if (selected) {
			span.addClass('solution-selected');
			input.val(solutionIds);
		} else {
		    input.val('');
		}
	});

	$('small.unhide').click(function() {
		var span = $(this);
		var photoId = span.data('photoid');
		$('div#hidden-'+photoId).show();
		span.hide();
	});
});
