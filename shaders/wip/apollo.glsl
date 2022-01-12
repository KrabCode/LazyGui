#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

float render(vec2 uv){
    float t = time;
    float scaling = 2.;
    int iterations = 10;
    for(int i = 0; i < iterations; i++){
        uv = fract(uv);
        uv *= scaling;
        uv /= dot(uv, uv);
        uv /= scaling;
    }
    return sin(uv.x + uv.y);
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy - .5 * resolution.xy) / resolution.y;
    cv.x = abs(cv.x);
    cv.y = abs(cv.y);
    cv *= 0.3;
    float n = smoothstep(0.0, 1.0, render(cv));
    vec3 clr = vec3(n);
    gl_FragColor = vec4(clr, 1);
}



























