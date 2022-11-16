uniform sampler2D u_texture;

uniform vec4 u_color;
uniform float u_offset_x;
uniform float u_offset_y;

//uniform vec2 u_texsize;
uniform vec2 u_invsize;

varying vec2 v_texCoords;

void main(){
    vec2 T = v_texCoords.xy;

    if(texture2D(u_texture, T).a <= 0){
        //vec2 coords = (T * u_texsize) + u_offset;
        vec2 v = u_invsize;

        vec4 maxed = max(max(max(texture2D(u_texture, T + vec2(u_offset_x, u_offset_y) * v), texture2D(u_texture, T + vec2(-u_offset_x, -u_offset_y) * v)), texture2D(u_texture, T + vec2(-u_offset_y, u_offset_x) * v)), texture2D(u_texture, T + vec2(u_offset_y, -u_offset_x) * v));
        //vec4 maxed = max(texture2D(u_texture, T + vec2(u_offset_x, u_offset_y) * v), texture2D(u_texture, T + vec2(-u_offset_x, -u_offset_y) * v));

        if(maxed.a > 0){
            gl_FragColor = u_color * vec4(1.0, 1.0, 1.0, maxed.a);
        }else{
            gl_FragColor = vec4(0.0);
        }
    }else{
        gl_FragColor = vec4(0.0);
    }
}
