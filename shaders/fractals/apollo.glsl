#version 120

uniform sampler2D texture;
uniform bool customGradient;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;
uniform float scaling;
uniform float range;
uniform int   iterations;
uniform float ampBase;
uniform float ampMult;
uniform float offsetX;
uniform float offsetY;

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

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5*resolution.xy) / resolution.y;
    float sum = 0.;
    float amp = ampBase;
    for(int i = 0; i < iterations; i++){
        uv = fract(uv - vec2(offsetX, offsetY)) - 0.5;
        uv *= scaling;
        uv /= dot(uv,uv);
        sum += length(uv)*amp;
        amp *= ampMult;
    }
    float n = smoothstep(0., range, sum);
    vec3 rgb;
    if(customGradient){
        rgb = texture2D(gradient, vec2(0.5, n)).rgb;
    }else{
        rgb = palette(1.-n, a,b,c,d);
    }
    gl_FragColor = vec4(rgb,1.);
}


























