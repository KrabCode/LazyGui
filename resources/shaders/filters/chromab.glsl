uniform sampler2D texture;
uniform vec2 resolution;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;
uniform float rotation  ;
uniform int steps     ;

// TODO lerp between fromCenter and purely uniform direction

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;

    const float amount =  (intensity / resolution.x) * smoothstep(innerEdge, outerEdge, length(cv));

    const vec2 direction = vec2(1,0)*rotate2d(rotation);

    vec2 offs = vec2(0);
    vec3 col = vec3(0);
    const vec2 chromabUv = uv;

    for(float i = 0.; i < float(steps); i++){
        offs += direction * (amount / steps);
        col.r += texture(texture, chromabUv + offs).r;
        col.g += texture(texture, chromabUv ).g;
        col.b += texture(texture, chromabUv - offs).b;
    }
    col /= steps;

    gl_FragColor = vec4(col, 1.0);
}