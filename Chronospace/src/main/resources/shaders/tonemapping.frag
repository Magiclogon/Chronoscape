#version 330 core
out vec4 FragColor;
in vec2 vUV;

uniform sampler2D screenTexture; 

void main() {
    vec3 col = texture(screenTexture, vUV).rgb;

    // Tonemapping (Reinhard) - squashes HDR values (1.0+) into visible range
    vec3 result = col / (col + vec3(1.0));
    
    FragColor = vec4(result, 1.0);
}