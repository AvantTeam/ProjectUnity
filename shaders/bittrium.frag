uniform sampler2D u_texture;
uniform float u_time;
varying vec4 v_color;
varying vec2 v_texCoord;
void main(){
  vec4 color = texture2D(u_texture, v_texCoord.xy);
  float t = clamp((sin(u_time * .01 + gl_FragCoord.x * .01 + gl_FragCoord.y * .005) + 1.) / 2., 0., 1.);
  vec3 c = vec3(mix(0., 1., t), mix(.89, .39, t), mix(1., .85, t));
  gl_FragColor = vec4(color.rgb * c.rgb, color.a);
}
