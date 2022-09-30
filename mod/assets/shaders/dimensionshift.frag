varying lowp vec4 v_color;
varying lowp vec4 v_mix_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_progress;
uniform vec4 u_override_color;

uniform vec2 u_uv;
uniform vec2 u_uv2;
uniform vec2 u_texsize;

void main(){
    vec4 c = texture2D(u_texture, v_texCoords);
    vec2 v = vec2(1.0/u_texsize.x, 1.0/u_texsize.y);
    vec2 coords = (v_texCoords - u_uv) / v;
    vec2 center = ((u_uv + u_uv2)/2.0 - u_uv) / v;
    float dst = abs(center.y - coords.y);
    //float p = mod((c.r + c.g + c.b) * 4.0, 1.2) + 0.6;
    float base = (c.r + c.g + c.b / 3.0);
    float p = base * base * 1.5 + 0.6;
    float prog = pow(u_progress, p);

    vec4 col = mix(vec4(1.0), u_override_color, u_progress);
    if(dst > (1.0 - prog) * max(center.x, center.y)){
        gl_FragColor = vec4(0.0);
    }else{
        gl_FragColor = v_color * col * mix(c, vec4(v_mix_color.rgb, c.a), v_mix_color.a);
    }

}
