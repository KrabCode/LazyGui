
uniform vec2 resolution;
uniform vec3 color;
uniform sampler2D texture;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 bg = texture2D(texture, uv).rgb;
    gl_FragColor = vec4(bg, 1);
}