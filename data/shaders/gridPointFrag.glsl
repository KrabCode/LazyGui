uniform vec4 window;
uniform float alpha;
uniform sampler2D texture;
uniform vec2 resolution;
uniform bool sdfCropEnabled;
varying vec4 vertColor;

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


float sdBox( in vec2 p, in vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

void main() {
    float m = min(resolution.x, resolution.y);
    vec2 uv = (gl_FragCoord.xy / m);
    uv.y = 1. - uv.y;
    float mult = 1.;
    if(sdfCropEnabled){
        vec4 windowNormalized = vec4(window.xy / m, window.zw / m);
        mult = sdBox(windowNormalized.xy + windowNormalized.zw / 2. - uv , windowNormalized.zw / 2.);
        mult = smoothstep(0.1, 0., mult);
    }
    gl_FragColor = vec4(vertColor.rgb, min(alpha, mult));
}