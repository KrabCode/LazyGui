
uniform sampler2D texture;
uniform vec2 resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 color = texture2D(texture, uv).rgb;

    gl_FragColor = vec4(vec3(1.) - color.rgb, 1.);
}
