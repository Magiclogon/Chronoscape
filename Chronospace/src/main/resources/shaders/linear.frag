#version 330 core

out vec4 FragColor;
in vec2 vUV;

uniform sampler2D texture0;

vec3 srgbToLinear(vec3 c)
{
    return pow(c, vec3(2.2));
}

void main()
{
    vec3 srgbColor = texture(texture0, vUV).rgb;
    vec3 linearColor = srgbToLinear(srgbColor);

    FragColor = vec4(linearColor, 1.0);
}
