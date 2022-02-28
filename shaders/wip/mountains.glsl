
uniform sampler2D texture;
uniform vec2 resolution;

vec3 render(vec2 uv){
    return vec3(0,0,0);
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    gl_FragColor = vec4(render(uv), 1.);
}
