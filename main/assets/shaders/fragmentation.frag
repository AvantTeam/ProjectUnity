#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_invsize;
uniform vec2 u_texsize;
uniform vec2 u_campos;

uniform vec2 u_blastpos;
uniform vec2 u_blastforce;

uniform vec4 heatcolor;

uniform float heatprogress;
uniform float fragprogress;
uniform float size;

varying vec2 v_texCoords;

const float delayScl = 0.6;
const float frags = 5.0;

bool empty(vec4 col){
    return col.r == 0.0 && col.g == 0.0 && col.b == 0.0;
}

bool equal(float a, float b){
    return abs(a - b) < 0.1;
}

float curve(float f, float from, float to){
    if(f < from){
        return 0.0;
    }else if(f > to){
        return 1.0;
    }
    return (f - from) / (to - from);
}

vec4 draw(vec4 color, float j, vec2 blastpos, vec2 pos, vec2 v){
    vec2 T = v_texCoords.xy;
    
    float idx = j / (frags - 1.0);
    
    float prog = curve(fragprogress, (j / frags) * delayScl, (((j + 1.0) / frags) * delayScl) + (1.0 - delayScl));
    
    vec2 blst = u_blastforce * prog;
    vec2 blstb = (-blastpos * 50.0 * prog) / size;
    
    vec2 trns = pos + ((blst + blstb) / (100.0 + size));
    vec2 trnst = T + ((blst + blstb) * v);
    
    vec4 noise = texture2D(u_noise, trns);

    if(equal(idx, noise.r)){
        vec4 c = texture2D(u_texture, trnst);
        if(c.a <= 0.0) return color;

        vec3 heat = vec3(0.0, 0.0, 0.0);

        float sk = heatprogress * 2.0;
        if(sk > 0.001){
            vec4 scana = texture2D(u_noise, trns + vec2(0.0, sk) * v);
            vec4 scanb = texture2D(u_noise, trns + vec2(sk, 0.0) * v);
            vec4 scanc = texture2D(u_noise, trns + vec2(0.0, -sk) * v);
            vec4 scand = texture2D(u_noise, trns + vec2(-sk, 0.0) * v);

            if(!equal(idx, scana.r) || !equal(idx, scanb.r) || !equal(idx, scanc.r) || !equal(idx, scand.r)){
                heat.rgb = heatcolor.rgb;
            }
        }

        vec4 n = texture2D(u_noise, trns);
        float f = clamp(n.g - curve(prog, 0.35, 1.0), 0.0, 1.0);
        float h = curve(f, 0.2 * heatprogress, 1.0);
        if(h <= 0.0){
            heat.rgb = heatcolor.rgb;
        }
        if(f <= 0.0){
            c.a = 0.0;
        }

        c.rgb += heat.rgb;

        if(empty(color)){
            color = c;
        }else{
            color.rgb = mix(color.rgb, c.rgb, c.a);
            color.a = min(color.a + c.a, 1.0);
        }
    }
    return color;
}

void main(){
    vec2 T = v_texCoords.xy;
    vec2 v = u_invsize.xy;
    vec2 pos = ((T * u_texsize) + u_campos) / (100.0 + size);
    vec2 blastpos = ((T * u_texsize) + u_campos - u_blastpos) / 100.0;
    
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

    color = draw(color, 4.0, blastpos, pos, v);
    color = draw(color, 3.0, blastpos, pos, v);
    color = draw(color, 2.0, blastpos, pos, v);
    color = draw(color, 1.0, blastpos, pos, v);
    color = draw(color, 0.0, blastpos, pos, v);

    gl_FragColor = color;
}
