#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D sceneTexture;
uniform sampler2D glowTexture;
uniform float intensity;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 scene = texture(sceneTexture, vUV).rgb;
    vec3 glow = texture(glowTexture, vUV).rgb;
    
    // Increase saturation of the glow
    vec3 glowHSV = rgb2hsv(glow);
    glowHSV.y = min(glowHSV.y * 2, 1.0); // Increase saturation by 50%
    vec3 saturatedGlow = hsv2rgb(glowHSV);
    
    // Additive blend with intensity control
    vec3 result = scene + saturatedGlow * intensity;
    
    FragColor = vec4(result, 1.0);
}