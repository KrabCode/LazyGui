#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform float scaleBase;
uniform float scaleMult;
uniform float range;
uniform int   iterations;
uniform float ampBase;
uniform float ampMult;
uniform float offsetX;
uniform float offsetY;
uniform float radius;

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

float sdCircle(vec2 uv){
    return length(uv) - radius;
}

float sdLine(in vec2 p){
    return abs(p.x);
}

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5*resolution.xy) / resolution.y;
    float t = time;
    float sum = 2.8;
    float amp = ampBase;
    float scale = scaleBase;
    float scaleMultTemp = scaleMult + 0.05 * sin(t);
    for(int i = 0; i < iterations; i++){
        uv = fract(uv - 0.5) - 0.5;
        uv *= scale;
        uv /= dot(uv, uv);
        sum = min(sum, sdLine(uv/scale));
        amp *= ampMult;
        scale *= scaleMultTemp;
    }
    vec3 rgb = palette(fract(sum*range), a,b,c,d);
    gl_FragColor = vec4(vec3(smoothstep(0.001, 0., sum)),1.);
}


























