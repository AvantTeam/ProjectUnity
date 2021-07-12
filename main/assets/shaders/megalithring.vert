attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_proj;
uniform mat4 u_trans;
uniform vec3 u_lightdir;
uniform vec3 u_camdir;
uniform vec3 u_ambientColor;

varying vec4 v_col;
varying vec2 v_texCoords;

const vec3 diffuse = vec3(0.01);
const float shinefalloff = 4.0;
const float shinelen = 0.2;

void main(){
    vec3 norc = u_ambientColor * (diffuse + vec3(clamp((dot(a_normal, u_lightdir) + 1.0) / 2.0, 0.0, 1.0)));
    float shinedot = max((-dot(u_camdir, a_normal) - (1.0 - shinelen)) / shinelen, 0.0);

    v_col = vec4(norc, 1.0);
    v_texCoords = a_texCoord0;

    gl_Position = u_proj * u_trans * a_position;
}
