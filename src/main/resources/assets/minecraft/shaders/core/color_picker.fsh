#version 150

uniform vec2 location;
uniform vec2 rectSize;
uniform float radius;
uniform float hue;

in vec2 texCoord;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 rectHalf = rectSize * 0.5;
    vec2 uv = texCoord * rectSize - rectHalf;

    float dist = roundSDF(uv, rectHalf - radius, radius);
    float alpha = 1.0 - smoothstep(0.0, 1.0, dist);

    float saturation = texCoord.x;
    float brightness = 1.0 - texCoord.y;

    vec3 color = hsv2rgb(vec3(hue, saturation, brightness));

    fragColor = vec4(color, alpha);
}