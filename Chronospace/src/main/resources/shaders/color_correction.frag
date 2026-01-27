#version 330 core
out vec4 FragColor;
in vec2 vUV;

uniform sampler2D screenTexture;

uniform float brightness;
uniform float contrast;
uniform float saturation;
uniform float exposure;

void main() {
    vec3 col = texture(screenTexture, vUV).rgb;

    // 1. Exposure
    col *= exposure;

    // 2. Brightness
    col += brightness;

    // 3. Contrast
    // Uses 0.5 as the midpoint for scaling
    col = (col - 0.5) * contrast + 0.5;

    // 4. Saturation
    // Standard grayscale weights for human eye perception
    float luminance = dot(col, vec3(0.2126, 0.7152, 0.0722));
    col = mix(vec3(luminance), col, saturation);

    // Prevent negative colors
    FragColor = vec4(max(col, 0.0), 1.0);
}