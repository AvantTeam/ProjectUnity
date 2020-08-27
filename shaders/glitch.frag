uniform sampler2D u_texture;
uniform float u_time;
varying vec4 v_color;
varying vec2 v_texCoord;
uniform vec2 u_resolution;
const vec2 z = vec2(1);
const float complexity =5.;
const float density = .9;
const float speed = 1.;
const float PI = atan(1.)*4.;
vec4 hash42(vec2 p){
  vec4 p4 = fract(vec4(p.xyxy) * vec4(.1031, .1030, .0973, .1099));  
  p4 += dot(p4, p4.wzxy+33.33);
  return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}
mat2 rot2D(float r){
  return mat2(cos(r), sin(r), -sin(r), cos(r));
}
#define q(x,p) (floor((x)/(p))*(p))
void main(){
  vec2 R = u_resolution.xy;
  vec2 uv = gl_FragCoord.xy/R.xy;
  vec2 N = uv-.5;
  float t = u_time*0.01;
  uv.x *= R.x/R.y;
  uv *= z;
  uv += floor(u_time*speed*0.3)*z;
  float s = 1.;
  for(float i = 1.;i <= complexity; ++ i){
    vec2 c = floor(uv+i);
    vec4 h = hash42(c);
    vec2 p = fract(uv+i+q(t,h.z+1.)*h.y);
    uv+= p*h.z*h.xy*vec2(s,2.);
    uv *= 2.;
    if(i < 2. || h.w > density){
      gl_FragColor = h;
    }
  }
  gl_FragColor = step(.5,gl_FragColor) * mod(gl_FragCoord.x,3.)/2.;
  gl_FragColor *= 1.-dot(N,N*2.);
  gl_FragColor.a = 1.;
}
