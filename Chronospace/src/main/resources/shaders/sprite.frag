#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform float uEmission;
uniform vec3 uEmissionColor;

// Color correction uniforms
uniform vec4 uColorTint;
uniform float uHueShift;
uniform float uSaturation;
uniform float uValue;
uniform float uBrightness;
uniform float uContrast;

// Color replacement
uniform int uUseColorReplacement;
uniform vec3 uTargetColor;
uniform vec3 uReplacementColor;
uniform float uColorTolerance;

// Draw simple quad
uniform bool uUseTexture;
uniform vec4 uColor;

// RGB to HSV conversion
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(
        abs(q.z + (q.w - q.y) / (6.0 * d + e)),
        d / (q.x + e),
        q.x
    );
}

// HSV to RGB conversion
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {

    /* ============================
       SIMPLE COLORED QUAD PATH
       ============================ */
    if (!uUseTexture) {
        vec3 baseColor = uColor.rgb;
        float alpha = uColor.a;

        // Optional emission still works
        vec3 emission = uEmission * uEmissionColor * baseColor;
        vec3 finalColor = baseColor + emission;

        FragColor = vec4(finalColor, alpha);
        return;
    }

    /* ============================
       TEXTURED SPRITE PATH
       ============================ */
    vec4 texColor = texture(uTexture, vUV);

    // Early exit for fully transparent pixels
    if (texColor.a < 0.001) {
        discard;
    }

    vec3 color = texColor.rgb;

    // Apply basic color tint
    color *= uColorTint.rgb;
    float alpha = texColor.a * uColorTint.a;

    // Color replacement (before other effects)
    if (uUseColorReplacement == 1) {
        float colorDistance = distance(color, uTargetColor);
        if (colorDistance < uColorTolerance) {
            float replacementFactor = 1.0 - (colorDistance / uColorTolerance);
            color = mix(color, uReplacementColor, replacementFactor);
        }
    }

    // HSV adjustments
    if (uHueShift != 0.0 || uSaturation != 1.0 || uValue != 1.0) {
        vec3 hsv = rgb2hsv(color);

        hsv.x = fract(hsv.x + uHueShift);
        hsv.y = clamp(hsv.y * uSaturation, 0.0, 1.0);
        hsv.z *= uValue;

        color = hsv2rgb(hsv);
    }

    // Brightness adjustment
    color += vec3(uBrightness);

    // Contrast adjustment
    if (uContrast != 1.0) {
        color = ((color - 0.5) * uContrast) + 0.5;
    }

    color = clamp(color, 0.0, 1.0);

    // Emission / glow
    vec3 emission = uEmission * uEmissionColor * color;
    vec3 finalColor = color + emission;

    FragColor = vec4(finalColor, alpha);
}
