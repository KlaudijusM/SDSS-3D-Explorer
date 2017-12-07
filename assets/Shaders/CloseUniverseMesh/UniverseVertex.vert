uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;

attribute vec3 inPosition;

varying vec4 objectCameraPosition;
varying float objectDistanceToCamera;

varying float transparency;
varying vec4 v_color;

void main()
{
    objectCameraPosition = g_WorldViewMatrix * vec4(inPosition, 1.0);
    objectDistanceToCamera = distance(objectCameraPosition,vec4(1.0, 1.0, 1.0, 1.0));
    
    if (objectDistanceToCamera < 50000.0 && objectDistanceToCamera > 100.0){
        v_color = vec4(1.0, 1.0, 1.0, 1.0);
        gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    }
}