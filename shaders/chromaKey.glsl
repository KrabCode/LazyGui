
uniform sampler2D texture;
uniform vec2 resolution;
uniform vec3 targetColor;
uniform float thresholdA;
uniform float thresholdB;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 inColor = texture2D(texture, uv).rgb;
    float dotProduct = dot(targetColor, inColor);
    float alpha = smoothstep(thresholdA, thresholdB, dotProduct);
    gl_FragColor = vec4(inColor, alpha);
}
