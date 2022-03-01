#version 120

float luma(vec3 color) { return dot(color, vec3(0.299, 0.587, 0.114)); }

float luma(vec4 color) { return dot(color.rgb, vec3(0.299, 0.587, 0.114)); }

vec4 rgb2cmyki(in vec3 c) { float k = max(max(c.r, c.g), c.b); return min(vec4(c.rgb / k, k), 1.0); }

vec3 cmyki2rgb(in vec4 c) { return c.rgb * c.a; }

vec3 lerpHSV(in vec3 hsv1, in vec3 hsv2, in float rate)
{
    float hue = (mod(mod((hsv2.x-hsv1.x), 1.) + 1.5, 1.)-0.5)*rate + hsv1.x;
    return vec3(hue, mix(hsv1.yz, hsv2.yz, rate));
}


vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
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

vec3 hsv2rgbSmooth( in vec3 hsv )
{
    vec3 rgb = clamp( abs(mod(hsv.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );

    rgb = rgb*rgb*(3.0-2.0*rgb); // cubic smoothing

    return hsv.z * mix( vec3(1.0), rgb, hsv.y);
}


vec3 hueShift(vec3 col, vec3 hsv){
    vec3 h = rgb2hsv(col);
    h.x += hsv.x;

    h.y *= hsv.y;
    h.z *= hsv.z;

    return hsv2rgbSmooth(h);
}