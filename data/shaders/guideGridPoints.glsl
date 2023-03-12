uniform vec4 window;
uniform float alpha;
uniform sampler2D texture;
uniform vec2 resolution;
uniform bool sdfCropEnabled;
uniform float sdfCropDistance;
uniform float gridCellSize;
uniform vec3 pointColor;
uniform float pointWeight;
uniform bool shouldCenterPoints;

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


float sdBox(in vec2 p, in vec2 b)
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

float sdPoint(){
    vec2 p = vec2(gl_FragCoord.x, resolution.y-gl_FragCoord.y) - 0.5;
    if(!shouldCenterPoints){
        p += gridCellSize / 2.;
    }
    vec2 v = fract(p / vec2(gridCellSize)) - 0.5;
    float w = (pointWeight / 2.) / gridCellSize;

    return smoothstep(w,  w-(1./gridCellSize), length(v));
}

void main() {
    float m = min(resolution.x, resolution.y);
    vec2 uv = (gl_FragCoord.xy / m);
    uv.y = 1. - uv.y;
    float sdfAlpha = 1.;
    if(sdfCropEnabled){
        vec4 windowNormalized = vec4(window.xy / m, window.zw / m);
        float sdfResult = sdBox(windowNormalized.xy + windowNormalized.zw / 2. - uv , windowNormalized.zw / 2.);
        sdfAlpha = smoothstep(sdfCropDistance/m, 0., sdfResult);
    }
    sdfAlpha *= smoothstep(0., 1., sdPoint());
    gl_FragColor = vec4(pointColor, min(alpha, sdfAlpha));
}