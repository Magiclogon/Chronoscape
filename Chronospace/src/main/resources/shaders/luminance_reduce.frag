#version 330 core
in vec2 vUV;
out float FragLum;

uniform sampler2D uTexture;

void main() {
    vec3 c = texture(uTexture, vUV).rgb;
    FragLum = dot(c, vec3(0.2126, 0.7152, 0.0722)); // perceptual luminance
}
