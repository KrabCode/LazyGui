
uniform sampler2D texture;
uniform vec2 resolution;
uniform float deltaRGB;
uniform float deltaR;
uniform float deltaG;
uniform float deltaB;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 color = texture2D(texture, uv).rgb;
    color.rgb += deltaRGB;
    color += vec3(deltaR,deltaG,deltaB);
    gl_FragColor = vec4(color.rgb, 1.);
}
