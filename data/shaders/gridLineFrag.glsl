uniform vec4 window;
uniform float alpha;
uniform sampler2D texture;
uniform vec2 resolution;

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


varying vec4 vertColor;

void main() {
    gl_FragColor = vec4(vertColor.rgb, min(alpha, vertColor.a));
}