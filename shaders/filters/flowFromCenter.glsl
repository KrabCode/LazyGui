
uniform sampler2D texture;
uniform vec2 resolution;
uniform float delta;

void main() {
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float a = atan(cv.y, cv.x);
    float x = - delta * cos(a);
    float y = - delta * sin(a);
    vec2 pos = gl_FragCoord.xy + vec2(x, y);
    vec2 uv = pos.xy / resolution.xy;
    vec3 color = texture2D(texture, uv).rgb;
    gl_FragColor = vec4(color.rgb, 1.);
}
