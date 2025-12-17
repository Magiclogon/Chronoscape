#version 330 core
out vec4 FragColor;
in vec2 TexCoord;
uniform sampler2D screenTexture;

vec3 sRGBToLinear(vec3 srgb) {
    return pow(srgb, vec3(2.2));
}

void main() {
    vec3 srgb = texture(screenTexture, TexCoord).rgb;
    FragColor = vec4(sRGBToLinear(srgb), 1.0);
}