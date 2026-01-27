#version 330 core
out vec4 FragColor;
in vec2 vUV;
uniform sampler2D sceneTexture;
uniform sampler2D bloomBlurTexture;
uniform float bloomIntensity = 1.0;

void main() {             
    vec3 hdrColor = texture(sceneTexture, vUV).rgb;      
    vec3 bloomColor = texture(bloomBlurTexture, vUV).rgb;
    
    // Additive blending
    vec3 result = hdrColor + (bloomColor * bloomIntensity);
    
    // Tonemapping (Reinhard) to bring HDR back to [0,1]
    result = result / (result + vec3(1.0));
    
    FragColor = vec4(result, 1.0);
}