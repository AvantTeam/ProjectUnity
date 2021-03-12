uniform float u_time;
uniform float u_dp;

float absin(float fin, float scl, float mag) {
    return (sin(fin / scl) * mag + mag) / 2.0;
}

void stroke(in vec4 color, in vec4 fragCoord, float stroke) {
    float f = mod(fragCoord.y + absin(u_time * 2.0, 25.0, 2.0), stroke);
    if(f > stroke / 2.0) {
        color *= absin(u_time, 10.0, 0.2) + 0.3;
    }
}

void main() {
    vec4 coords = gl_FragCoord * u_dp;
    stroke(gl_FragColor, coords, 5.5);

    float base = 0.3;
    gl_FragColor *= absin(coords.y + u_time, 15.0, 1.0 - base) + base;
}
