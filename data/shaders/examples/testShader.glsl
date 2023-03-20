uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform vec4 colorA;
uniform vec4 colorB;
uniform float mixerLow;
uniform float mixerHigh;

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec3 col = mix(colorA.rgb, colorB.rgb, smoothstep(mixerLow, mixerHigh, uv.y));
    gl_FragColor = vec4(col, 1.);
}