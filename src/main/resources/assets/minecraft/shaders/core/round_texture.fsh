#version 150

uniform vec2 location;
uniform vec2 rectSize;
uniform sampler2D tex;
uniform float radius;
uniform float u;
uniform float v;
uniform float w;
uniform float h;
uniform vec4 tintColor;

in vec2 texCoord0;

out vec4 fragColor;

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 rectHalf = rectSize * 0.5;

    vec2 texCoords = texCoord0;
    texCoords.x = u + texCoords.x * w;
    texCoords.y = v + texCoords.y * h;

    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (texCoord0 * rectSize), rectHalf - radius - 1.0, radius)));
    vec4 color = texture(tex, texCoords);

    color *= tintColor;

    fragColor = vec4(color.rgb, color.a * smoothedAlpha);
}