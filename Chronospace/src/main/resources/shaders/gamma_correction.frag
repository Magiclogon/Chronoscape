// gamma.frag
#version 330 core
out vec4 FragColor;
in vec2 TexCoord;
uniform sampler2D screenTexture;

vec3 gamma(vec3 c, float g) { return pow(c, vec3(1.0/g)); }

void main() {
    vec3 hdr = texture(screenTexture, TexCoord).rgb;
    FragColor = vec4(gamma(hdr, 2.2), 1.0);
}