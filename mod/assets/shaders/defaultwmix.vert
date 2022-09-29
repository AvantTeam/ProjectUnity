uniform mat4 u_projTrans;

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;
attribute vec4 a_mix_color;

varying vec4 v_color;
varying vec4 v_mix_color;
varying vec2 v_texCoords;

uniform vec2 u_viewportInverse;

void main(){
    v_texCoords = a_texCoord0;
    v_color = a_color;
    v_mix_color = a_mix_color;
    gl_Position = u_projTrans * a_position;
}
