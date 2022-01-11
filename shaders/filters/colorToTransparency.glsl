#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform vec3 targetColor;
uniform float distanceLow;
uniform float distanceHigh;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.x = 1.-uv.x;
    uv.y = 1.-uv.y;
    vec3 orig = texture2D(texture, uv).rgb;
    float orthogonality = dot(orig, targetColor);
    float weighedAlpha = smoothstep(orthogonality, distanceLow, distanceHigh);
    gl_FragColor = vec4(orig, weighedAlpha);
}
