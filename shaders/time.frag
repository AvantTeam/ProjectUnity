#define PI 3.14159265
uniform sampler2D u_texture;
varying vec4 v_color;
varying vec2 v_texCoord;
uniform float u_time;
uniform vec2 u_resolution;
void main(void){
  vec4 color = texture2D(u_texture, v_texCoord.xy);
  float time = u_time*0.0001;
  float color1, color2, color3;
  color1 = (sin(dot(gl_FragCoord.xy,vec2(sin(time*1.0),cos(time*3.0)))*0.02+time*4.0)+1.0)/2.0;
  vec2 center = vec2(u_resolution.x, u_resolution.y) + vec2(u_resolution.x/2.0*sin(-time*3.0),u_resolution.y/2.0*cos(-time*10.0));
  color2 = (cos(length(gl_FragCoord.xy - center)*0.03)+1.0)/2.0;
  color3 = (color1+ color2)/2.0;
  float red = (cos(PI*color3/0.5+time*3.0)+1.0)/2.0;
  float green = (sin(PI*color3/0.5+time*3.0)+1.0)/2.0;
  float blue = (sin(PI*color3/0.25+time*3.0)+1.0)/2.0;
  vec3 fin = vec3(blue+0.6, red+0.5, green+0.5);
  gl_FragColor = vec4(color.rgb * fin, color.a);
}
