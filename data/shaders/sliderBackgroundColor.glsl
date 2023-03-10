
uniform vec2 quadPos;
uniform vec2 quadSize;
uniform float time;
uniform float precisionNormalized;

uniform float hueValue;
uniform float saturationValue;
uniform float brightnessValue;
uniform float alphaValue;

uniform int mode;
    // 0 = hue
    // 1 = sat
    // 2 = brightness
    // 3 = alpha

const float PI = 3.14159;
const float TAU = PI * 2.;


// https://iquilezles.org/www/articles/palettes/palettes.htm

struct colorPoint
{
    float pos;
    vec3 val;
};

colorPoint emptyColorPoint()
{
    return colorPoint(1.1, vec3(1.,0.,0.));
}

float map(float value, float start1, float stop1, float start2, float stop2)
{
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop)
{
    return map(value, start, stop, 0., 1.);
}

mat2 rotate2D(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main(){
    vec2 uv = (gl_FragCoord.xy - quadPos.xy) / quadSize.xy;
    vec4 clr;
    if(mode == 0){  // hue
        float hueRangeMin = hueValue - precisionNormalized;
        float hueRangeMax = hueValue + precisionNormalized;
        float hueVarying = mod(map(uv.x, 0, 1, hueRangeMin, hueRangeMax), 1.); // hue repeats so mod() instead of clamp()
        clr = vec4(hsv2rgb(vec3(hueVarying, max(0.5, saturationValue), max(0.5, brightnessValue))), 1.);
    }
    if(mode == 1){  // sat
        float satRangeMin = saturationValue - precisionNormalized;
        float satRangeMax = saturationValue + precisionNormalized;
        float satVarying = clamp(map(uv.x, 0, 1, satRangeMin, satRangeMax), 0., 1.);
        clr = vec4(hsv2rgb(vec3(hueValue, satVarying, brightnessValue)), 1.);
    }
    if(mode == 2){  // brightness
        float brRangeMin = brightnessValue - precisionNormalized;
        float brRangeMax = brightnessValue + precisionNormalized;
        float brVarying = clamp(map(uv.x, 0, 1, brRangeMin, brRangeMax), 0., 1.);
        clr = vec4(hsv2rgb(vec3(hueValue, saturationValue, brVarying)), 1.);;
    }
    if(mode == 3){  // alpha
        float aRangeMin = alphaValue - precisionNormalized;
        float aRangeMax = alphaValue + precisionNormalized;
        float aVarying = clamp(map(uv.x, 0, 1, aRangeMin, aRangeMax), 0., 1.);
        clr = vec4(hsv2rgb(vec3(hueValue, saturationValue, brightnessValue)), aVarying);
    }
    float checkerboardColumns = 20.;
    float checkerboardRows = 4.001;
    vec3 checkerboard = vec3(0);
    vec2 rep = vec2(10.);
    vec2 id = floor(gl_FragCoord.xy/rep);
    if(mod(id.x + id.y,2.) < 0.001){
        checkerboard += 1.;
    }
    checkerboard *= 0.3;
    gl_FragColor = vec4(mix(checkerboard, clr.rgb, clr.w), 1);
}
