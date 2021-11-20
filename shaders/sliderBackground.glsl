
uniform vec2 quadPos;
uniform vec2 quadSize;
uniform float time;
uniform float scrollX;
const float PI = 3.14159;
const float TAU = PI * 2.;

vec4 lerp4(vec4 a, vec4 b, float amt){
    return vec4(
        mix(a.x,b.x, amt),
        mix(a.y,b.y, amt),
        mix(a.z,b.z, amt),
        mix(a.w,b.w, amt)
    );
}

void main(){
    vec2 uv = (gl_FragCoord.xy - quadPos.xy) / quadSize.xy;
    float divisions = 1.0;
    float x = uv.x * divisions + scrollX * 0.001;
    float y = 0;
    float sinewave = sin(x * TAU + y);
    float coswave  = cos(x * TAU + y);
    float transition = 0.1;
//    sinewave = smoothstep(-transition, transition, sinewave);
    vec4 bgCol = vec4(0.3+0.3*sinewave, 0.2+0.2*min(sinewave, coswave), 0.3+0.3*coswave, 1);
    gl_FragColor = bgCol;
}