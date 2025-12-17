#version 330 core

out vec4 FragColor;
in vec2 TexCoord;

uniform sampler2D screenTexture;
uniform float uBloomIntensity;
uniform float uBloomThreshold;
uniform int uIsFBO;

void main() {
	vec2 uv = TexCoord;
    if (uIsFBO == 1) uv.y = 1.0 - uv.y; 
    
    vec4 color = texture(screenTexture, TexCoord);
    
    // Extract bright areas
    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    vec3 bloom = vec3(0.0);
    
    if (brightness > uBloomThreshold) {
        bloom = color.rgb * uBloomIntensity;
    }
    
    // Simple box blur for bloom (in real implementation, use two-pass Gaussian)
    vec2 texelSize = 1.0 / textureSize(screenTexture, 0);
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            vec2 offset = vec2(float(x), float(y)) * texelSize;
            vec4 sample = texture(screenTexture, TexCoord + offset);
            float sampleBrightness = dot(sample.rgb, vec3(0.2126, 0.7152, 0.0722));
            if (sampleBrightness > uBloomThreshold) {
                bloom += sample.rgb * 0.04; // 1/25 for 5x5 kernel
            }
        }
    }
    
    FragColor = vec4(color.rgb + bloom, color.a);
}