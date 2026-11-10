#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

void main() {
    vec4 texColor = texture(Sampler0, texCoord);
    if (texColor.a < 0.01) discard;
    fragColor = texColor * ColorModulator;
}
