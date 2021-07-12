//https://github.com/libgdx/libgdx/blob/master/gdx/res/com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl

#if defined(specularTextureFlag) || defined(specularColorFlag)
    #define specularFlag
#endif

#if defined(colorFlag)
    varying vec4 v_color;
#endif

#ifdef blendedFlag
    varying float v_opacity;
    #ifdef alphaTestFlag
        varying float v_alphaTest;
    #endif
#endif

#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
    #define textureFlag
#endif

#ifdef diffuseTextureFlag
    varying vec2 v_diffuseUV;
#endif

#ifdef specularTextureFlag
    varying vec2 v_specularUV;
#endif

#ifdef emissiveTextureFlag
    varying vec2 v_emissiveUV;
#endif

#ifdef diffuseColorFlag
    uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
    uniform sampler2D u_diffuseTexture;
#endif

#ifdef specularColorFlag
    uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
    uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
    uniform sampler2D u_normalTexture;
#endif

#ifdef emissiveColorFlag
    uniform vec4 u_emissiveColor;
#endif

#ifdef emissiveTextureFlag
    uniform sampler2D u_emissiveTexture;
#endif

#ifdef lightingFlag
    varying vec4 v_lightDiffuse;

    #if defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
        #define ambientFlag
    #endif

    #ifdef specularFlag
        varying vec4 v_lightSpecular;
    #endif

    #ifdef shadowMapFlag
        uniform sampler2D u_shadowTexture;
        uniform float u_shadowPCFOffset;
        varying vec3 v_shadowMapUv;
        #define separateAmbientFlag

        float getShadowness(vec2 offset){
            const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
            return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
        }

        float getShadow(){
            return (
                getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
                getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
                getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
                getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))
            ) * 0.25;
        }
    #endif

    #if defined(ambientFlag) && defined(separateAmbientFlag)
        varying vec4 v_ambientLight;
    #endif
#endif

#ifdef fogFlag
    uniform vec4 u_fogColor;
    varying float v_fog;
#endif

void main(){
    #if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
    #elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    #elif defined(diffuseTextureFlag) && defined(colorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;
    #elif defined(diffuseTextureFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
    #elif defined(diffuseColorFlag) && defined(colorFlag)
        vec4 diffuse = u_diffuseColor * v_color;
    #elif defined(diffuseColorFlag)
        vec4 diffuse = u_diffuseColor;
    #elif defined(colorFlag)
        vec4 diffuse = v_color;
    #else
        vec4 diffuse = vec4(1.0);
    #endif

    #if defined(emissiveTextureFlag) && defined(emissiveColorFlag)
        vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV) * u_emissiveColor;
    #elif defined(emissiveTextureFlag)
        vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV);
    #elif defined(emissiveColorFlag)
        vec4 emissive = u_emissiveColor;
    #else
        vec4 emissive = vec4(0.0);
    #endif

    #if(!defined(lightingFlag))
        gl_FragColor = diffuse + emissive;
    #elif(!defined(specularFlag))
        #if defined(ambientFlag) && defined(separateAmbientFlag)
            #ifdef shadowMapFlag
                gl_FragColor = (diffuse * (v_ambientLight + getShadow() * v_lightDiffuse)) + emissive;
            #else
                gl_FragColor = (diffuse * (v_ambientLight + v_lightDiffuse)) + emissive;
            #endif
        #else
            #ifdef shadowMapFlag
                gl_FragColor = getShadow() * (diffuse * v_lightDiffuse) + emissive;
            #else
                gl_FragColor = (diffuse * v_lightDiffuse) + emissive;
            #endif
        #endif
    #else
        #if defined(specularTextureFlag) && defined(specularColorFlag)
            vec4 specular = texture2D(u_specularTexture, v_specularUV) * u_specularColor * v_lightSpecular;
        #elif defined(specularTextureFlag)
            vec4 specular = texture2D(u_specularTexture, v_specularUV) * v_lightSpecular;
        #elif defined(specularColorFlag)
            vec4 specular = u_specularColor * v_lightSpecular;
        #else
            vec4 specular = v_lightSpecular;
        #endif

        #if defined(ambientFlag) && defined(separateAmbientFlag)
            #ifdef shadowMapFlag
                gl_FragColor = (diffuse * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular + emissive;
            #else
                gl_FragColor = (diffuse * (v_lightDiffuse + v_ambientLight)) + specular + emissive;
            #endif
        #else
            #ifdef shadowMapFlag
                gl_FragColor = getShadow() * ((diffuse * v_lightDiffuse) + specular) + emissive;
            #else
                gl_FragColor = (diffuse * v_lightDiffuse) + specular + emissive;
            #endif
        #endif
    #endif

    #ifdef fogFlag
        gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
    #endif

    #ifdef blendedFlag
        gl_FragColor.a = diffuse.a * v_opacity;
        #ifdef alphaTestFlag
            if (gl_FragColor.a <= v_alphaTest)
            discard;
        #endif
    #else
        gl_FragColor.a = 1.0;
    #endif
}
