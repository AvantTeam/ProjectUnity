uniform float u_time;
uniform float u_scl;
uniform vec2 u_offset;

uniform sampler2D u_texture;
varying vec2 v_texCoords;

float absin(float fin, float scl, float mag) {
    return (sin(fin / scl) * mag + mag) / 2.0;
}

void stripe(out vec4 color, vec2 coord, float stroke) {
    vec2 pos = coord - u_offset.xy;
    float f = mod(pos.y + absin(u_time * 2.0 * u_scl, 25.0 * u_scl, 2.0 * u_scl), stroke);
    if(f > stroke / 2.0) {
        color *= absin(u_time, 10.0, 0.2) + 0.3;
    }
}

void main() {
    vec2 T = v_texCoords.xy;
    vec4 color = texture2D(u_texture, T);

    stripe(color, T, 1.8 * u_scl);
    vec2 pos = T - u_offset.xy;

    color *= absin(pos.y + u_time * u_scl, 15.0 * u_scl, 0.6) + 0.4;
    color.a = 0.36;

    gl_FragColor = color;
}
