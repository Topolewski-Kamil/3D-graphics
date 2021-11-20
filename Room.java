import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.time.LocalTime;

/**
 * @author Kamil Topolewski
 */
class Room {

    Model floor, wallBack, wallLeft, outside;
    private GL3 gl;
    private Camera camera;
    private Light swingingLight, generalLight1, generalLight2;

    Room(GL3 gl, Camera camera, Light swingingLight, Light generalLight1, Light generalLight2) {
        this.gl = gl;
        this.camera = camera;
        this.swingingLight = swingingLight;
        this.generalLight1 = generalLight1;
        this.generalLight2 = generalLight2;
        buildRoom();
    }

    private void buildRoom() {
        int[] floorWood = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
        int[] clouds = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
        int[] wallWhite = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        int[] wallWithDoor = TextureLibrary.loadTexture(gl, "textures/door.jpg");
        int[] moon = TextureLibrary.loadTexture(gl, "textures/moon.jpg");

        Shader floorShader = new Shader(gl, "vs_tt_05.txt", "fs_floor.txt");
        Shader wallShader = new Shader(gl, "vs_tt_05.txt", "fs_wall.txt");

        Material shiny = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
        Material whiteRubber = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 0.778125f);

        Mesh twoTriangles = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

        // floor
        Mat4 modelMatrix = Mat4Transform.scale(18, 1f, 18);
        floor = new Model(gl, camera, swingingLight, generalLight1, generalLight2, floorShader, shiny, modelMatrix, twoTriangles, floorWood);

        // wall back
        wallBack = new Model(gl, camera, swingingLight, generalLight1, generalLight2, wallShader, whiteRubber, modelMatrix, twoTriangles, wallWithDoor);
        wallBack.setModelMatrix(getMforBackWall());

        // wall left
        wallLeft = new Model(gl, camera, swingingLight, generalLight1, generalLight2, wallShader, whiteRubber, modelMatrix, twoTriangles, wallWhite);

        // outside
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.parse("08:00")) || now.isAfter(LocalTime.parse("20:00")))
            outside = new Model(gl, camera, swingingLight, generalLight1, generalLight2, wallShader, shiny, modelMatrix, twoTriangles, moon);
        else
            outside = new Model(gl, camera, swingingLight, generalLight1, generalLight2, wallShader, shiny, modelMatrix, twoTriangles, clouds);
        outside.setModelMatrix(getMforOutside());

    }

    private Mat4 getMforBackWall() {
        float sizeX = 18f;
        float sizeZ = 12f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, 1f, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, sizeZ * 0.5f, -sizeX * 0.5f), modelMatrix);
        return modelMatrix;
    }

    private Mat4 getMforOutside() {
        float sizeX = 18f;
        float sizeZ = 12f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, 1f, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-11f, sizeZ / 2, 0), modelMatrix);
        return modelMatrix;
    }

    Mat4 getMforLeftWall(int i, int j) {
        float sizeX = 6f;
        float sizeZ = 4f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, 1f, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-9f, sizeZ * j - 2f, i * sizeX - 2 * sizeX), modelMatrix);
        return modelMatrix;
    }
}