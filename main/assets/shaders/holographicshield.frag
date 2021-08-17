uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_texCoords;

float absin(float t) {
    return 0.5 + sin(t) * 0.5;
}

float ababsin(float t) {
    return 0.75 + sin(t) * 0.25;
}

float abababsin(float t) {
    return 0.7 + sin(t) * 0.1;
}

float absincos(float t, float n) {
    return abababsin(n * 3.14159 * cos(t));
}

void main() {
    vec4 base = texture2D(u_texture, v_texCoords);

    float L = 2800.0 + 50.0 * cos(u_time / 5.0);
    float P = 20.0;
    float N = 22.0;
    float M = 250.0 + 50.0 * sin(u_time / 5.0);
    float x = v_texCoords.x;
    float y = v_texCoords.y;
    float a = absincos((x - 2.0 * y) / L + u_time * 0.15, P) * absincos(u_time * 0.3 + y / M, N);

    gl_FragColor = base * a;
}
