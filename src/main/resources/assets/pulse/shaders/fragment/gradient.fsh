#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 location, rectSize;
uniform vec4 radii;
uniform vec4 topLeftColor, topRightColor, bottomLeftColor, bottomRightColor;

float dither(vec2 coord) {
    float noise = fract(sin(dot(coord, vec2(12.9898, 78.233))) * 43758.5453);
    return (noise - 0.5) / 255.0;
}

float roundedBoxSDF(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 pos = gl_FragCoord.xy - location;
    vec4 color = mix(
        mix(topLeftColor, topRightColor, pos.x / rectSize.x),
        mix(bottomLeftColor, bottomRightColor, pos.x / rectSize.x),
        pos.y / rectSize.y
    );
    color.a *= 1.0 - smoothstep(0.0, 1.0, roundedBoxSDF(pos - (rectSize / 2.0), rectSize / 2.0, radii));
    fragColor = color;
}
