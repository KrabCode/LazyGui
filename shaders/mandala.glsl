uniform sampler2D texture;
uniform vec2 resolution;
uniform float radius;
uniform float freq;
uniform float amp;
uniform vec3 color;

// TODO WIP
void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec3 background = texture2D(texture, uv).rgb;
    float dist = length(cv);
    float theta = atan(cv.y, cv.x);
    vec3 outputColor = background;
    if(dist < radius){
        outputColor = color;
    }
    gl_FragColor = vec4(outputColor, 1.);
}