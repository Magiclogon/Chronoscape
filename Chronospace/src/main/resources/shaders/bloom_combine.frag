#version 330 core
out vec4 FragColor;
in vec2 vUV;

uniform sampler2D bloomBlur;    // Texture Unit 0
uniform sampler2D originalScene; // Texture Unit 1
uniform float intensity;

void main() {
    vec3 sceneCol = texture(originalScene, vUV).rgb;
    vec3 bloomCol = texture(bloomBlur, vUV).rgb;

    // Additive Blend
    vec3 result = sceneCol + (bloomCol * intensity);
  
    FragColor = vec4(result, 1.0);
}