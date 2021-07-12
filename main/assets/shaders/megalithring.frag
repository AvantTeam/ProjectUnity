uniform sampler2D u_ringTexture;

varying vec4 v_col;
varying vec2 v_texCoords;

void main(){
    gl_FragColor = texture2D(u_ringTexture, v_texCoords) * v_col;
}
