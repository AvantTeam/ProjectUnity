uniform sampler2D u_texture;
uniform float u_time;
uniform float u_scl;

varying vec2 v_texCoords;

float absin(float fin, float scl, float mag) {
    return (sin(fin / scl) * mag + mag) / 2.0;
}

void stroke(in vec4 color, in vec2 coord, float stroke) {
    float f = mod(coord.y + absin(u_time * 2.0, 25.0, 2.0), stroke);
    if(f > stroke / 2.0) {
        color *= absin(u_time, 10.0, 0.2) + 0.3;
    }
}

void main() {
    vec2 T = v_texCoords.xy;
    vec4 color = texture2D(u_texture, T);

    stroke(color, T, 5.5 * u_scl);

    float base = 0.3;
    color *= absin(T.y * u_scl + u_time, 15.0, 1.0 - base) + base;

    gl_FragColor = color;
}
