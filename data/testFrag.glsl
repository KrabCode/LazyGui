uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform float alpha;

void main(){
    vec2 ov = gl_FragCoord.xy / resolution.xy;
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec3 col = 0.5 + 0.5 * cos(time+uv.xyx+vec3(0,2,4));
    vec3 original = texture(texture, ov).rgb;
    gl_FragColor = vec4(mix(original, col, alpha), 1.);
}