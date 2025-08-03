$(document).ready(function() {
	var locationSelect = $("select[name=location]");
	$("select[name=country]").change(function() {
	    if($(this).val()) {
	        $.get("/search/_locations?" + $.param({country: $(this).val()}), function(locations) {
                locationSelect.find("option").remove();
                locationSelect.removeAttr("disabled");
                locationSelect.append($("<option>").attr("value", "").text(""));
                locations.forEach(function(loc) {
                    locationSelect.append($("<option>").attr("value", loc["numId"]).text(loc["name"]));
                });
            });
	    } else {
            locationSelect.find("option").remove();
            locationSelect.append($("<option>").attr("value", "").text(locationSelect.data("choose-country")));
            locationSelect.attr("disabled", true);
        }
	});

    $("#modifySearch").click(function() {
	    $("#breadcrumbs").hide();
		$("#searchForm").show();
		return false;
	});

	var keywordsInput = function() {
		var tokens = [];
		$("span.token-selectable.include").each(function(i, obj){
			tokens.push($(obj).text());
		});
		$("span.token-selectable.exclude").each(function(i, obj){
			tokens.push("-" + $(obj).text());
		});
		$("input[name='keywords']").val(tokens.join(","));
	};
	keywordsInput();
	$("span.token-selectable").click(function(event) {
		var target = $(event.target);
		if (target.hasClass("exclude")) {
			target.removeClass("exclude");
		} else if (target.hasClass("include")) {
			target.removeClass("include");
			target.addClass("exclude");
		} else {
			target.addClass("include");
		}
		keywordsInput();
	});
});
