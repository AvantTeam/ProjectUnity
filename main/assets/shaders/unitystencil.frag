uniform sampler2D u_texture;
uniform vec4 stencilcolor;

varying vec2 v_texCoords;

bool id(float a, float b){
    return a > b - 0.01 && a < b + 0.01;
}

void main(){
    vec2 T = v_texCoords.xy;
    vec4 color = texture2D(u_texture, T);
    
    if(id(color.x, stencilcolor.x) && id(color.y, stencilcolor.y) && id(color.z, stencilcolor.z)){
        color.a = 0.0;
    }
    
    gl_FragColor = color;
}
