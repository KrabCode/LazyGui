
uniform vec2 quadPos;
uniform vec2 quadSize;
uniform float time;
uniform float scrollX;
uniform float precisionNormalized;

const float PI = 3.14159;
const float TAU = PI * 2.;
const int colorsPerGradient = 3;


// https://iquilezles.org/www/articles/palettes/palettes.htm

struct colorPoint
{
    float pos;
    vec3 val;
};

colorPoint emptyColorPoint()
{
    return colorPoint(1.1, vec3(1.,0.,0.));
}

float map(float value, float start1, float stop1, float start2, float stop2)
{
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop)
{
    return map(value, start, stop, 0., 1.);
}

int findClosestLeftNeighbourIndex(float pos, colorPoint[colorsPerGradient] gradient)
{
    for(int i = 0; i < colorsPerGradient-1; i++){
        if(pos >= gradient[i].pos && pos <= gradient[i+1].pos){
            return i;
        }
    }
    return 0;
}

vec3 gradientColorAt(float normalizedPos, colorPoint[colorsPerGradient] gradient)
{
    float pos = clamp(normalizedPos, 0., 1.);
    int leftIndex = findClosestLeftNeighbourIndex(pos, gradient);
    int rightIndex = leftIndex + 1;
    colorPoint A = gradient[leftIndex];
    colorPoint B = gradient[rightIndex];
    float normalizedPosBetweenNeighbours = norm(pos, A.pos, B.pos);
    return mix(A.val, B.val, normalizedPosBetweenNeighbours);
}

vec3 hexToRgb(int color)
{
    float rValue = float(color / 256 / 256);
    float gValue = float(color / 256 - int(rValue * 256.0));
    float bValue = float(color - int(rValue * 256.0 * 256.0) - int(gValue * 256.0));
    return vec3(rValue / 255.0, gValue / 255.0, bValue / 255.0);
}

vec3 gammaCorrection(vec3 rgb){
    return pow(smoothstep(0., 1., rgb), vec3(1.0/2.2));
}

vec4 gammaCorrection(vec4 rgba){
    return vec4(gammaCorrection(rgba.rgb), 1.);
}

void main(){
    vec3 edge = hexToRgb(0x282828);
    vec3 center = hexToRgb(0x383838);
    colorPoint[colorsPerGradient] gradient = colorPoint[](
        colorPoint(0.,  edge),
        colorPoint(0.5, center),
        colorPoint(1.,  edge)
    );

    vec2 uv = (gl_FragCoord.xy - quadPos.xy) / quadSize.xy;
    float freq = 0.2 + 2. * precisionNormalized;
    freq *= PI;
    float x = uv.x * freq + scrollX * 0.008;
    float y = - uv.y * freq + uv.x * freq;
    x = 0.5+0.45*sin(x + y ) ;
    vec4 color = vec4(gradientColorAt(x, gradient), 1.);
    gl_FragColor = color;
}