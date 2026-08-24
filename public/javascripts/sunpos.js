
var posOnCircle = function(radius, radians) {
    const x = Math.sin(radians) * radius;
    const y = -Math.cos(radians) * radius;
    return {x: x, y: y};
}

var sunpos = function(map) {
    console.log(map);


    var canvas = document.createElement('canvas');
    canvas.style.cssText = 'position:fixed;width:401px;height:401px;z-index:100;background:#000;left:200px;top:200px;background-color:rgba(0,0,0,0);';
    document.body.appendChild(canvas);

    canvas.width = 400;
    canvas.height = 400;
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.strokeStyle = "white";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.arc(200, 200, 180, 0, 2 * Math.PI);
    ctx.stroke();

    const pos = posOnCircle(180, 6);
    ctx.beginPath();
    ctx.moveTo(200, 200);
    ctx.lineTo(200 + pos.x, 200 + pos.y);
    ctx.lineWidth = 1;
    ctx.stroke();

    const date = new Date();
    date.setTime(date.getTime() - 0*60*60*1000);
    console.log(date);
    const sunPosition = SunCalc.getPosition(date, 47, 8);
    console.log(sunPosition);

    const sunPosOnOuterCircle = posOnCircle(180, sunPosition.azimuth * Math.PI / 180);
    ctx.beginPath();
    ctx.moveTo(200, 200);
    ctx.lineTo(200 + sunPosOnOuterCircle.x, 200 + sunPosOnOuterCircle.y);
    ctx.lineWidth = 1;
    ctx.strokeStyle = "yellow";
    ctx.stroke();

    const sunPosOnAltitudeCircle = posOnCircle(180 - 180 * sunPosition.altitude / 90, sunPosition.azimuth * Math.PI / 180)
    ctx.beginPath();
    ctx.arc(sunPosOnAltitudeCircle.x + 200, sunPosOnAltitudeCircle.y + 200, 8, 0, 2 * Math.PI);
    ctx.fillStyle = "#ffff88";
    ctx.fill();
    ctx.lineWidth = 1;
    ctx.strokeStyle = "#ffffff";
    ctx.stroke();

}
sunpos(null);
