
uniform sampler2D texture;
uniform vec2 resolution;

vec3 antiAlias(vec2 uv){
    float pixelFraction = (1./resolution.x)/3.0;
    vec2 aa = vec2(-pixelFraction, pixelFraction);
    vec3 c1 = texture2D(texture, uv+aa.xx).rgb;
    vec3 c2 = texture2D(texture, uv+aa.xy).rgb;
    vec3 c3 = texture2D(texture, uv+aa.yx).rgb;
    vec3 c4 = texture2D(texture, uv+aa.yy).rgb;
    return (c1+c2+c3+c4) / 4.;
}

void main(void) {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 clr = antiAlias(uv);
    gl_FragColor = vec4(clr.rgb, 1);
}