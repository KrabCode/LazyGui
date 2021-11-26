
void main(){
    float checkerboardColumns = 20.;
    float checkerboardRows = 4.001;
    vec3 checkerboard = vec3(0);
    vec2 rep = vec2(10.);
    vec2 id = floor(gl_FragCoord.xy/rep);
    if(mod(id.x + id.y,2.) == 0.){
        checkerboard += 1.;
    }
    checkerboard *= 0.3;
    gl_FragColor = vec4(checkerboard, 1);
}