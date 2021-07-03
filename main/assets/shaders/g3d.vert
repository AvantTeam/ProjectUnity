//https://github.com/libgdx/libgdx/blob/master/gdx/res/com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl

#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
    #define textureFlag
#endif

#if defined(specularTextureFlag) || defined(specularColorFlag)
    #define specularFlag
#endif

#if defined(specularFlag) || defined(fogFlag)
    #define cameraPositionFlag
#endif

attribute vec4 a_position;
uniform mat4 u_projViewTrans;

#if defined(colorFlag)
    varying vec4 v_color;
    attribute vec4 a_color;
#endif

#ifdef normalFlag
    attribute vec3 a_normal;
    uniform mat3 u_normalMatrix;
    varying vec3 v_normal;
#endif

#ifdef textureFlag
    attribute vec2 a_texCoord0;
#endif

#ifdef diffuseTextureFlag
    uniform vec4 u_diffuseUVTransform;
    varying vec2 v_diffuseUV;
#endif

#ifdef emissiveTextureFlag
    uniform vec4 u_emissiveUVTransform;
    varying vec2 v_emissiveUV;
#endif

#ifdef specularTextureFlag
    uniform vec4 u_specularUVTransform;
    varying vec2 v_specularUV;
#endif

uniform mat4 u_worldTrans;

#ifdef shininessFlag
    uniform float u_shininess;
#else
    const float u_shininess = 20.0;
#endif

#ifdef blendedFlag
    uniform float u_opacity;
    varying float v_opacity;

    #ifdef alphaTestFlag
        uniform float u_alphaTest;
        varying float v_alphaTest;
    #endif
#endif

#ifdef lightingFlag
    varying vec3 v_lightDiffuse;

    #ifdef ambientLightFlag
        uniform vec3 u_ambientLight;
    #endif

    #ifdef ambientCubemapFlag
        uniform vec3 u_ambientCubemap[6];
    #endif

    #ifdef sphericalHarmonicsFlag
        uniform vec3 u_sphericalHarmonics[9];
    #endif

    #ifdef specularFlag
        varying vec3 v_lightSpecular;
    #endif

    #ifdef cameraPositionFlag
        uniform vec4 u_cameraPosition;
    #endif

    #ifdef fogFlag
        varying float v_fog;
    #endif

    #if numDirectionalLights > 0
        struct DirectionalLight{
            vec3 color;
            vec3 direction;
        };
        uniform DirectionalLight u_dirLights[numDirectionalLights];
    #endif

    #if numPointLights > 0
        struct PointLight{
            vec3 color;
            vec3 position;
        };
        uniform PointLight u_pointLights[numPointLights];
    #endif

    #if defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
        #define ambientFlag
    #endif

    #ifdef shadowMapFlag
        uniform mat4 u_shadowMapProjViewTrans;
        varying vec3 v_shadowMapUv;
        #define separateAmbientFlag
    #endif

    #if defined(ambientFlag) && defined(separateAmbientFlag)
        varying vec3 v_ambientLight;
    #endif
#endif

void main(){
    #ifdef diffuseTextureFlag
        v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    #endif

    #ifdef specularTextureFlag
        v_specularUV = u_specularUVTransform.xy + a_texCoord0 * u_specularUVTransform.zw;
    #endif

    #ifdef emissiveTextureFlag
        v_emissiveUV = u_emissiveUVTransform.xy + a_texCoord0 * u_emissiveUVTransform.zw;
    #endif

    #if defined(colorFlag)
        v_color = a_color;
    #endif

    #ifdef blendedFlag
        v_opacity = u_opacity;
        #ifdef alphaTestFlag
            v_alphaTest = u_alphaTest;
        #endif
    #endif

    gl_Position = u_projViewTrans * u_worldTrans * a_position;

    #ifdef shadowMapFlag
        vec4 spos = u_shadowMapProjViewTrans * pos;
        v_shadowMapUv.xyz = (spos.xyz / spos.w) * 0.5 + 0.5;
        v_shadowMapUv.z = min(v_shadowMapUv.z, 0.998);
    #endif

    #if defined(normalFlag)
        v_normal = normalize(u_normalMatrix * a_normal);
    #endif

    #ifdef fogFlag
        vec3 flen = u_cameraPosition.xyz - pos.xyz;
        float fog = dot(flen, flen) * u_cameraPosition.w;
        v_fog = min(fog, 1.0);
    #endif

    #ifdef lightingFlag
        #if    defined(ambientLightFlag)
            vec3 ambientLight = u_ambientLight;
        #elif defined(ambientFlag)
            vec3 ambientLight = vec3(0.0);
        #endif

        #ifdef ambientCubemapFlag
            vec3 squaredNormal = normal * normal;
            vec3 isPositive  = step(0.0, normal);
            ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
            squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
            squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
        #endif

        #ifdef sphericalHarmonicsFlag
            ambientLight += u_sphericalHarmonics[0];
            ambientLight += u_sphericalHarmonics[1] * normal.x;
            ambientLight += u_sphericalHarmonics[2] * normal.y;
            ambientLight += u_sphericalHarmonics[3] * normal.z;
            ambientLight += u_sphericalHarmonics[4] * (normal.x * normal.z);
            ambientLight += u_sphericalHarmonics[5] * (normal.z * normal.y);
            ambientLight += u_sphericalHarmonics[6] * (normal.y * normal.x);
            ambientLight += u_sphericalHarmonics[7] * (3.0 * normal.z * normal.z - 1.0);
            ambientLight += u_sphericalHarmonics[8] * (normal.x * normal.x - normal.y * normal.y);
        #endif

        #ifdef ambientFlag
            #ifdef separateAmbientFlag
                v_ambientLight = ambientLight;
                v_lightDiffuse = vec3(0.0);
            #else
                v_lightDiffuse = ambientLight;
            #endif
        #else
            v_lightDiffuse = vec3(0.0);
        #endif

        #ifdef specularFlag
            v_lightSpecular = vec3(0.0);
            vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);
        #endif

        #if(numDirectionalLights > 0) && defined(normalFlag)
            for(int i = 0; i < numDirectionalLights; i++){
                vec3 lightDir = -u_dirLights[i].direction;
                float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
                vec3 value = u_dirLights[i].color * NdotL;
                v_lightDiffuse += value;
                #ifdef specularFlag
                    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
                    v_lightSpecular += value * pow(halfDotView, u_shininess);
                #endif
            }
        #endif

        #if(numPointLights > 0) && defined(normalFlag)
            for(int i = 0; i < numPointLights; i++){
                vec3 lightDir = u_pointLights[i].position - pos.xyz;
                float dist2 = dot(lightDir, lightDir);
                lightDir *= inversesqrt(dist2);
                float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
                vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));
                v_lightDiffuse += value;
                #ifdef specularFlag
                    float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
                    v_lightSpecular += value * pow(halfDotView, u_shininess);
                #endif
            }
        #endif
    #endif
}
