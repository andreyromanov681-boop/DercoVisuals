#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform vec2 location, rectSize;
uniform vec4 color;
uniform float radius;

float roundedBoxSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b + r, 0.0)) - r;
}

void main() {
    float alpha = roundedBoxSDF(gl_FragCoord.xy - location - (rectSize / 2.0), rectSize / 2.0, radius);
    float smoothedAlpha = 1.0 - smoothstep(0.0, 1.0, alpha);
    fragColor = vec4(color.rgb, color.a * smoothedAlpha);
}
