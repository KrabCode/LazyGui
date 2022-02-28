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
    return length(uv.xy) - radius;
}

float sdLine(float x){
    return abs(x);
}

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.y;
    float accumulatingSDF = 1000.;
    vec3 p = vec3(uv.xy, 1.);
    for(int i = 0; i < iterations; i++){
        p /= dot(p.xy, p.xy);
        p *= scaleBase;
        p.xy = fract(p.xy - 0.5) - 0.5;
        accumulatingSDF = min(accumulatingSDF, sdCircle(p.xy / p.z));
    }
    vec3 col;
    // accumulated sdf

    // sdfs at end
    p.xy /= p.z;
//    col += smoothstep(0.001,0.,sdCircle(p.xy));
//    col += ;

//    gl_FragColor = vec4(vec3(col), 1.);
    gl_FragColor = vec4(palette(length(p.xy) * range, a,b,c,d), 1.);
}


























