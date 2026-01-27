// bloom_extract.frag
#version 330 core
out vec4 FragColor;
in vec2 vUV;
uniform sampler2D screenTexture;
uniform float threshold;

void main() {
    vec3 col = texture(screenTexture, vUV).rgb;
    // Calculate brightness using perceived luminance
    float brightness = dot(col, vec3(0.2126, 0.7152, 0.0722));
    if(brightness > threshold)
        FragColor = vec4(col, 1.0);
    else
        FragColor = vec4(0.0, 0.0, 0.0, 1.0);
}