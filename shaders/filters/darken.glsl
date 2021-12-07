
uniform sampler2D texture;
uniform vec2 resolution;
uniform float delta;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 color = texture2D(texture, uv).rgb;
    color.rgb -= delta;
    gl_FragColor = vec4(color.rgb, 1.);
}
