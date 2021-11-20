import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
/**
 * @author Kamil Topolewski
 */
class SpotLightStand {

    private GL3 gl;
    private Camera camera;
    private Light swingingLight, generalLight1, generalLight2;
    Model lightTop, lightCase, lampTop, lampMid, lampBtm;

    SpotLightStand(GL3 gl, Camera camera, Light swingingLight, Light generalLight1, Light generalLight2) {
        this.gl = gl;
        this.camera = camera;
        this.swingingLight = swingingLight;
        this.generalLight1 = generalLight1;
        this.generalLight2 = generalLight2;
        buildSpotLightStand();
    }

    private void buildSpotLightStand() {
        int[] woodBox = TextureLibrary.loadTexture(gl, "textures/wooden_box.jpg");
        int[] woodBoxSpecular = TextureLibrary.loadTexture(gl, "textures/wooden_box_specular.jpg");
        int[] white = TextureLibrary.loadTexture(gl, "textures/white.jpg");

        Mat4 modelMatrix = Mat4Transform.translate(0, 0, 0);
        Mat4 initTranslate = Mat4Transform.translate(0, 0.5f, 0);

        Material shiny = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

        Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");

        // light bulb
        lightTop = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, shiny, modelMatrix, sphere, white);

        //light square
        lightCase = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand top
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.37f, 0.2f, 1.7f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(7.54f, 4.9f, 0), modelMatrix);
        lampTop = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand middle
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.25f, 5, 0.25f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8f, 0, 0), modelMatrix);
        lampMid = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand bottom
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f, 0.15f, 0.5f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8, 0, 0), modelMatrix);
        lampBtm = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);
    }

    Mat4 getMforLamp() {
        double elapsedTime = Museum_GLEventListener.getSeconds() - Museum_GLEventListener.appStartTime;
        float sizeX = 0.3f;
        float sizeY = 0.5f;
        float sizeZ = 0.3f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, sizeY, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(180), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(20 * (float) Math.sin(elapsedTime)), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(6.5f, 5, 0), modelMatrix);
        return modelMatrix;
    }

    Mat4 getMforLampCase() {
        double elapsedTime = Museum_GLEventListener.getSeconds() - Museum_GLEventListener.appStartTime;
        float sizeX = 0.37f;
        float sizeY = 0.37f;
        float sizeZ = 0.4f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, sizeY, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(180), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(20 * (float) Math.sin(elapsedTime)), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(6.5f, 5.1f, 0), modelMatrix);
        return modelMatrix;
    }

}