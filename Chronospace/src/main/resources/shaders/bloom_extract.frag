#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform float uThreshold;

void main() {
    vec3 color = texture(uTexture, vUV).rgb;
    
    // Calculate brightness (luminance)
    float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722));
    
    // Extract bright areas
    if (brightness > uThreshold) {
        // Smooth extraction using soft threshold
        float contribution = smoothstep(uThreshold, uThreshold + 0.1, brightness);
        FragColor = vec4(color * contribution, 1.0);
    } else {
        // Allow some contribution even below threshold if the color is very saturated
        // This helps emissive colored objects bloom better
        float maxChannel = max(max(color.r, color.g), color.b);
        if (maxChannel > uThreshold * 0.5) {
            FragColor = vec4(color * 0.3, 1.0);
        } else {
            FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }
}