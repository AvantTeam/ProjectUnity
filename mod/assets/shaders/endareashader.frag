uniform sampler2D u_texture;

uniform vec4 u_color;
uniform float u_offset_x;
uniform float u_offset_y;

//uniform vec2 u_texsize;
uniform vec2 u_invsize;

varying vec2 v_texCoords;

void main(){
    vec2 T = v_texCoords.xy;
    vec2 v = u_invsize;
    
    vec4 tex = texture2D(u_texture, T);
    vec4 maxed = max(max(max(texture2D(u_texture, T + vec2(u_offset_x, u_offset_y) * v), texture2D(u_texture, T + vec2(-u_offset_x, -u_offset_y) * v)), texture2D(u_texture, T + vec2(-u_offset_y, u_offset_x) * v)), texture2D(u_texture, T + vec2(u_offset_y, -u_offset_x) * v));
    float a = maxed.a - tex.a;
    
    if(a > 0.0){
        gl_FragColor = u_color * vec4(1.0, 1.0, 1.0, a);
    }else{
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
