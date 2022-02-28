
uniform sampler2D texture;
uniform sampler2D gradientA;
uniform sampler2D gradientB;
uniform vec2 resolution;
uniform float time;
uniform float a;
uniform float b;
uniform float c;
uniform float d;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p){
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u*u*(3.0-2.0*u);

    float res = mix(
    mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
    mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
    return res*res;
}

vec4 gradientColor(sampler2D gradient, float x){
    return texture2D(gradient, vec2(0., x));
}

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.y;
    int count = int(a);
    float sdfMin = 1000.;
    float range = 1.;
    float t = time * b;
    float amp = 0.5;
    for(int i = 0; i < count; i++){
        vec2 randomPos = -range+2.*range*vec2(noise(vec2(float(i) , float(i + 34.321))));
        sdfMin = min(length(uv - randomPos) - amp, sdfMin);
    }
    vec3 clr = gradientColor(gradientA, smoothstep(c, d, sdfMin)).rgb;
    gl_FragColor = vec4(clr, 1.);
}
