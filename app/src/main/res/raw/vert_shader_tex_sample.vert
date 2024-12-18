attribute vec3 v_Position;
attribute vec2 iTexSrcCoordinate;
attribute vec2 iTexBaseCoordinate;
uniform mat3 uMVPMatrix;
varying vec2 v_TexSrcCoord;
varying vec2 v_TexBaseCoord;
void main(){
    vec3 translatePos = uMVPMatrix * vec3(v_Position.x, v_Position.y , 1.0);
    gl_Position = vec4(translatePos.x,translatePos.y,0.0,1.0);
    v_TexSrcCoord = iTexSrcCoordinate;
    v_TexBaseCoord = iTexBaseCoordinate;
}