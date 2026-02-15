

var autodetect = function(total, photoIds) {
    $("div#autodetectProgress").text("Progress: " + (100 - (100 * photoIds.length / total)).toFixed(1) + " %");
    $.get(window.location.href.split('?')[0] + "/_batch?photoIds=" + photoIds.slice(0,20).join(","), function(data) {
        console.log(data);
        for (photoId of data) {
            $("div#autodetectPhotos").append("<a target=\"_blank\" href=\"/" + photoId + "/_autodetect\">" + photoId + "</a><br />");
        }
        if (photoIds.length > 0) {
            autodetect(total, photoIds.slice(20));
        } else {
            $("div#autodetectProgress").remove();
        }
    });
};

$(document).ready(function() {
    // autodetectPhotoIds = autodetectPhotoIds.slice(0, 100);
    autodetect(autodetectPhotoIds.length, autodetectPhotoIds);
});
