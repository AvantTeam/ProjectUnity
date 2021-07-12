uniform float u_time;
uniform float u_scl;
uniform vec2 u_offset;

uniform sampler2D u_texture;
varying vec2 v_texCoords;

void main(){
    gl_FragColor = texture2D(u_texture, v_texCoords);
}
