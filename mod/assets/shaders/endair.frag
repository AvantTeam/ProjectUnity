uniform sampler2D u_texture;

uniform vec2 u_offset;
uniform float u_div;

uniform vec2 u_texsize;
uniform vec2 u_invsize;
uniform vec2 u_camsize;
uniform vec2 u_campos;

varying vec2 v_texCoords;

void main(){
    vec2 pos = (u_campos + u_offset) * u_invsize;
    vec2 T = (((v_texCoords.xy + pos) / u_texsize) * (u_camsize / vec2(u_div)));
    vec4 color = vec4(0.0);
    if(T.x >= 0.0 && T.x <= 1.0 && T.y >= 0.0 && T.y <= 1.0){
        float ax = min(T.x * u_texsize.x, 1.0) * min(1.0 - max(0.0, (u_texsize.x * T.x) - (u_texsize.x - 1.0)), 1.0);
        float ay = min(T.y * u_texsize.y, 1.0) * min(1.0 - max(0.0, (u_texsize.y * T.y) - (u_texsize.y - 1.0)), 1.0);

        color = texture2D(u_texture, T);
        
        float ave = (color.r + color.g + color.b) / 3.0;
        float lum = max(color.r, max(color.g, color.b));
        if(ave > 0.001){
            color.r = color.r / ave;
            color.g = color.g / ave;
            color.b = color.b / ave;
            color.a = lum * ax * ay * 0.5;
        }else{
            color = vec4(0.0);
        }
    }
    
    gl_FragColor = color;
}
