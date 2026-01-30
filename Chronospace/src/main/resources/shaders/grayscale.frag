#version 330 core
out vec4 FragColor;

in vec2 vUV;
uniform sampler2D screenTexture;

void main()
{
    vec3 color = texture(screenTexture, vUV).rgb;

    // Perceptual luminance (better than simple average)
    float gray = dot(color, vec3(0.299, 0.587, 0.114));

    FragColor = vec4(vec3(gray), 1.0);
}
