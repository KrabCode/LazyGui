
uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform float speed;
uniform float pixelate;
uniform float freqX;
uniform float freqY;
uniform float offset;
uniform float whitePoint;
uniform float blackPoint;
uniform float alpha;

vec4 hash44(vec4 p4)
{
    p4 = fract(p4  * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    uv += offset;
    uv = floor(uv * pixelate * 1000);
    float t = time * speed;
    float tr = 2.5;
    vec4 p = vec4(uv*vec2(freqX ,freqY), t, 0.);
    float n = length(hash44(p));
    n = smoothstep(blackPoint, whitePoint, n);
    vec3 origColor = texture2D(texture, gl_FragCoord.xy / resolution.xy).rgb;
    vec3 noiseColor = origColor + vec3(n);
    vec3 final = mix(origColor, noiseColor, alpha).rgb;
    gl_FragColor = vec4(final, 1.);
}