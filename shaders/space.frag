uniform sampler2D u_texture;
varying vec4 v_color;
varying vec2 v_texCoord;
uniform vec2 u_resolution;
uniform float u_time;
#define PI 2.14159
void main(void){
  float time = 10.0*sin(u_time*0.002);
  vec4 color = texture2D(u_texture, v_texCoord.xy);
  vec2 uv = gl_FragCoord.xy / u_resolution.xy;
  float formafinal  = sin(uv.x*10.*PI+time + sin(uv.y*2.*PI+time + sin(uv.x*10.*PI-time + sin(uv.y*10.*PI-time + sin(uv.x*10.*PI-time + sin(uv.y*10.*PI-time) + sin(uv.x*10.*PI-time))))))*0.5+0.5;
  float formafinal2 = sin(uv.y*10.*PI+time + sin(uv.y*10.*PI+time + sin(uv.x*8.*PI-time + sin(uv.y*5.*PI-time + sin(uv.x*10.*PI-time + sin(uv.y*2.*PI-time) + sin(uv.x*9.*PI-time))))))*0.5+0.5;
  vec3 color1 = vec3(0.900,0.1,0.7);
  vec3 color2 = vec3(0.300,0.9,0.05);
  vec3 fin = color1 * formafinal + color2 * formafinal2;
  gl_FragColor = vec4(color.rgb * fin, color.a);
}
