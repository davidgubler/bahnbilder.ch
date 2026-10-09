class SunPos {
    constructor(map) {
        const self = this;
        this.map = map;
        this.sunPosDate = new Date();
        this.x = 300;
        this.y = 300;

        this.slider = document.createElement('input');
        this.slider.setAttribute('type', 'range');
        this.slider.setAttribute('id', 'time');
        this.slider.setAttribute('value', '15');
        this.slider.setAttribute('min', '0');
        this.slider.setAttribute('max', '288');
        this.slider.style.cssText = 'width: 100%;';
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

        this.timeElem = document.createElement('span');
        this.sunHeightElem = document.createElement('span');
        this.sunHeightElem.style.cssText = "float: right; text-align: right;";

        const infoDiv = document.createElement('div');
        infoDiv.style.cssText = "margin-top: -8px;";
        infoDiv.appendChild(this.timeElem);
        infoDiv.appendChild(this.sunHeightElem);

        const sliderDiv = document.createElement('div');
        sliderDiv.appendChild(this.slider);
        sliderDiv.appendChild(infoDiv);
        sliderDiv.style.cssText = 'width: calc(min(100%, 400px)); height: 46px; position: absolute; bottom: 10px; background-color: #000000cc; left: calc(50% - min(100%, 400px)/2); padding-top: 4px; padding-left: 10px; padding-right: 10px;';
        sliderDiv.style.visibility = 'hidden';
        map.getDiv().appendChild(sliderDiv);

        const div1 = document.createElement('div');
        div1.style.cssText = 'position: relative; width: 100%; height: 100%; pointer-events: none;';
        div1.style.visibility = 'hidden';
        const div2 = document.createElement('div');
        div2.style.cssText = 'position: absolute; inset: 0; max-height: 100%; max-width: 100%; object-fit: contain; margin: auto; padding: 50px 0px 50px 0px;';
        div1.appendChild(div2);
        this.canvas = document.createElement('canvas');
        this.canvas.style.cssText = 'width: 100%; height: 100%;';
        div2.append(this.canvas);
        map.getDiv().appendChild(div1);

        this.ctx = this.canvas.getContext("2d");

        // Add button to map UI
        const btn = document.createElement('button');
        btn.style.backgroundColor = '#fff';
        btn.style.border = '2px solid #fff';
        btn.style.borderRadius = '2px';
        btn.style.boxShadow = '0 1px 1px rgba(0,0,0,.1)';
        btn.style.color = 'rgb(25,25,25)';
        btn.style.cursor = 'pointer';
        btn.style.fontFamily = 'Robot,Arial,sans-serif';
        btn.style.fontSize = '24px';
        btn.style.lineHeight = '38px';
        btn.style.margin = '8px 10px 22px';
        btn.style.padding = '0 5px';
        btn.style.textAlign = 'center';
        btn.style.width = '40px';
        btn.textContent = '☼';
        btn.type = 'button';
        btn.addEventListener('click', () => {
            if (div1.style.visibility == 'hidden') {
                div1.style.visibility = 'visible';
                sliderDiv.style.visibility = 'visible';
                btn.style.backgroundColor = '#ccc';
                btn.style.border = '2px solid #ccc';
            } else {
                div1.style.visibility = 'hidden';
                sliderDiv.style.visibility = 'hidden';
                btn.style.backgroundColor = '#fff';
                btn.style.border = '2px solid #fff';
            }
        });
        const centerControlDiv = document.createElement('div');
        centerControlDiv.appendChild(btn);
        map.controls[google.maps.ControlPosition.INLINE_END_BLOCK_START].push(centerControlDiv);
    }

    posOnCircle(radius, radians) {
        const x = Math.sin(radians) * radius;
        const y = -Math.cos(radians) * radius;
        return {x: x, y: y};
    }

    drawSun() {
        if (this.sunPosition.altitude < 0) {
            return;
        }
        const sunPosOnAltitudeCircle = this.posOnCircle(this.radius * (1 - this.sunPosition.altitude / 90), this.sunPosition.azimuth * Math.PI / 180)
        const x = sunPosOnAltitudeCircle.x + this.centerX;
        const y = sunPosOnAltitudeCircle.y + this.centerY;
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

    drawNight() {
        let startRadians = SunCalc.getPosition(this.sunCalcTimes.sunset, this.lat, this.lng).azimuth * Math.PI / 180;
        let endRadians = SunCalc.getPosition(this.sunCalcTimes.sunrise, this.lat, this.lng).azimuth * Math.PI / 180;
        if ((SunCalc.getPosition(this.sunCalcTimes.solarNoon, this.lat, this.lng).azimuth - 90) < 0) {
            const tmp = endRadians;
            endRadians = startRadians;
            startRadians = tmp;
        }
        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.arc(this.centerX, this.centerY, this.radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        this.ctx.closePath();
        this.ctx.fillStyle = "#0022dd55";
        this.ctx.fill();
    }

    drawSunArc() {
        const sunrise = this.sunCalcTimes.sunrise;
        const sunset = this.sunCalcTimes.sunset;
        this.ctx.beginPath();
        this.ctx.strokeStyle = "#ffff00";
        let sunPos = SunCalc.getPosition(sunrise, this.lat, this.lng);
        let onCircle = this.posOnCircle(this.radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
        this.ctx.moveTo(this.centerX + onCircle.x, this.centerY + onCircle.y);

        for (let i = sunrise.getTime() + 10*60*1000; i < sunset.getTime(); i += 10*60*1000) {
            let sunPos = SunCalc.getPosition(new Date().setTime(i), this.lat, this.lng);
            let onCircle = this.posOnCircle(this.radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
            this.ctx.lineTo(this.centerX + onCircle.x, this.centerY + onCircle.y);
        }

        sunPos = SunCalc.getPosition(sunset, this.lat, this.lng);
        onCircle = this.posOnCircle(this.radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
        this.ctx.lineTo(this.centerX + onCircle.x, this.centerY + onCircle.y);
        this.ctx.stroke();
    }

    drawSunLine() {
        const sunPosOnOuterCircle = this.posOnCircle(this.radius, this.sunPosition.azimuth * Math.PI / 180);
        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.lineTo(this.centerX + sunPosOnOuterCircle.x, this.centerY + sunPosOnOuterCircle.y);
        this.ctx.lineWidth = 1;
        this.ctx.strokeStyle = "#ffff00";
        this.ctx.stroke();
    }

    drawInfos() {
        const date = new Intl.DateTimeFormat().format(this.sunPosDate);
        const time = this.sunPosDate.getHours().toString().padStart(2, '0') + ":" + this.sunPosDate.getMinutes().toString().padStart(2, '0');
        this.timeElem.innerHTML = date + " " + time;

        const sunHeight = Math.round(this.sunPosition.altitude);
        this.sunHeightElem.innerHTML = "&#9788; " + " " + sunHeight + "°";
    }

    drawCircle() {
        this.ctx.strokeStyle = "white";
        this.ctx.lineWidth = 2;
        this.ctx.beginPath();
        this.ctx.arc(this.centerX, this.centerY, this.radius, 0, 2 * Math.PI);
        this.ctx.stroke();
        this.ctx.fillStyle = "#40404030";
        this.ctx.fill();

        for (let i = 10; i <= 90; i+=10) {
            this.ctx.strokeStyle = "#ffffff66";
            this.ctx.lineWidth = 1;
            this.ctx.beginPath();
            this.ctx.arc(this.centerX, this.centerY, i * this.radius / 90, 0, 2 * Math.PI);
            this.ctx.stroke();
        }

        for (let i = 0; i < 360; i+=10) {
            this.ctx.strokeStyle = "#ffffff66";
            this.ctx.lineWidth = 1;
            this.ctx.beginPath();
            this.ctx.moveTo(this.centerX, this.centerY);
            let pos = this.posOnCircle(this.radius, i * Math.PI / 180);
            this.ctx.lineTo(this.centerX + pos.x, this.centerY + pos.y);
            this.ctx.stroke();
        }
    }

    drawSector30() {
        let startRadians = (this.sunPosition.azimuth - 30)* Math.PI / 180;
        let endRadians = (this.sunPosition.azimuth + 30) * Math.PI / 180;
        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.arc(this.centerX, this.centerY, this.radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        this.ctx.closePath();
        this.ctx.fillStyle = "#ee000044";
        this.ctx.fill();
    }

    draw() {
        const cs = this.canvas.getBoundingClientRect();
        this.canvas.width = cs.width;
        this.canvas.height = cs.height;
        this.centerX = Math.round(this.canvas.width / 2);
        this.centerY = Math.round(this.canvas.height / 2);
        this.radius = Math.round(Math.min(this.canvas.width, this.canvas.height) / 2) - 20;

        this.sunPosition = SunCalc.getPosition(this.sunPosDate, this.lat, this.lng);
        this.sunCalcTimes = SunCalc.getTimes(this.sunPosDate, this.lat, this.lng);
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.drawCircle();
        this.drawNight();
        this.drawSunArc();
        this.drawInfos();
        this.drawSector30();
        this.drawSunLine();
        this.drawSun()
    }

    updatePos() {
        this.lat = this.map.getCenter().lat();
        this.lng = this.map.getCenter().lng();
        this.draw();
    }
}

