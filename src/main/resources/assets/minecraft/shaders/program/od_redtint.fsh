#version 150

uniform sampler2D DiffuseSampler;
uniform float RedAmount;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 col = texture(DiffuseSampler, texCoord);
    fragColor = vec4(col.r + RedAmount, col.g, col.b, col.a);
}