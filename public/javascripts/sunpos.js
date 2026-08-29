class SunPos {
    constructor(map) {
        const self = this;
        this.map = map;
        this.sunPosDate = new Date();
        this.x = 300;
        this.y = 300;

        const div = document.createElement('div');
        div.style.cssText = 'width: 400px; height: 450px; position: fixed; left: 200px; top: 200px; pointer-events: none;';

        document.body.appendChild(div);

        this.slider = document.createElement('input');
        this.slider.setAttribute('type', 'range');
        this.slider.setAttribute('id', 'time');
        this.slider.setAttribute('value', '15');
        this.slider.setAttribute('min', '0');
        this.slider.setAttribute('max', '288');
        this.slider.style.cssText = 'margin-left: 20px; width: 360px;';
        this.slider.addEventListener('input', function(event){
            self.sunPosDate = new Date();
            const minutes = parseInt(event.target.value)*5;
            self.sunPosDate.setHours(0, minutes, 0, 0);
            self.draw();
        });
        const startOfDay = new Date();
        startOfDay.setTime(this.sunPosDate.getTime());
        startOfDay.setHours(0,0,0,0);
        this.slider.value = ((this.sunPosDate.getTime() - startOfDay.getTime()) / 1000 / 60 / 5).toString();


        const div1 = document.createElement('div');
        div1.style.cssText = 'position: relative; width: 100%; height: calc(100% - 100px); pointer-events: none;';
        const div2 = document.createElement('div');
        div2.style.cssText = 'position: absolute; inset: 0; max-height: 100%; max-width: 100%; object-fit: contain; aspect-ratio: 1 / 1; margin-left: auto; margin-right: auto;';
        div1.appendChild(div2);
        this.canvas = document.createElement('canvas');
        this.canvas.style.cssText = 'width: 100%; height: 100%;';
        div2.append(this.canvas);
        map.getDiv().appendChild(div1);

        div.appendChild(this.slider);
        div.appendChild(document.createElement('br'));
        //div.appendChild(this.canvas);

        this.ctx = this.canvas.getContext("2d");
    }

    posOnCircle(radius, radians) {
        const x = Math.sin(radians) * radius;
        const y = -Math.cos(radians) * radius;
        return {x: x, y: y};
    }

    drawSun(x, y) {
        for (let i = 0; i < 16; i++) {
            this.ctx.beginPath();
            this.ctx.arc(x, y, 22-i, 0, 2 * Math.PI);
            this.ctx.fillStyle = "#ffffdd" + (Math.round(0.5*i*Math.sqrt(i))).toString(16).padStart(2, '0');
            this.ctx.fill();
        }
        this.ctx.beginPath();
        this.ctx.arc(x, y, 6, 0, 2 * Math.PI);
        this.ctx.fillStyle = "#ffffff";
        this.ctx.fill();
    }

    drawNight(radius, x, y, nadir, sunrise, sunset) {
        let startRadians = SunCalc.getPosition(sunset, this.lat, this.lng).azimuth * Math.PI / 180;
        let endRadians = SunCalc.getPosition(sunrise, this.lat, this.lng).azimuth * Math.PI / 180;
        if ((SunCalc.getPosition(nadir, this.lat, this.lng).azimuth - 270) < 0) {
            const tmp = endRadians;
            endRadians = startRadians;
            startRadians = tmp;
        }
        this.ctx.beginPath();
        this.ctx.moveTo(x, y);
        this.ctx.arc(x, y, radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        this.ctx.closePath();
        this.ctx.fillStyle = "#0022dd55";
        this.ctx.fill();
    }

    drawSunArc(radius, x, y,  sunrise, sunset) {
        this.ctx.strokeStyle = "#ffff00";
        this.ctx.beginPath();
        for (let i = sunrise.getTime(); i < sunset.getTime(); i += 10*60*1000) {
            let sunPos = SunCalc.getPosition(new Date().setTime(i), this.lat, this.lng);
            let onCircle = this.posOnCircle(radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
            this.ctx.lineTo(x + onCircle.x, y + onCircle.y);
        }
        let sunPos = SunCalc.getPosition(sunset, this.lat, this.lng);
        let onCircle = this.posOnCircle(radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
        this.ctx.lineTo(x + onCircle.x, y + onCircle.y);
        this.ctx.stroke();
    }

    drawCrossHairs(x, y) {
        this.ctx.strokeStyle = "#ffffff";
        this.ctx.beginPath();
        this.ctx.arc(x, y, 10, 0, 2 * Math.PI);
        this.ctx.lineTo(x-10, y);
        this.ctx.moveTo(x, y-10);
        this.ctx.lineTo(x, y+10);
        this.ctx.stroke();
    }

    drawInfos(x, y) {
        this.ctx.font = "bold 16px sans-serif";
        this.ctx.fillStyle = "#000000";
        this.ctx.textAlign = 'right';
        this.ctx.fillText(new Intl.DateTimeFormat().format(this.sunPosDate), x, y);
        this.ctx.fillText(this.sunPosDate.getHours().toString().padStart(2, '0') + ":" + this.sunPosDate.getMinutes().toString().padStart(2, '0'), x, y + 20);
        this.ctx.font = "bold 16px sans-serif";
        this.ctx.fillStyle = "#ffffff";
        this.ctx.textAlign = 'right';
        this.ctx.fillText(new Intl.DateTimeFormat().format(this.sunPosDate), x-1, y-1);
        this.ctx.fillText(this.sunPosDate.getHours().toString().padStart(2, '0') + ":" + this.sunPosDate.getMinutes().toString().padStart(2, '0'), x-1, y + 19);
    }

    drawCircle(r, x, y) {
        this.ctx.strokeStyle = "white";
        this.ctx.lineWidth = 2;
        this.ctx.beginPath();
        this.ctx.arc(x, y, r, 0, 2 * Math.PI);
        this.ctx.stroke();

        for (let i = 10; i <= 90; i+=10) {
            this.ctx.strokeStyle = "#ffffff88";
            this.ctx.lineWidth = 1;
            this.ctx.beginPath();
            this.ctx.arc(x, y, i * r / 90, 0, 2 * Math.PI);
            this.ctx.stroke();
        }

        for (let i = 0; i < 360; i+=10) {
            this.ctx.strokeStyle = "#ffffff8866";
            this.ctx.lineWidth = 1;
            this.ctx.beginPath();
            this.ctx.moveTo(x, y);
            let pos = this.posOnCircle(r, i * Math.PI / 180);
            this.ctx.lineTo(x + pos.x, y + pos.y);
            this.ctx.stroke();
        }
    }

    draw() {
        const cs = this.canvas.getBoundingClientRect();
        this.canvas.width = cs.width;
        this.canvas.height = cs.height;

        const centerX = Math.round(this.canvas.width / 2);
        const centerY = Math.round(this.canvas.height / 2);
        const radius = Math.round(this.canvas.width / 2) - 20;

        const sunCalcTimes = SunCalc.getTimes(this.sunPosDate, this.lat, this.lng);
        console.log(sunCalcTimes);
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.drawNight(radius, centerX, centerY, sunCalcTimes.nadir, sunCalcTimes.sunrise, sunCalcTimes.sunset);
        this.drawSunArc(radius, centerX, centerY, sunCalcTimes.sunrise, sunCalcTimes.sunset);
        this.drawCrossHairs(centerX, centerY);
        this.drawInfos(380, 30);
        this.drawCircle(radius, centerX, centerY);

        const sunPosition = SunCalc.getPosition(this.sunPosDate, this.lat, this.lng);

        /* draw 30° sector */
        let startRadians = (sunPosition.azimuth - 30)* Math.PI / 180;
        let endRadians = (sunPosition.azimuth + 30) * Math.PI / 180;
        this.ctx.beginPath();
        this.ctx.moveTo(centerX, centerY);
        this.ctx.arc(centerX, centerY, radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        this.ctx.closePath();
        this.ctx.fillStyle = "#ee000055";
        this.ctx.fill();

        const sunPosOnOuterCircle = this.posOnCircle(radius, sunPosition.azimuth * Math.PI / 180);
        this.ctx.beginPath();
        this.ctx.moveTo(centerX, centerY);
        this.ctx.lineTo(centerX + sunPosOnOuterCircle.x, centerY + sunPosOnOuterCircle.y);
        this.ctx.lineWidth = 1;
        this.ctx.strokeStyle = "#ffff00";
        this.ctx.stroke();
        const sunPosOnAltitudeCircle = this.posOnCircle(radius - 180 * sunPosition.altitude / 90, sunPosition.azimuth * Math.PI / 180)
        this.drawSun(sunPosOnAltitudeCircle.x + centerX, sunPosOnAltitudeCircle.y + centerY)
    }

    screenToMapCoordinates() {
        // Compensate for map position in page
        const mapDiv = this.map.getDiv();
        const mapPos = mapDiv.getBoundingClientRect();
        let x = this.x - mapPos.x + 200;
        let y = this.y - mapPos.y + 200;

        // Get the current map bounds and projection
        const bounds = map.getBounds();
        const projection = map.getProjection();
        if (!bounds || !projection) return null;

        // Extract corners of the visible map area
        const neBound = bounds.getNorthEast();
        const swBound = bounds.getSouthWest();

        // Convert corners into map point instances (world pixels)
        const nePointInPx = projection.fromLatLngToPoint(neBound);
        const swPointInPx = projection.fromLatLngToPoint(swBound);

        // Calculate the percentage of where the pixel sits relative to the map container

        const percentX = x / mapDiv.clientWidth;
        const percentY = y / mapDiv.clientHeight;

        // Interpolate the world point location
        const worldX = (nePointInPx.x - swPointInPx.x) * percentX + swPointInPx.x;
        const worldY = (swPointInPx.y - nePointInPx.y) * percentY + nePointInPx.y;

        // Convert the world point back into a LatLng object
        const worldPoint = new google.maps.Point(worldX, worldY);
        return projection.fromPointToLatLng(worldPoint);
    }

    updatePos() {
        const coords = this.screenToMapCoordinates();
        this.lat = coords.lat();
        this.lng = coords.lng();
        this.draw();
    }
}

