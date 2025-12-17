// debug_uv.frag
#version 330 core
out vec4 FragColor;
in vec2 TexCoord;
uniform sampler2D screenTexture;

void main() {
    // Show UV coordinates as colors (Red = U, Green = V)
    FragColor = vec4(TexCoord.x, TexCoord.y, 0.0, 1.0);
}