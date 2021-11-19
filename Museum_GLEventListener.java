import gmaths.*;
import com.jogamp.opengl.*;

/**
 * @author Dr Steve Maddock
 * @author Kamil Topolewski (unauthored bits)
 */
public class Museum_GLEventListener implements GLEventListener {

    Museum_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(4f, 14f, 12f));
    }

    /**
     * Initialization
     * @author Dr Steve Maddock
     */
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        initialise(gl);
        appStartTime = getSeconds();
    }

    /**
     * Called to indicate the drawing surface has been moved and/or resized
     * @author Dr Steve Maddock
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        float aspect = (float) width / (float) height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    /**
     * Draw
     * @author Dr Steve Maddock
     */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /* Clean up memory, if necessary */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        swingingLight.dispose(gl);
        generalLight1.dispose(gl);
        generalLight2.dispose(gl);
        room.floor.dispose(gl);
        room.wallBack.dispose(gl);
        room.wallLeft.dispose(gl);
        room.outside.dispose(gl);
        lightTop.dispose(gl);
        lampTop.dispose(gl);
        lampMid.dispose(gl);
        lampBtm.dispose(gl);
        mobilePhone.dispose(gl);
        standPhone.dispose(gl);
        standEgg.dispose(gl);
        egg.dispose(gl);
        lightCase.dispose(gl);
        roboDuck.sphereYellow.dispose(gl);
        roboDuck.sphereWhite.dispose(gl);
        roboDuck.sphereBlack.dispose(gl);
        roboDuck.sphereOrange.dispose(gl);
    }

    /* ON/OFF for lights and animations */
    private boolean faceAnimation = true, light1on, light2on, spotLight;
    static boolean moveAnimation;
    void changeFaceAnimation() {
        this.faceAnimation = !faceAnimation;
    }
    void changeLight1() {
        this.light1on = !light1on;
    }
    void changeLight2() {
        this.light2on = !light2on;
    }
    void changeSpotLight() {
        this.spotLight = !spotLight;
    }
    void changeMoveAnimation() {
        moveAnimation = !moveAnimation;
    }

    // ***************************************************
    /* THE SCENE
     * Now define all the methods to handle the scene.
     */
    private Camera camera;
    private Model standPhone, lightTop, standEgg, mobilePhone, egg, lightCase, lampTop,
            lampMid, lampBtm;
    private Light swingingLight, generalLight1, generalLight2;
    Robot roboDuck;
    private Room room;

    private void initialise(GL3 gl) {
        int[] woodBox = TextureLibrary.loadTexture(gl, "textures/wooden_box.jpg");
        int[] woodBoxSpecular = TextureLibrary.loadTexture(gl, "textures/wooden_box_specular.jpg");
        int[] phoneScreen = TextureLibrary.loadTexture(gl, "textures/phone.jpg");
        int[] white = TextureLibrary.loadTexture(gl, "textures/white.jpg");
        int[] eggBlue = TextureLibrary.loadTexture(gl, "textures/egg_blue.jpg");
        int[] eggBlueSpecular = TextureLibrary.loadTexture(gl, "textures/egg_blue_specular.jpg");

        swingingLight = new Light(gl);
        swingingLight.setCamera(camera);

        generalLight1 = new Light(gl);
        generalLight1.setCamera(camera);

        generalLight2 = new Light(gl);
        generalLight2.setCamera(camera);

        Mat4 modelMatrix;
        Mat4 initTranslate = Mat4Transform.translate(0, 0.5f, 0);

        Material shiny = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

        Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Mesh phone = new Mesh(gl, Phone.vertices.clone(), Phone.indices.clone());
        Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");

        // stand phone
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 0, -6), modelMatrix);
        standPhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // mobile phone
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 0.5f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 1, -6), modelMatrix);
        mobilePhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, shiny, modelMatrix, phone, phoneScreen,phoneScreen);

        // stand egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        standEgg = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 2), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 1, 0), modelMatrix);
        egg = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, sphere, eggBlue,eggBlueSpecular);

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

        room = new Room(gl, camera, swingingLight, generalLight1, generalLight2);

        // robot init
        roboDuck = new Robot(gl, camera, swingingLight, generalLight1, generalLight2);
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        swingingLight.setPosition(getSwingLightPosition());  // changing light position each frame
        generalLight1.setPosition(getGeneralLightPosition1());  // changing light position each frame
        generalLight2.setPosition(getGeneralLightPosition2());  // changing light position each frame

        room.floor.render(gl);
        room.wallBack.render(gl);
        room.outside.render(gl);

        // left wall grid (window)
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                if (!(i == 2 && j == 2)) {
                    room.wallLeft.setModelMatrix(getMforLeftWall(i, j));
                    room.wallLeft.render(gl);
                }
            }
        }

        standEgg.render(gl);
        egg.render(gl);
        standPhone.render(gl);
        mobilePhone.render(gl);
        lightTop.render(gl);
        lightTop.setModelMatrix(getMforLamp());
        lightCase.render(gl);
        lightCase.setModelMatrix(getMforLampCase());
        lampTop.render(gl);
        lampMid.render(gl);
        lampBtm.render(gl);

        if (light1on) generalLight1.switchOffLight();
        else generalLight1.switchOnLight();

        if (light2on) generalLight2.switchOffLight();
        else generalLight2.switchOnLight();

        if (spotLight) swingingLight.switchOffLight();
        else swingingLight.switchOnLight();

        if(moveAnimation) roboDuck.animateRobot();

        if (faceAnimation){
            roboDuck.updateMouth();
            roboDuck.updatePupils();
        }
        roboDuck.robotRoot.draw(gl);
    }

    private Vec3 getSwingLightPosition() {
        double elapsedTime = getSeconds() - appStartTime;
        float y = 0.5f * (float) (Math.sin(elapsedTime));
        float z = 2 * (float) (Math.sin(elapsedTime));
        return new Vec3(6.5f, Math.abs(y) + 3.5f, -z);
    }

    private Vec3 getGeneralLightPosition1() {
        return new Vec3(-6, 10, -6);
    }

    private Vec3 getGeneralLightPosition2() {
        return new Vec3(6, 10, 6);
    }

    private Mat4 getMforLeftWall(int i, int j) {
        float sizeX = 6f;
        float sizeZ = 4f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, 1f, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-9f, sizeZ * j - 2f, i * sizeX - 2 * sizeX), modelMatrix);
        return modelMatrix;
    }

    private Mat4 getMforLamp() {
        double elapsedTime = getSeconds() - appStartTime;
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

    private Mat4 getMforLampCase() {
        double elapsedTime = getSeconds() - appStartTime;
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


    // ***************************************************
    /* TIME
    * @author Dr Steve Maddoc
     */
    private double appStartTime;
    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
