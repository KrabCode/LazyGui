
uniform sampler2D texture;
uniform vec2 resolution;
uniform vec3 targetColor;
uniform float base;
uniform float ramp;

// change neighboring color range to transparent

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 inColor = texture2D(texture, uv).rgb;
    float dotProduct = dot(normalize(targetColor), normalize(inColor));
    float alpha = smoothstep(base, base + ramp, dotProduct);
    gl_FragColor = vec4(inColor, alpha);
}
