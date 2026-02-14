#version 330 core
out vec4 FragColor;
in vec2 vUV;
uniform sampler2D screenTexture;

void main() {
    vec3 col = texture(screenTexture, vUV).rgb;
    
    FragColor = vec4(col, 1.0);
}