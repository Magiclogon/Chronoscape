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
    //vec3 result = (bloomCol * intensity);

    // Tonemapping (Reinhard) - squashes HDR values (1.0+) into visible range
    result = result / (result + vec3(1.0));
    
    // Final Gamma Correction (Linear -> Gamma)
    //result = pow(result, vec3(1.0 / 2.2));

    FragColor = vec4(result, 1.0);
}