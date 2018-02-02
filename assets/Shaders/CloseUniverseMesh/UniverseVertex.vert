uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_ViewMatrix;

attribute vec3 inPosition;

varying vec4 cameraWorldPosition;
varying vec4 objectWorldPosition;
varying vec4 objectCameraPosition;
varying float cameraDistanceToCenter;
varying float objectDistanceToCenter;
varying float objectDistanceToCamera;

varying float transparency;
varying vec4 v_color;

void main()
{
    objectWorldPosition = g_WorldMatrix * vec4(inPosition, 1.0);
    objectDistanceToCenter = distance(objectWorldPosition, vec4(0.0, 0.0, 0.0, 0.0));

    objectCameraPosition = g_WorldViewMatrix * vec4(inPosition, 1.0);
    objectDistanceToCamera = distance(objectCameraPosition,vec4(1.0, 1.0, 1.0, 1.0));
    
    transparency = 1.0 - (objectDistanceToCamera / 1000000.0);

    v_color = vec4(1.0, 1.0, 1.0, transparency);

    if (objectDistanceToCamera > 500.0){
        gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    }
}