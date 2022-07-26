attribute vec4 vPosition;
attribute vec2 vCoord;
uniform mat4 vMatrix;
varying vec2 textureCoordinate;

void main(){
    gl_Position = vec4(vPosition.x,-vPosition.y,vPosition.z,1.0f);
    textureCoordinate = vCoord;
}