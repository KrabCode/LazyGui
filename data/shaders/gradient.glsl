uniform sampler2D texture;
uniform vec2 resolution;

uniform int directionType;
uniform int blendType;
uniform int colorCount;
const int maxColorCount = 100;
uniform vec4[maxColorCount] colorValues;
uniform float[maxColorCount] colorPositions;
uniform bool wrapAtEdges;

//---------------------------------------------------------------------------------
//--------------------------------Color Functions----------------------------------
//---------------------------------------------------------------------------------
// by nmz: https://www.shadertoy.com/view/XddGRN

#define PI 3.14159365
#define TAU 6.28318531

//Allows to scale the saturation and Value/Lightness of the 2nd color
const float SAT2MUL = 1.0;
const float L2MUL = 1.0;


//const vec3 wref =  vec3(.950456, 1.0, 1.089058);
const vec3 wref =  vec3(1.0, 1.0, 1.0);

#define SMOOTH_HSV
#define ITR 50
#define FAR 8.


const float fov = 1.5;
vec2 mo;
mat2 mm2(in float a){float c = cos(a), s = sin(a);return mat2(c,s,-s,c);}

float sRGB(float t){ return mix(1.055*pow(t, 1./2.4) - 0.055, 12.92*t, step(t, 0.0031308)); }
vec3 sRGB(in vec3 c) { return vec3 (sRGB(c.x), sRGB(c.y), sRGB(c.z)); }

//-----------------Lch-----------------

float xyzF(float t){ return mix(pow(t,1./3.), 7.787037*t + 0.139731, step(t,0.00885645)); }
float xyzR(float t){ return mix(t*t*t , 0.1284185*(t - 0.139731), step(t,0.20689655)); }
vec3 rgb2lch(in vec3 c)
{
    c  *= mat3( 0.4124, 0.3576, 0.1805,
    0.2126, 0.7152, 0.0722,
    0.0193, 0.1192, 0.9505);
    c.x = xyzF(c.x/wref.x);
    c.y = xyzF(c.y/wref.y);
    c.z = xyzF(c.z/wref.z);
    vec3 lab = vec3(max(0.,116.0*c.y - 16.0), 500.0*(c.x - c.y), 200.0*(c.y - c.z));
    return vec3(lab.x, length(vec2(lab.y,lab.z)), atan(lab.z, lab.y));
}

vec3 lch2rgb(in vec3 c)
{
    c = vec3(c.x, cos(c.z) * c.y, sin(c.z) * c.y);

    float lg = 1./116.*(c.x + 16.);
    vec3 xyz = vec3(wref.x*xyzR(lg + 0.002*c.y),
    wref.y*xyzR(lg),
    wref.z*xyzR(lg - 0.005*c.z));

    vec3 rgb = xyz*mat3( 3.2406, -1.5372,-0.4986,
    -0.9689,  1.8758, 0.0415,
    0.0557,  -0.2040, 1.0570);

    return rgb;
}

//cheaply lerp around a circle
float lerpAng(in float a, in float b, in float x)
{
    float ang = mod(mod((a-b), TAU) + PI*3., TAU)-PI;
    return ang*x+b;
}

//Linear interpolation between two colors in Lch space
vec3 lerpLch(in vec3 a, in vec3 b, in float x)
{
    float hue = lerpAng(a.z, b.z, x);
    return vec3(mix(b.xy, a.xy, x), hue);
}

    //-----------------HSV-----------------

    //HSV functions from iq (https://www.shadertoy.com/view/MsS3Wc)
    #ifdef SMOOTH_HSV
vec3 hsv2rgb( in vec3 c )
{
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );

    rgb = rgb*rgb*(3.0-2.0*rgb); // cubic smoothing

    return c.z * mix( vec3(1.0), rgb, c.y);
}
    #else
vec3 hsv2rgb( in vec3 c )
{
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );

    return c.z * mix( vec3(1.0), rgb, c.y);
}
    #endif

//From Sam Hocevar: http://lolengine.net/blog/2013/07/27/rgb-to-hsv-in-glsl
vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

//Linear interpolation between two colors in normalized (0..1) HSV space
vec3 lerpHSV(in vec3 a, in vec3 b, in float x)
{
    float hue = (mod(mod((b.x-a.x), 1.) + 1.5, 1.)-0.5)*x + a.x;
    return vec3(hue, mix(a.yz, b.yz, x));
}

    //---------------Improved RGB--------------

    /*
        The idea behind this function is to avoid the low saturation area in the
        rgb color space. This is done by getting the direction to that diagonal
        and displacing the interpolated	color by it's inverse while scaling it
        by saturation error and desired lightness.

        I find it behaves very well under most circumstances, the only instance
        where it doesn't behave ideally is when the hues are very close	to 180
        degrees apart, since the method I am using to find the displacement vector
        does not compensate for non-curving motion. I tried a few things to
        circumvent this problem but none were cheap and effective enough..
    */

    //Changes the strength of the displacement
    #define DSP_STR 1.5

    //Optimizaton for getting the saturation (HSV Type) of a rgb color
    #if 0
float getsat(vec3 c)
{
    c.gb = vec2(max(c.g, c.b), min(c.g, c.b));
    c.rg = vec2(max(c.r, c.g), min(c.r, c.g));
    return (c.r - min(c.g, c.b)) / (c.r + 1e-7);
}
    #else
//Further optimization for getting the saturation
float getsat(vec3 c)
{
    float mi = min(min(c.x, c.y), c.z);
    float ma = max(max(c.x, c.y), c.z);
    return (ma - mi)/(ma+ 1e-7);
}
    #endif

//Improved rgb lerp
vec3 iLerp(in vec3 a, in vec3 b, in float x)
{
    //Interpolated base color (with singularity fix)
    vec3 ic = mix(a, b, x) + vec3(1e-6,0.,0.);

    //Saturation difference from ideal scenario
    float sd = abs(getsat(ic) - mix(getsat(a), getsat(b), x));

    //Displacement direction
    vec3 dir = normalize(vec3(2.*ic.x - ic.y - ic.z, 2.*ic.y - ic.x - ic.z, 2.*ic.z - ic.y - ic.x));
    //Simple Lighntess
    float lgt = dot(vec3(1.0), ic);

    //Extra scaling factor for the displacement
    float ff = dot(dir, normalize(ic));

    //Displace the color
    ic += DSP_STR*dir*sd*ff*lgt;
    return clamp(ic,0.,1.);
}

vec3 hsb2rgb(in vec3 hsb){
    vec3 rgb = clamp(abs(mod(hsb.x*6.0+
    vec3(0.0, 4.0, 2.0), 6.0)-3.0)-1.0, 0.0, 1.0);
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return hsb.z * mix(vec3(1.0), rgb, hsb.y);
}

float map(float value, float start1, float stop1, float start2, float stop2){
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop){
    return map(value, start, stop, 0., 1.);
}

vec4 lerpByBlendType(vec4 colorA, vec4 colorB, float amt){
    float mixedAlpha = mix(colorA.a, colorB.a, amt);
    if(blendType == 0){
        return mix(colorA, colorB, amt);
    }
    if(blendType == 1){
        return vec4(iLerp(colorA.rgb, colorB.rgb, amt), mixedAlpha);
    }
    if(blendType == 2){
        return vec4(hsv2rgb(lerpHSV(rgb2hsv(colorA.rgb), rgb2hsv(colorB.rgb), smoothstep(0.0, 1.0, amt))), mixedAlpha);
    }
    return vec4(0,0,0,1);
}

float getPosByDirectionType(vec2 uv, vec2 cv){
    if(directionType == 0){
        return uv.x; // HORIZONTAL
    }
    if (directionType == 1) {
        return 1.-uv.y; // VERTICAL
    }
    if (directionType == 2) {
        float maxLength = length(vec2(0.5));
        return map(length(cv), 0., maxLength, 0., 1.); // CIRCULAR
    }
    return 0;
}

ivec2 findClosestNeighboursWrapAware(float pos){
    int lastIndex = colorCount - 1;
    if(pos <= colorPositions[0]){
        return ivec2(lastIndex, 0);
    }
    if(pos >= colorPositions[lastIndex]){
        return ivec2(lastIndex, 0);
    }
    for(int i = 0; i < maxColorCount; i++){
        if(pos >= colorPositions[i] && pos <= colorPositions[i+1]){
            return ivec2(i, i+1);
        }
        if(i == lastIndex){
            return ivec2(0, lastIndex);
        }
    }
    return ivec2(0, lastIndex);
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float pos = clamp(getPosByDirectionType(uv, cv), 0., 1.);
    vec3 debug = ivec3(0);
    ivec2 neighbouringIndexes = findClosestNeighboursWrapAware(pos);
    int leftIndex = neighbouringIndexes.x;
    int rightIndex = neighbouringIndexes.y;
    int lastIndex = colorCount - 1;
    vec4 colorA = vec4(hsb2rgb(colorValues[leftIndex].xyz), colorValues[leftIndex].a);
    vec4 colorB = vec4(hsb2rgb(colorValues[rightIndex].xyz), colorValues[rightIndex].a);
    float posA = colorPositions[leftIndex];
    float posB = colorPositions[rightIndex];
    float normalizedPosBetweenNeighbours = norm(pos, posA, posB);
    bool isWrappable = rightIndex == 0 && leftIndex == lastIndex;
    if (isWrappable && wrapAtEdges) {
        if(pos > colorPositions[lastIndex]){
            normalizedPosBetweenNeighbours = norm(pos, posA , posB + 1.);
        }else{
            normalizedPosBetweenNeighbours = norm(pos, posA - 1., posB);
        }
    }
    vec4 mixedColor = lerpByBlendType(colorA, colorB, clamp(normalizedPosBetweenNeighbours, 0., 1.));
    mixedColor.rgb += debug;
    gl_FragColor = mixedColor;
}