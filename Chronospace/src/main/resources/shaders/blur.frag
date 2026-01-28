#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform int uHorizontal;
uniform float uRadius;  // Blur radius multiplier

// Gaussian weights for 5-tap blur
const float weights[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 texelSize = 1.0 / textureSize(uTexture, 0);
    vec3 result = texture(uTexture, vUV).rgb * weights[0];
    
    if (uHorizontal == 1) {
        // Horizontal blur
        for (int i = 1; i < 5; i++) {
            float offset = float(i) * uRadius;
            result += texture(uTexture, vUV + vec2(texelSize.x * offset, 0.0)).rgb * weights[i];
            result += texture(uTexture, vUV - vec2(texelSize.x * offset, 0.0)).rgb * weights[i];
        }
    } else {
        // Vertical blur
        for (int i = 1; i < 5; i++) {
            float offset = float(i) * uRadius;
            result += texture(uTexture, vUV + vec2(0.0, texelSize.y * offset)).rgb * weights[i];
            result += texture(uTexture, vUV - vec2(0.0, texelSize.y * offset)).rgb * weights[i];
        }
    }
    
    FragColor = vec4(result, 1.0);
}