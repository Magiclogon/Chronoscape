#version 330 core
out vec4 FragColor;
in vec2 vUV;
uniform sampler2D screenTexture;
uniform float gamma = 2.2;

void main() {
    vec3 col = texture(screenTexture, vUV).rgb;
    // Map linear to gamma space: color^(1/gamma)
    vec3 corrected = pow(col, vec3(1.0 / gamma));
    FragColor = vec4(corrected, 1.0);
}