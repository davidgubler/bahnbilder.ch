class SunPos {


    constructor() {
        const div = document.createElement('div');
        div.style.cssText = 'width: 401px; height: 450px; position: fixed;left: 200px; top: 200px;';

        document.body.appendChild(div);

        this.slider = document.createElement('input');
        this.slider.setAttribute('type', 'range');
        this.slider.setAttribute('id', 'time');
        this.slider.setAttribute('value', '15');
        this.slider.setAttribute('min', '0');
        this.slider.setAttribute('max', '288');
        this.slider.style.cssText = 'margin-left: 20px; width: 360px;';

        this.canvas = document.createElement('canvas');
        this.canvas.setAttribute("draggable", true);
        this.canvas.style.cssText = 'width:401px;height:401px;';
        this.canvas.width = 400;
        this.canvas.height = 400;

        this.canvas.addEventListener('dragstart', function(event){
            var style = window.getComputedStyle(div, null);
            event.dataTransfer.setData("text/plain",(parseInt(style.getPropertyValue("left"),10) - event.clientX) + ',' + (parseInt(style.getPropertyValue("top"),10) - event.clientY));
        }, false);

        document.body.addEventListener('dragover',function(event){
            event.preventDefault();
            return false;
        },false);

        document.body.addEventListener('drop',function(event) {
            const offset = event.dataTransfer.getData("text/plain").split(',');
            div.style.left = (event.clientX + parseInt(offset[0],10)) + 'px';
            div.style.top = (event.clientY + parseInt(offset[1],10)) + 'px';
            event.preventDefault();
            return false;
        },false);

        div.appendChild(this.slider);
        div.appendChild(document.createElement('br'));
        div.appendChild(this.canvas);

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

    drawNight(ctx, radius, x, y, date, lat, lng, sunrise, sunset) {
        let startRadians = SunCalc.getPosition(sunset, lat, lng).azimuth * Math.PI / 180;
        let endRadians = SunCalc.getPosition(sunrise, lat, lng).azimuth * Math.PI / 180;
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.arc(x, y, radius, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        ctx.closePath();
        ctx.fillStyle = "#0022dd55";
        ctx.fill();
    }

    drawSunArc(ctx, radius, x, y, date, lat, lng, sunrise, sunset) {
        ctx.strokeStyle = "#ffff00";
        ctx.beginPath();
        for (let i = sunrise.getTime(); i < sunset.getTime(); i += 10*60*1000) {
            let sunPos = SunCalc.getPosition(new Date().setTime(i), lat, lng);
            let onCircle = this.posOnCircle(radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
            ctx.lineTo(x + onCircle.x, y + onCircle.y);
        }
        let sunPos = SunCalc.getPosition(sunset, lat, lng);
        let onCircle = this.posOnCircle(radius * (1 - sunPos.altitude / 90), sunPos.azimuth * Math.PI / 180);
        ctx.lineTo(x + onCircle.x, y + onCircle.y);
        ctx.stroke();
    }

    drawCrossHairs(ctx, x, y) {
        ctx.strokeStyle = "#ffffff";
        ctx.beginPath();
        ctx.arc(x, y, 10, 0, 2 * Math.PI);
        ctx.lineTo(x-10, y);
        ctx.moveTo(x, y-10);
        ctx.lineTo(x, y+10);
        ctx.stroke();
    }

    drawInfos(ctx, x, y, date) {
        ctx.font = "16px sans-serif";
        ctx.fillStyle = "#ffffff";
        ctx.textAlign = 'right';
        ctx.fillText(new Intl.DateTimeFormat().format(date), x, y);
        ctx.fillText(date.getHours().toString().padStart(2, '0') + ":" + date.getMinutes().toString().padStart(2, '0'), x, y + 20);
    }

    drawCircle(ctx) {
        ctx.strokeStyle = "white";
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(200, 200, 180, 0, 2 * Math.PI);
        ctx.stroke();

        for (let i = 0; i < 90; i+=10) {
            ctx.strokeStyle = "#ffffff33";
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.arc(200, 200, i * 2, 0, 2 * Math.PI);
            ctx.stroke();
        }

        for (let i = 0; i < 360; i+=10) {
            ctx.strokeStyle = "#ffffff33";
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(200, 200);
            let pos = this.posOnCircle(180, i * Math.PI / 180);
            ctx.lineTo(200 + pos.x, 200 + pos.y);
            ctx.stroke();
        }
    }

    draw(canvas, ctx, date, lat, lng) {
        const sunCalcTimes = SunCalc.getTimes(date, lat, lng);
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        this.drawNight(ctx, 180, 200, 200, date, lat, lng, sunCalcTimes.sunrise, sunCalcTimes.sunset);
        this.drawSunArc(ctx, 180, 200, 200, date, lat, lng, sunCalcTimes.sunrise, sunCalcTimes.sunset);
        this.drawCrossHairs(ctx, 200, 200);
        this.drawInfos(ctx, 380, 30, date);
        this.drawCircle(ctx);

        const sunPosition = SunCalc.getPosition(date, lat, lng);

        /* draw 30° sector */
        let startRadians = (sunPosition.azimuth - 30)* Math.PI / 180;
        let endRadians = (sunPosition.azimuth + 30) * Math.PI / 180;
        ctx.beginPath();
        ctx.moveTo(200, 200);
        ctx.arc(200, 200, 180, startRadians - 0.5*Math.PI, endRadians - 0.5*Math.PI);
        ctx.closePath();
        ctx.fillStyle = "#ee000055";
        ctx.fill();

        const sunPosOnOuterCircle = this.posOnCircle(180, sunPosition.azimuth * Math.PI / 180);
        ctx.beginPath();
        ctx.moveTo(200, 200);
        ctx.lineTo(200 + sunPosOnOuterCircle.x, 200 + sunPosOnOuterCircle.y);
        ctx.lineWidth = 1;
        ctx.strokeStyle = "#ffff00";
        ctx.stroke();
        const sunPosOnAltitudeCircle = this.posOnCircle(180 - 180 * sunPosition.altitude / 90, sunPosition.azimuth * Math.PI / 180)
        this.drawSun(sunPosOnAltitudeCircle.x + 200, sunPosOnAltitudeCircle.y + 200)
    }

    init(map) {
        console.log(map);

        const lat = 47;
        const lng = 8;

        const date = new Date();
        const startOfDay = new Date();
        startOfDay.setTime(date.getTime());
        startOfDay.setHours(0,0,0,0);
        this.slider.value = (date.getTime() - startOfDay.getTime()) / 1000 / 60 / 5;
        this.draw(this.canvas, this.ctx, date, lat, lng);

        const self = this;
        this.slider.addEventListener('input', function(event){
            const date = new Date();
            const minutes = parseInt(event.target.value)*5;
            date.setHours(0, minutes, 0, 0);
            self.draw(self.canvas, self.ctx, date, lat, lng);
        });
    }
}

const sunPos = new SunPos();
sunPos.init(null);
