#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform vec2 uResolution;
uniform vec2 uLightPos;      // Screen space position
uniform vec3 uLightColor;
uniform float uLightRadius;
uniform float uLightIntensity;
uniform int uLightType;      // 0 = point, 1 = spot, 2 = ambient

void main() {
    if (uLightType == 2) {
        // Ambient light - fill entire screen
        FragColor = vec4(uLightColor * uLightIntensity, 1.0);
        return;
    }
    
    // Convert to screen coordinates
    vec2 pixelPos = vUV * uResolution;
    
    // Calculate distance from light
    float distance = length(pixelPos - uLightPos);
    
    // Attenuation (falloff)
    float attenuation = 1.0 - smoothstep(0.0, uLightRadius, distance);
    attenuation = pow(attenuation, 2.0); // Quadratic falloff
    
    vec3 lightContribution = uLightColor * attenuation * uLightIntensity;
    
    FragColor = vec4(lightContribution, 1.0);
    
}