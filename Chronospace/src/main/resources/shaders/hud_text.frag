#version 330 core

in vec2 vUV;

uniform sampler2D screenTexture;
uniform vec4      uColor;

out vec4 FragColor;

void main() {
    // Atlas is white glyphs on transparent background.
    // Use the red channel as the glyph mask and tint with uColor.
    float mask = texture(screenTexture, vUV).r;
    FragColor  = vec4(uColor.rgb, uColor.a * mask);
}
