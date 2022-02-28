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
uniform float power;

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

float sdCircle(vec2 uv, float r){
    return length(uv.xy) - r;
}

float sdLine(float x){
    return abs(x);
}

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.y;
    float accumulatingSDF = 1000.;
    vec3 p = vec3(uv.xy, 1.);
    float amp = ampBase;
    float t = time *0.15;
    float scale = scaleBase + 0.1*sin(t);
    float r = radius + 0.15 * sin(time);
    for(int i = 0; i < iterations; i++){
        p /= dot(p.xy, p.xy);
        p *= scale;
        p.xy = fract(p.xy - 0.5) - 0.5;
        accumulatingSDF = min(accumulatingSDF, sdCircle(p.xy, r));
        scale *= scaleMult + .1*sin(t);
    }

    float n;

    p.xy /= p.z;
    n += length(p.xy) * range;
    n += smoothstep(0.1, 0.0, accumulatingSDF);
    //    gl_FragColor = vec4(vec3(col), 1.);
    vec3 col = palette(pow(n, power), a,b,c,d);
//    vec3 col = vec3(1.-n);
    gl_FragColor = vec4(col, 1.);
}


























