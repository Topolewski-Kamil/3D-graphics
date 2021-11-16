import gmaths.*;

import com.jogamp.opengl.*;

import java.time.LocalTime;

public class M04_GLEventListener implements GLEventListener {

    private static final boolean DISPLAY_SHADERS = false;

    public M04_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(4f, 12f, 18f));
    }

    // ***************************************************
    /*
     * METHODS DEFINED BY GLEventListener
     */

    /* Initialisation */
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
        startTime = getSeconds();
    }

    /* Called to indicate the drawing surface has been moved and/or resized  */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        float aspect = (float) width / (float) height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    /* Draw */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /* Clean up memory, if necessary */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        light.dispose(gl);
        floor.dispose(gl);
//        cube.dispose(gl);
//        cube2.dispose(gl);
    }


    // ***************************************************
    /* INTERACTION
     *
     *
     */

    private boolean animation = false;
    private double savedTime = 0;

    public void startAnimation() {
        animation = true;
        startTime = getSeconds() - savedTime;
    }

    public void stopAnimation() {
        animation = false;
        double elapsedTime = getSeconds() - startTime;
        savedTime = elapsedTime;
    }

    public void incXPosition() {
        xPosition += 0.5f;
        if (xPosition > 5f) xPosition = 5f;
        updateMove();
    }

    public void decXPosition() {
        xPosition -= 0.5f;
        if (xPosition < -5f) xPosition = -5f;
        updateMove();
    }

    private void updateMove() {
        robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition, 0, 0));
        robotMoveTranslate.update();
    }

    public void loweredArms() {
        stopAnimation();
        leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
        leftArmRotate.update();
        rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
        rightArmRotate.update();
    }

    public void raisedArms() {
        stopAnimation();
        leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
        leftArmRotate.update();
        rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
        rightArmRotate.update();
    }

    // ***************************************************
    /* THE SCENE
     * Now define all the methods to handle the scene.
     * This will be added to in later examples.
     */

    private Camera camera;
    private Mat4 perspective;
    private Model floor, wallBack, wallLeft, outside, standPhone, lightTop, standEgg, phone, egg, lightCase, lampTop,
            lampMid, lampBtm, sphere, cube, cube2;
    private Light light;
    private SGNode robotRoot;

    private float xPosition = 0;
    private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;

    private void initialise(GL3 gl) {
        createRandomNumbers();
        double elapsedTime = getSeconds() - startTime;
        int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/phone.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
        int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/window.jpg");
        int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/cube.jpg");
        int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/door.jpg");
        int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
        int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/white.jpg");


        light = new Light(gl);
        light.setCamera(camera);

        // floor
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
        Mat4 modelMatrix = Mat4Transform.scale(18, 1f, 18);
        floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

        // wall back
        material = new Material(new Vec3(0.05f, 0.05f, 0.05f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 0.778125f);
        wallBack = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId8);
        wallBack.setModelMatrix(getMforBackWall());

        // wall left
        wallLeft = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5);

        // outside
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.parse("08:00")) || now.isAfter(LocalTime.parse("20:00"))) {
            outside = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId4);
        } else {
            outside = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId4);
        }
        outside.setModelMatrix(getMforOutside());

        // stand phone
        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 0, -5), modelMatrix);
        standPhone = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        // stand egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), Mat4Transform.translate(0, 0.5f, 0));
        standEgg = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        // mobile phone
        mesh = new Mesh(gl, Phone.vertices.clone(), Phone.indices.clone());
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 0.5f),
                Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 1, -5), modelMatrix);
        phone = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3);

        // egg
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 2), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 1, 0), modelMatrix);
        egg = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId9);

        // light bulb
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        lightTop = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId10);

        //light square
        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        lightCase = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        //light stand top
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.37f, 0.2f, 3), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(6.69f, 4.9f, 0), modelMatrix);
        lampTop = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        //light stand middle
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.25f, 5, 0.25f), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8f, 0, 0), modelMatrix);
        lampMid = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        //light stand bottom
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f, 0.15f, 0.5f), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8, 0, 0), modelMatrix);
        lampBtm = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        cube2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh);


        // robot

//        float bodyHeight = 3f;
//        float bodyWidth = 2f;
//        float bodyDepth = 1f;
//        float headScale = 2f;
//        float armLength = 3.5f;
//        float armScale = 0.5f;
//        float legLength = 3.5f;
//        float legScale = 0.67f;
//
//        robotRoot = new NameNode("root");
//        robotMoveTranslate = new TransformNode("robot transform", Mat4Transform.translate(xPosition, 0, 0));
//
//        TransformNode robotTranslate = new TransformNode("robot transform", Mat4Transform.translate(0, legLength, 0));
//
//        NameNode body = new NameNode("body");
//        Mat4 m = Mat4Transform.scale(bodyWidth, bodyHeight, bodyDepth);
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode bodyTransform = new TransformNode("body transform", m);
//        ModelNode bodyShape = new ModelNode("Cube(body)", cube);
//
//        NameNode head = new NameNode("head");
//        m = new Mat4(1);
//        m = Mat4.multiply(m, Mat4Transform.translate(0, bodyHeight, 0));
//        m = Mat4.multiply(m, Mat4Transform.scale(headScale, headScale, headScale));
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode headTransform = new TransformNode("head transform", m);
//        ModelNode headShape = new ModelNode("Sphere(head)", sphere);
//
//        NameNode leftarm = new NameNode("left arm");
//        TransformNode leftArmTranslate = new TransformNode("leftarm translate",
//                Mat4Transform.translate((bodyWidth * 0.5f) + (armScale * 0.5f), bodyHeight, 0));
//        leftArmRotate = new TransformNode("leftarm rotate", Mat4Transform.rotateAroundX(180));
//        m = new Mat4(1);
//        m = Mat4.multiply(m, Mat4Transform.scale(armScale, armLength, armScale));
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode leftArmScale = new TransformNode("leftarm scale", m);
//        ModelNode leftArmShape = new ModelNode("Cube(left arm)", cube2);
//
//        NameNode rightarm = new NameNode("right arm");
//        TransformNode rightArmTranslate = new TransformNode("rightarm translate",
//                Mat4Transform.translate(-(bodyWidth * 0.5f) - (armScale * 0.5f), bodyHeight, 0));
//        rightArmRotate = new TransformNode("rightarm rotate", Mat4Transform.rotateAroundX(180));
//        m = new Mat4(1);
//        m = Mat4.multiply(m, Mat4Transform.scale(armScale, armLength, armScale));
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode rightArmScale = new TransformNode("rightarm scale", m);
//        ModelNode rightArmShape = new ModelNode("Cube(right arm)", cube2);
//
//        NameNode leftleg = new NameNode("left leg");
//        m = new Mat4(1);
//        m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth * 0.5f) - (legScale * 0.5f), 0, 0));
//        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
//        m = Mat4.multiply(m, Mat4Transform.scale(legScale, legLength, legScale));
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode leftlegTransform = new TransformNode("leftleg transform", m);
//        ModelNode leftLegShape = new ModelNode("Cube(leftleg)", cube);
//
//        NameNode rightleg = new NameNode("right leg");
//        m = new Mat4(1);
//        m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth * 0.5f) + (legScale * 0.5f), 0, 0));
//        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
//        m = Mat4.multiply(m, Mat4Transform.scale(legScale, legLength, legScale));
//        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
//        TransformNode rightlegTransform = new TransformNode("rightleg transform", m);
//        ModelNode rightLegShape = new ModelNode("Cube(rightleg)", cube);
//
//        robotRoot.addChild(robotMoveTranslate);
//        robotMoveTranslate.addChild(robotTranslate);
//        robotTranslate.addChild(body);
//        body.addChild(bodyTransform);
//        bodyTransform.addChild(bodyShape);
//        body.addChild(head);
//        head.addChild(headTransform);
//        headTransform.addChild(headShape);
//        body.addChild(leftarm);
//        leftarm.addChild(leftArmTranslate);
//        leftArmTranslate.addChild(leftArmRotate);
//        leftArmRotate.addChild(leftArmScale);
//        leftArmScale.addChild(leftArmShape);
//        body.addChild(rightarm);
//        rightarm.addChild(rightArmTranslate);
//        rightArmTranslate.addChild(rightArmRotate);
//        rightArmRotate.addChild(rightArmScale);
//        rightArmScale.addChild(rightArmShape);
//        body.addChild(leftleg);
//        leftleg.addChild(leftlegTransform);
//        leftlegTransform.addChild(leftLegShape);
//        body.addChild(rightleg);
//        rightleg.addChild(rightlegTransform);
//        rightlegTransform.addChild(rightLegShape);
//
//        robotRoot.update();  // IMPORTANT - don't forget this
        //robotRoot.print(0, false);
        //System.exit(0);
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        light.setPosition(getLightPosition());  // changing light position each frame
//        light.render(gl);

        floor.render(gl);
        wallBack.render(gl);
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                if (!(i == 2 && j == 2)) {
                    wallLeft.setModelMatrix(getMforLeftWall(i, j));
                    wallLeft.render(gl);
                }
            }
        }
        outside.render(gl);

        standEgg.render(gl);
        egg.render(gl);

        standPhone.render(gl);
        phone.render(gl);

        lightTop.render(gl);
        lightTop.setModelMatrix(getMforLamp());

        lightCase.render(gl);
        lightCase.setModelMatrix(getMforLampCase());

        lampTop.render(gl);
        lampMid.render(gl);
        lampBtm.render(gl);

//    if (animation) updateLeftArm();
//    robotRoot.draw(gl);
    }

    private void updateLeftArm() {
        double elapsedTime = getSeconds() - startTime;
        float rotateAngle = 180f + 90f * (float) Math.sin(elapsedTime);
        leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
        leftArmRotate.update();
    }

    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds() - startTime;
        float x = 5f;
        float y = 0.5f * (float) (Math.sin(elapsedTime));
        float z = 2 * (float) (Math.sin(elapsedTime));
        return new Vec3(x, Math.abs(y) + 3.5f, -z);
//        return new Vec3(5f,3.4f,5f);
    }


    // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
    private Mat4 getMforBackWall() {
        float sizeX = 18f;
        float sizeZ = 12f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, 1f, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, sizeZ * 0.5f, -sizeX * 0.5f), modelMatrix);
        return modelMatrix;
    }

    // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
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

    // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
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

    // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
    private Mat4 getMforLamp() {
        double elapsedTime = getSeconds() - startTime;
        float sizeX = 0.3f;
        float sizeY = 0.5f;
        float sizeZ = 0.3f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, sizeY, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(180), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(20 * (float) Math.sin(elapsedTime)), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5f, 5f, 0), modelMatrix);
        return modelMatrix;
    }

    // As the transforms do not change over time for this object, they could be stored once rather than continually being calculated
    private Mat4 getMforLampCase() {
        double elapsedTime = getSeconds() - startTime;
        float sizeX = 0.37f;
        float sizeY = 0.37f;
        float sizeZ = 0.4f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sizeX, sizeY, sizeZ), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(180), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(20 * (float) Math.sin(elapsedTime)), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5f, 5.1f, 0), modelMatrix);
        return modelMatrix;
    }


    // ***************************************************
    /* TIME
     */

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    // ***************************************************
    /* An array of random numbers
     */

    private int NUM_RANDOMS = 1000;
    private float[] randoms;

    private void createRandomNumbers() {
        randoms = new float[NUM_RANDOMS];
        for (int i = 0; i < NUM_RANDOMS; ++i) {
            randoms[i] = (float) Math.random();
        }
    }

}
