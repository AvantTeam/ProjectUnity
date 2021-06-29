in vec3 a_position;
in vec3 a_normal;

in vec2 a_texCoord0;
in vec3 a_tangent;
in vec3 a_binormal;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform mat3 u_normalMatrix;

out vec3 v_normal;
out vec3 v_position;

out vec3 v_tangent;
out vec3 v_binormal;
out vec2 v_texCoords;

void main() {
    v_normal = normalize(u_normalMatrix * a_normal);

    v_tangent = normalize(u_normalMatrix * a_tangent);
    v_binormal = normalize(u_normalMatrix * a_binormal);
    v_texCoords = a_texCoord0;

    vec4 position = u_worldTrans * vec4(a_position, 1.0);
    v_position = position.xyz;

    vec4 pos = u_projViewTrans * position;
    gl_Position = pos;
}
