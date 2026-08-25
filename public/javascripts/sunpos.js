


var posOnCircle = function(radius, radians) {
    const x = Math.sin(radians) * radius;
    const y = -Math.cos(radians) * radius;
    return {x: x, y: y};
}

var drawSun = function(ctx, x, y) {
    for (i = 0; i < 16; i++) {
        ctx.beginPath();
        ctx.arc(x, y, 22-i, 0, 2 * Math.PI);
        ctx.fillStyle = "#ffffdd" + (Math.round(0.5*i*Math.sqrt(i))).toString(16).padStart(2, '0');
        ctx.fill();
    }
    ctx.beginPath();
    ctx.arc(x, y, 6, 0, 2 * Math.PI);
    ctx.fillStyle = "#ffffff";
    ctx.fill();
}

var drawNight = function(ctx, radius, x, y, date, lat, lng, sunrise, sunset) {
    let startRadians = SunCalc.getPosition(sunset, lat, lng).azimuth * Math.PI / 180;
    let endRadians = SunCalc.getPosition(sunrise, lat, lng).azimuth * Math.PI / 180;
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.arc(x, y, radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
    ctx.closePath();
    ctx.fillStyle = "#0022dd55";
    ctx.fill();
}

var drawSunArc = function(ctx, radius, x, y, date, lat, lng, sunrise, sunset) {
    ctx.strokeStyle = "#ffff00";
    ctx.beginPath();
    for (i = sunrise.getTime(); i < sunset.getTime(); i += 10*60*1000) {
        let sunPos = SunCalc.getPosition(new Date().setTime(i), lat, lng);
        let onCircle = posOnCircle(radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
        ctx.lineTo(x + onCircle.x, y + onCircle.y);
    }
    ctx.stroke();
}

var drawCrossHairs = function(ctx, x, y) {
    ctx.strokeStyle = "#ffffff";
    ctx.beginPath();
    ctx.arc(x, y, 10, 0, 2 * Math.PI);
    ctx.lineTo(x-10, y);
    ctx.moveTo(x, y-10);
    ctx.lineTo(x, y+10);
    ctx.stroke();
}

var sunPos = function(map) {
    console.log(map);

    const lat = 47;
    const lng = 8;

    var canvas = document.createElement('canvas');
    canvas.style.cssText = 'position:fixed;width:401px;height:401px;z-index:100;background:#000;left:200px;top:200px;background-color:rgba(0,0,0,0);';
    canvas.setAttribute("draggable", true);

    canvas.addEventListener('dragstart', function(event){
        var style = window.getComputedStyle(event.target, null);
        event.dataTransfer.setData("text/plain",(parseInt(style.getPropertyValue("left"),10) - event.clientX) + ',' + (parseInt(style.getPropertyValue("top"),10) - event.clientY));
    }, false);

    document.body.addEventListener('dragover',function(event){
        event.preventDefault();
        return false;
    },false);

    document.body.addEventListener('drop',function(event) {
        var offset = event.dataTransfer.getData("text/plain").split(',');
        canvas.style.left = (event.clientX + parseInt(offset[0],10)) + 'px';
        canvas.style.top = (event.clientY + parseInt(offset[1],10)) + 'px';
        event.preventDefault();
        return false;
    },false);

    document.body.appendChild(canvas);

    canvas.width = 400;
    canvas.height = 400;
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const date = new Date();
    date.setTime(date.getTime() - 1*60*60*1000);
    const sunCalcTimes = SunCalc.getTimes(date, lat, lng);
    console.log(sunCalcTimes);

    drawNight(ctx, 180, 200, 200, date, lat, lng, sunCalcTimes.sunrise, sunCalcTimes.sunset);
    drawSunArc(ctx, 180, 200, 200, date, lat, lng, sunCalcTimes.sunrise, sunCalcTimes.sunset);
    drawCrossHairs(ctx, 200, 200);

    ctx.strokeStyle = "white";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(200, 200, 180, 0, 2 * Math.PI);
    ctx.stroke();


    console.log(date);
    const sunPosition = SunCalc.getPosition(date, lat, lng);

    const sunPosOnOuterCircle = posOnCircle(180, sunPosition.azimuth * Math.PI / 180);
    ctx.beginPath();
    ctx.moveTo(200, 200);
    ctx.lineTo(200 + sunPosOnOuterCircle.x, 200 + sunPosOnOuterCircle.y);
    ctx.lineWidth = 1;
    ctx.strokeStyle = "#ffff00";
    ctx.stroke();

    const sunPosOnAltitudeCircle = posOnCircle(180 - 180 * sunPosition.altitude / 90, sunPosition.azimuth * Math.PI / 180)
    drawSun(ctx, sunPosOnAltitudeCircle.x + 200, sunPosOnAltitudeCircle.y + 200)


}

sunPos(null);
