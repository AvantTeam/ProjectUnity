uniform sampler2D u_texture;
uniform vec4 stencilcolor;
uniform vec4 heatcolor;
uniform vec2 u_invsize;

varying vec2 v_texCoords;

bool id(float a, float b){
    return a > b - 0.01 && a < b + 0.01;
}

bool cequal(vec3 a, vec3 b){
    return id(a.x, b.x) && id(a.y, b.y) && id(a.z, b.z);
}

void main(){
    vec2 T = v_texCoords.xy;
    vec2 v = u_invsize.xy;
    vec4 color = texture2D(u_texture, T);
    bool avail = true;
    
    if(cequal(color.xyz, stencilcolor.xyz)){
        color.a = 0.0;
        avail = false;
    }
    
    if(color.a < 0.1){
        avail = false;
    }
    
    if(avail){
        float scan = 2.0;
        vec4 scanxp = texture2D(u_texture, T + vec2(scan, 0.0) * v);
        vec4 scanxn = texture2D(u_texture, T + vec2(-scan, 0.0) * v);
        vec4 scanyp = texture2D(u_texture, T + vec2(0.0, scan) * v);
        vec4 scanyn = texture2D(u_texture, T + vec2(0.0, -scan) * v);
        
        if(color.a > 0.1 && (cequal(scanxp.xyz, stencilcolor.xyz) || cequal(scanxn.xyz, stencilcolor.xyz) || cequal(scanyp.xyz, stencilcolor.xyz) || cequal(scanyn.xyz, stencilcolor.xyz))){
            color += heatcolor;
        }
    }
    
    gl_FragColor = color;
}
