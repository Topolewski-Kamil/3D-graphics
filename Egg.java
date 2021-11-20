import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
/**
 * @author Kamil Topolewski
 */
class Egg {

    private GL3 gl;
    private Camera camera;
    private Light swingingLight, generalLight1, generalLight2;
    Model standEgg, eggFig;

    Egg(GL3 gl, Camera camera, Light swingingLight, Light generalLight1, Light generalLight2) {
        this.gl = gl;
        this.camera = camera;
        this.swingingLight = swingingLight;
        this.generalLight1 = generalLight1;
        this.generalLight2 = generalLight2;
        buildEgg();
    }

    private void buildEgg() {
        int[] eggBlue = TextureLibrary.loadTexture(gl, "textures/egg_blue.jpg");
        int[] eggBlueSpecular = TextureLibrary.loadTexture(gl, "textures/egg_blue_specular.jpg");
        int[] woodBox = TextureLibrary.loadTexture(gl, "textures/wooden_box.jpg");
        int[] woodBoxSpecular = TextureLibrary.loadTexture(gl, "textures/wooden_box_specular.jpg");

        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        Mat4 initTranslate = Mat4Transform.translate(0, 0.5f, 0);

        Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

        // stand egg
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        standEgg = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 2), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 1, 0), modelMatrix);
        eggFig = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, sphere, eggBlue,eggBlueSpecular);
    }

}