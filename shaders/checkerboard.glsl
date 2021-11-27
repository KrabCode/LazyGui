
uniform vec2 quadPos;
uniform vec2 quadSize;

void main(){
    vec3 checkerboard = vec3(0);
    vec2 rep = vec2(12);
    vec2 windowId = floor((gl_FragCoord.xy - quadPos * vec2(1,-1))/rep);
    vec2 id = windowId;
//    vec2 staticId = floor(gl_FragCoord.xy/rep);
//    id = staticId;
    if(mod(id.x + id.y,2.) == 0.){
        checkerboard += 1.;
    }
    checkerboard *= 0.3;
    gl_FragColor = vec4(checkerboard, 1);
}