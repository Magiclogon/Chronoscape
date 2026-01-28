#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uSceneTexture;
uniform sampler2D uLightTexture;

void main() {
    vec4 sceneColor = texture(uSceneTexture, vUV);
    vec3 lightColor = texture(uLightTexture, vUV).rgb;
    
    // Multiply scene by lighting
    vec3 finalColor = sceneColor.rgb * lightColor;
    
    FragColor = vec4(finalColor, sceneColor.a);
}