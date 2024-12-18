precision highp float;
varying vec2 v_TexSrcCoord;
varying vec2 v_TexBaseCoord;
uniform float iAlpha;
uniform int blendMode;
uniform sampler2D src_Texture;
uniform sampler2D base_Texture;
void main(){
    vec4 srcColor = texture2D(src_Texture, vec2(v_TexSrcCoord.x,v_TexSrcCoord.y));
    vec4 baseColor = texture2D(base_Texture, vec2(v_TexBaseCoord.x,v_TexBaseCoord.y));
    if(blendMode== 0){
        if(srcColor.a > 0.0){
            vec4 srcFragColor = vec4(srcColor.r/srcColor.a,srcColor.g/srcColor.a,srcColor.b/srcColor.a,srcColor.a * iAlpha);
            float finalRed = srcFragColor.r * srcFragColor.a + baseColor.r * (1.0 - srcFragColor.a);
            float finalGreen = srcFragColor.g * srcFragColor.a + baseColor.g * (1.0 - srcFragColor.a);
            float finalBlue = srcFragColor.b * srcFragColor.a + baseColor.b * (1.0 - srcFragColor.a);
            float finalAlpha = srcFragColor.a * 1.0 + baseColor.a * (1.0 - srcFragColor.a);
            gl_FragColor = vec4(finalBlue/finalAlpha,finalGreen/finalAlpha,finalRed/finalAlpha,finalAlpha);
        }else{
            gl_FragColor = vec4(baseColor.b/baseColor.a,baseColor.g/baseColor.a,baseColor.r/baseColor.a,baseColor.a);
        }
    }else if(blendMode== 1) {
        srcColor = vec4(srcColor.r * iAlpha,srcColor.g * iAlpha,srcColor.b * iAlpha,srcColor.a * iAlpha);
        vec3 result = vec3(srcColor.r + (1.0-srcColor.a) * baseColor.r, srcColor.g + (1.0-srcColor.a) * baseColor.g, srcColor.b + (1.0-srcColor.a) * baseColor.b);
        vec3 result2 = vec3((1.0-baseColor.a) * srcColor.r + baseColor.r, (1.0-baseColor.a) * srcColor.g + baseColor.g, (1.0-baseColor.a) * srcColor.b + baseColor.b);
        vec3 colorMin = min(result.xyz,result2.xyz);
        float blendAlpha =  srcColor.a + baseColor.a - srcColor.a * baseColor.a;
        gl_FragColor = vec4(colorMin.b/blendAlpha,colorMin.g/blendAlpha, colorMin.r/blendAlpha,blendAlpha);
    }
}