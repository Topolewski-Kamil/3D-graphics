import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * @author Kamil Topolewski
 */
class Phone {

    private GL3 gl;
    private Camera camera;
    private Light swingingLight, generalLight1, generalLight2;
    Model standPhone, mobilePhone;

    Phone(GL3 gl, Camera camera, Light swingingLight, Light generalLight1, Light generalLight2) {
        this.gl = gl;
        this.camera = camera;
        this.swingingLight = swingingLight;
        this.generalLight1 = generalLight1;
        this.generalLight2 = generalLight2;
        buildPhone();
    }

    private void buildPhone() {
        int[] woodBox = TextureLibrary.loadTexture(gl, "textures/wooden_box.jpg");
        int[] woodBoxSpecular = TextureLibrary.loadTexture(gl, "textures/wooden_box_specular.jpg");
        int[] phoneScreen = TextureLibrary.loadTexture(gl, "textures/phone.jpg");

        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Material shiny = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);

        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Mat4 initTranslate = Mat4Transform.translate(0, 0.5f, 0);

        Mesh phone = new Mesh(gl, Cube2.vertices.clone(), Cube2.indices.clone());

        // stand phone
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 0, -6), modelMatrix);
        standPhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // mobile phone
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 0.5f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 1, -6), modelMatrix);
        mobilePhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, shiny, modelMatrix, phone, phoneScreen, phoneScreen);
    }

}