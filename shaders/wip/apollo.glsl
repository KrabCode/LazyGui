#version 120
#define pmod(p,a) mod(p - 0.5*a,a) - 0.5*a

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

const float pi = 3.14159;

// pretty gradient by iq

vec3 a = vec3(0.5);
vec3 b = vec3(0.5);
vec3 c = vec3(1);
vec3 d = vec3(0,0.1,0.2);

vec3 palette( in float t, in vec3 a, in vec3 b, in vec3 c, in vec3 d )
{
    return a + b*cos( 6.28318*(c*t+d) );
}

float render(vec2 uv){
    float t = time;
    float scaling = 1.5 + 0.25;
    int iterations = 4;
    for(int i = 0; i < iterations; i++){
        uv = pmod(uv, 1.25);
        uv *= scaling;
        uv /= dot(uv, uv);
    }

    uv /= scaling * iterations;
    return length(uv);
}

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy - .5 * resolution.xy) / resolution.y;
    float t = time;

//    cv *= rotate2d(0.5 * pi * atan(cv.y, cv.x));
    cv.x = abs(cv.x);
    cv.y = abs(cv.y);

    float n = smoothstep(-1., 1., render(cv));

    vec3 clr = palette(n,a,b,c,d);
//    vec3 clr = vec3(n);
    gl_FragColor = vec4(clr, 1);
}


























