#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_invsize;
uniform vec2 u_texsize;
uniform vec2 u_offset;

uniform vec2 position;

uniform vec4 tocolor;

uniform float progress;
uniform float colorprog;
uniform float fragprogress;
uniform float size;

varying vec2 v_texCoords;

const float mdist = 1.0;

void main(){
    vec2 T = v_texCoords.xy;
    vec2 v = u_invsize.xy;
    vec2 pos = ((T * u_texsize) + u_offset) / (100.0 + size);

    vec2 windpos = ((T * u_texsize) + u_offset - position) / 100.0;
    
    float len = length(windpos);
    if(len > mdist){
        float mindistort = mdist / len;
        windpos *= mindistort;
    }
    windpos *= fragprogress;

    vec4 noise = texture2D(u_noise, pos);

    vec2 frag = noise.xy * -windpos;

    vec4 color = texture2D(u_texture, T + (frag * v));
    vec4 n = texture2D(u_noise, pos + (frag / (100.0 + size)));

    if(n.z - progress < 0.001){
        color.a = 0.0;
    }
    if(n.z - colorprog < 0.001){
        color.rgb = mix(color.rgb, tocolor.rgb, tocolor.a);
    }

    gl_FragColor = color;
}
