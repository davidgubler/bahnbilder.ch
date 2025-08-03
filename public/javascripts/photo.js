var state = {
    "photo": null,
    "prev": null,
    "next": null,
    "search": window.location.search,
};


function getCookieValue(getKey){
    for (item of document.cookie.split(";")) {
        let p = item.indexOf("=");
        let key = item.substring(0, p);
        if (key === getKey)
            return item.substring(p + 1, item.length);
    };
    return null;
}


var detailTextFade = function(tableRow, text) {
	if (tableRow.children().eq(1).text() == text) {
		return;
	}
	if (text == null) {
		tableRow.fadeOut(200, function() {
			tableRow.children().eq(1).text("");
		});
		return;
	}
	if (tableRow.is(':hidden')) {
		setTimeout(function() {
			tableRow.children().eq(1).text(text);
			tableRow.fadeTo(0, 0);
			tableRow.show();
			tableRow.fadeTo(200, 1);
		}, 200);
		return;
	}
	tableRow.children().eq(1).fadeTo(200, 0, function() {
		tableRow.children().eq(1).text(text);
		tableRow.children().eq(1).fadeTo(200, 1);
	});
};

var detailHtmlFade = function(tableRow, html) {
	if (tableRow.children().eq(1).html() == html) {
		return;
	}
	if (html == null) {
		tableRow.fadeOut(200, function() {
			tableRow.children().eq(1).html("");
		});
		return;
	}
	if (tableRow.is(':hidden')) {
		setTimeout(function() {
			tableRow.children().eq(1).html(html);
			tableRow.fadeTo(0, 0);
			tableRow.show();
			tableRow.fadeTo(200, 1);
		}, 200);
		return;
	}
	tableRow.children().eq(1).fadeTo(200, 0, function() {
		tableRow.children().eq(1).html(html);
		tableRow.children().eq(1).fadeTo(200, 1);
	});
};

var transition = function(id, o) {
	var container = $('#' + id + '-container');
	var content = $('#' + id + '-content');

	if (content.html() == $('<div/>').append(o).html()) {
		return;
	}

	if (!o.length) {
		container.fadeOut(200, function() {
			content.empty();
		});
		return;
	}

	if (container.is(':hidden')) {
		setTimeout(function() {
			content.append(o);
			container.fadeTo(0, 0);
			container.show();
			container.fadeTo(200, 1);
		}, 200);
		return;
	}

	content.fadeTo(200, 0, function() {
		content.empty();
		content.append(o);
		content.fadeTo(200, 1);
	});
};

var transitionHtml = function(id, html) {
	if (html == null) {
		transition(id, $());
	} else {
		transition(id, $('<span/>').html(html));
	}
}

var transitionText = function(id, text) {
	if (text == null) {
		transition(id, $());
	} else {
		transition(id, $('<span/>').text(text));
	}
};

var transitionMap = function(longitude, latitude) {
	if ($("body").hasClass("fullscreen")) {
		return; // do not update map if we're in fullsceen mode to improve performance and reduce requests
	}
	var newMap;
	if (longitude && latitude) {
		newMap = $('<iframe>').attr("id", "map_canvas").attr("src", "https://www.google.com/maps/embed/v1/place?key=AIzaSyCSTI76nMO56dK7L252bAD24E6rQROFRms&q="+latitude+","+longitude);
	} else {
		newMap = $();
	}
	transition('map', newMap);
};

var transitionEditUrl = function() {
	var returnUrl = state.photo.url;
	var editHtml = null;
	if (state.photo.canEdit) {
		edit = $("<a/>").attr("href", state.photo.url + "/_replace?returnUrl=" + encodeURIComponent(returnUrl)).append("<i class='fas fa-upload' />");
		edit.append(" &nbsp; ");
		edit.append($("<a/>").attr("href", state.photo.url + "/_edit?returnUrl=" + encodeURIComponent(returnUrl)).append("<i class='fas fa-edit' />"));
		editHtml = edit.prop("outerHTML");
	}
	transitionHtml("editUrl", editHtml);
}

var transitionAuthorRating = function(rating) {
	if (state.photo.canEdit) {
		var stars = $("<span/>");
		for (var i = 1; i <= 5; i++) {
			stars.append($("<a/>").attr("id", "rate"+i).attr("href", "#").html(rating >= i ? "&starf;" : "&star;")).append(" ");
		}
		transitionHtml("authorRating", stars.prop("outerHTML"));
	} else {
		transitionHtml("authorRating", null);
	}
}

var transitionPrevNextArrows = function(prev, next) {
    if (prev) {
        $("div.large-picture i.fa-angle-left").removeClass("nodisplay");
    } else {
        $("div.large-picture i.fa-angle-left").addClass("nodisplay");
    }
    if (next) {
        $("div.large-picture i.fa-angle-right").removeClass("nodisplay");
    } else {
        $("div.large-picture i.fa-angle-right").addClass("nodisplay");
    }
}

var transitionImage = function(id, push) {
	$.get("/" + id + "/_state" + state.search, {}, function(data) {
		state.photo = data.photo;
		state.next = data.next;
		state.prev = data.prev;



		var imgOld = $("img#mainphoto");
		var imgNew = $("<img />");
		imgNew.attr("id", imgOld.attr("id"));
		imgNew.attr("width", state.photo.width);
		imgNew.attr("height", state.photo.height);
		imgNew.attr("class", imgOld.attr("class"));
		imgNew.attr("sizes", imgOld.attr("sizes"));
		imgNew.attr("srcset", state.photo.srcset);
		imgNew.attr("style", "max-width: calc(min((100vh - 100px) * " + state.photo.width + " / " + state.photo.height + ", " + state.photo.width + "px)); height: auto;");
		imgNew.attr("title", state.photo.label);
		imgNew.attr("src", "/pictures/xlarge/" + state.photo.id + ".jpg");
		imgOld.fadeTo(200, 0, function() {
			try {
				window.history.replaceState(null, null, state.photo.path);
			} catch (err) {
				// probably IE
			}
			document.title = state.photo.title;
			imgOld.replaceWith(imgNew);
			imgNew.fadeTo(200, 1);
		});

        transitionPrevNextArrows(state.prev, state.next);
		transitionText("label", state.photo.label);
		transitionText("fullscreen-label", state.photo.label);
		transitionHtml("description", state.photo.description ? state.photo.description.html : null);
		transitionText("collection", state.photo.collection ? state.photo.collection.name : null);
		transitionText("author", state.photo.author ? state.photo.author.name : null);
		transitionText("photographer", state.photo.photographer);
		transitionHtml("vehicleClass", state.photo.vehicleClass ? state.photo.vehicleClass.html : null);
		transitionHtml("vehicleSeries", state.photo.vehicleClass ? (state.photo.vehicleClass.vehicleSeries ? state.photo.vehicleClass.vehicleSeries.html : null) : null);
		transitionHtml("operator", state.photo.operator ? state.photo.operator.html : null);
		transitionHtml("photoDate", state.photo.photoDate ? state.photo.photoDate.html : null);
		transitionText("camera", state.photo.camera);
		transitionText("focalLength", state.photo.focalLength);
		transitionText("aperture", state.photo.aperture);
		transitionText("exposure", state.photo.exposure);
		transitionText("sensitivity", state.photo.sensitivity);
		transitionHtml("license", state.photo.license.html);
		transitionText("viewsUncollected", state.photo.viewsUncollected);
		transitionHtml("download", state.photo.download.html);

		transitionMap(state.photo.longitude, state.photo.latitude);

		transitionEditUrl();

		transitionAuthorRating(state.photo.authorRating);
	});
};


var changeAuthorRating = function(rating) {
	if (!state.photo.canEdit) {
		return;
	}
	var data = {
		csrfToken : getCookieValue("csrfToken"),
		id : state.photo.id,
		authorRating : rating,
	};
	$.post(state.photo.url + "/_rate", data, function() {
		transitionAuthorRating(rating);
	});
};

var sharePictureFacebook = function() {
	window.open("https://www.facebook.com/sharer.php?u=" + encodeURIComponent("https://www.bahnbilder.ch/picture/" + state.photo.id));
};

var copyToClipboard = function(text) {
	var input = $("<input>");
	$("body").append(input);
	input.val(text).select();
	document.execCommand("copy");
	input.remove();
}

var shareBbSmall = function() {
	copyToClipboard(state.photo.bbCodeSmall);
}

var shareBbLarge= function() {
	copyToClipboard(state.photo.bbCodeLarge);
}

var sizes;
var startFullscreen = function() {
	var img = $("img#mainphoto");
	var body = $("body");
	body.addClass("fullscreen");
	sizes = img.attr("sizes");
	console.log(sizes);
	img.attr("sizes", "100vw");
	document.documentElement.requestFullscreen();
}

var endFullscreen = function() {
	var img = $("img#mainphoto");
	var body = $("body");
	body.removeClass("fullscreen");
	transitionMap(state.photo.longitude, state.photo.latitude);
	img.attr("sizes", sizes);
	document.exitFullscreen();
}

$(document).ready(function() {
    var id = $("#mainphoto").data("id");
    $.get("/" + id + "/_state" + state.search, {}, function(data) {
        state.photo = data.photo;
        state.next = data.next;
        state.prev = data.prev;
        try {
            window.history.replaceState(null, null, state.photo.path);
        } catch (err) {
            // too bad
        }
        transitionPrevNextArrows(state.prev, state.next);
        $("#imagecontainer").mouseover(function() {
            if (state.prev)
                $("#previmage").fadeIn();
            if (state.next)
                $("#nextimage").fadeIn();
            $("#options").fadeIn();
        });
        $("#imagecontainer").mouseleave(function() {
            $("#previmage").fadeOut();
            $("#nextimage").fadeOut();
            $("#options").fadeOut();
        });
        $("div.large-picture i.fa-angle-left").click(function() {
            if (state.prev) {
                transitionImage(state.prev, true);
            }
        });
        $("div.large-picture i.fa-angle-right").click(function() {
            if (state.next) {
                transitionImage(state.next, true);
            }
        });
        $("#shareFb").click(function() {
            sharePictureFacebook();
        });
        $("#shareBbSmall").click(function() {
            shareBbSmall();
        });
        $("#shareBbLarge").click(function() {
            shareBbLarge();
        });
        $(document).on("click", "#rate1", function() {
            changeAuthorRating(1);
            return false;
        });
        $(document).on("click", "#rate2", function() {
            changeAuthorRating(2);
            return false;
        });
        $(document).on("click", "#rate3", function() {
            changeAuthorRating(3);
            return false;
        });
        $(document).on("click", "#rate4", function() {
            changeAuthorRating(4);
            return false;
        });
        $(document).on("click", "#rate5", function() {
            changeAuthorRating(5);
            return false;
        });
        $(document).on("click", "#endFullscreen", function() {
            endFullscreen();
            return false;
        });
        $(document).on("click", "#startFullscreen", function() {
            startFullscreen();
            return false;
        });
        $(document).keydown(function(e) {
            if ( e.which > 48 && e.which < 54 ) {
                changeAuthorRating( e.which - 48 );
                e.preventDefault();
                return;
            }
            if ( e.which > 96 && e.which < 102 ) {
                changeAuthorRating( e.which - 96 );
                e.preventDefault();
                return;
            }
            switch (e.which) {
            case 37: // left
                if (state.prev) {
                    transitionImage(state.prev, true);
                }
                e.preventDefault();
                break;
            case 39: // right
                if (state.next) {
                    transitionImage(state.next, true);
                }
                e.preventDefault();
                break;
            case 70: // f
            case 86: // v
                if ($("body").hasClass("fullscreen")) {
                    endFullscreen();
                } else {
                    startFullscreen();
                }
                e.preventDefault();
                break;
            default:
                return; // exit this handler for other keys
            }
        });
        transitionEditUrl();
    });
});
