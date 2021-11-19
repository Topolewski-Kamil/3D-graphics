import gmaths.*;
import com.jogamp.opengl.*;
import java.time.LocalTime;


/**
 * @author Dr Steve Maddock
 * @author Kamil Topolewski (unauthored bits)
 */
public class Museum_GLEventListener implements GLEventListener {

    public Museum_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(4f, 12f, 18f));
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
        startTime = getSeconds();
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
        floor.dispose(gl);
        generalLight1.dispose(gl);
        generalLight2.dispose(gl);
        wallBack.dispose(gl);
        wallLeft.dispose(gl);
        outside.dispose(gl);
        standPhone.dispose(gl);
        lightTop.dispose(gl);
        wallLeft.dispose(gl);
        standEgg.dispose(gl);
        mobilePhone.dispose(gl);
        egg.dispose(gl);
        lightCase.dispose(gl);
        lampTop.dispose(gl);
        lampMid.dispose(gl);
        lampBtm.dispose(gl);
        sphereYellow.dispose(gl);
        sphereWhite.dispose(gl);
        sphereBlack.dispose(gl);
        sphereOrange.dispose(gl);
    }


    /* ON/OFF for lights and animations */
    private boolean faceAnimation, light1on, light2on, spotLight;
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

    // ***************************************************
    /* THE SCENE
     * Now define all the methods to handle the scene.
     */
    private Camera camera;
    private Model floor, wallBack, wallLeft, outside, standPhone, lightTop, standEgg, mobilePhone, egg, lightCase, lampTop,
            lampMid, lampBtm, sphereYellow, sphereWhite, sphereBlack,sphereOrange;
    private Light swingingLight, generalLight1, generalLight2;
    private SGNode robotRoot;

    private TransformNode rotateUpperLip, rotateLowerLip, robotMoveTranslate, leanBody, turnHead, translateRightPupilOnEye, translateLeftPupilOnEye;

    private void initialise(GL3 gl) {
        createRandomNumbers();
        int[] floorWood = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
        int[] woodBox = TextureLibrary.loadTexture(gl, "textures/wooden_box.jpg");
        int[] woodBoxSpecular = TextureLibrary.loadTexture(gl, "textures/wooden_box_specular.jpg");
        int[] phoneScreen = TextureLibrary.loadTexture(gl, "textures/phone.jpg");
        int[] clouds = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
        int[] wallWhite = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        int[] black = TextureLibrary.loadTexture(gl, "textures/black.jpg");
        int[] yellow = TextureLibrary.loadTexture(gl, "textures/yellow.jpg");
        int[] wallWithDoor = TextureLibrary.loadTexture(gl, "textures/door.jpg");
        int[] orange = TextureLibrary.loadTexture(gl, "textures/orange.jpg");
        int[] white = TextureLibrary.loadTexture(gl, "textures/white.jpg");
        int[] moon = TextureLibrary.loadTexture(gl, "textures/moon.jpg");
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
        Material whiteRubber = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 0.778125f);
        Material polishedBronze = new Material(new Vec3(0.25f, 0.148f, 0.06475f), new Vec3(0.4f, 0.2368f, 0.1036f), new Vec3(0.774597f, 0.458561f, 0.200621f), 76.8f);
        Material yellowCopper = new Material(new Vec3(0.19125f, 0.0735f, 0.0225f), new Vec3(0.7038f, 0.27048f, 0.0828f), new Vec3(0.256777f, 0.137622f, 0.086014f), 0.778125f);
        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

        Mesh twoTriangles = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Mesh phone = new Mesh(gl, Phone.vertices.clone(), Phone.indices.clone());
        Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

        Shader floorShader = new Shader(gl, "vs_tt_05.txt", "fs_floor.txt");
        Shader wallShader = new Shader(gl, "vs_tt_05.txt", "fs_wall.txt");
        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");

        // floor
        modelMatrix = Mat4Transform.scale(18, 1f, 18);
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

        // stand phone
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 0, -5), modelMatrix);
        standPhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // stand egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), initTranslate);
        standEgg = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        // mobile phone
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 0.5f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 1, -5), modelMatrix);
        mobilePhone = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, shiny, modelMatrix, phone, phoneScreen);

        // egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 2), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 1, 0), modelMatrix);
        egg = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, sphere, eggBlue,eggBlueSpecular);

        // light bulb
        lightTop = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, shiny, modelMatrix, sphere, white);

        //light square
        lightCase = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand top
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.37f, 0.2f, 3), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(6.69f, 4.9f, 0), modelMatrix);
        lampTop = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand middle
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.25f, 5, 0.25f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8f, 0, 0), modelMatrix);
        lampMid = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);

        //light stand bottom
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f, 0.15f, 0.5f), initTranslate);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8, 0, 0), modelMatrix);
        lampBtm = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, modelMatrix, cube, woodBox, woodBoxSpecular);


        /* robot elements */
        sphereYellow = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, yellow);
        sphereWhite = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, white);
        sphereBlack = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, black);
        sphereOrange = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, orange);

        float bodyX = 1f;
        float bodyY = 2f;
        float headScale = 1.5f;
        float eyeScale = 0.4f;
        float pupilScale = 0.2f;
        float neckScale = 0.3f;
        float legScale = 0.5f;
        float mouthScale = 0.5f;

        /* robot scene graph transformations */
        robotRoot = new NameNode("root");

        // init robot position
        TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0, legScale,0));

        NameNode body = new NameNode("body");
        Mat4 m = Mat4.multiply(Mat4Transform.scale(bodyX,bodyY,bodyX), initTranslate);
        TransformNode bodyTransform = new TransformNode("body transform", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphereYellow);

        NameNode head = new NameNode("head");
        m = Mat4.multiply(Mat4Transform.scale(headScale,headScale,headScale),initTranslate);
        TransformNode headTransform = new TransformNode("head transform", m);
        ModelNode headShape = new ModelNode("Sphere(head)", sphereYellow);

        NameNode eyeLeft = new NameNode("eyeLeft");
        m = Mat4.multiply(Mat4Transform.scale(eyeScale,eyeScale,eyeScale),initTranslate);
        TransformNode eyeLeftTransform = new TransformNode("eyeLeft transform", m);
        ModelNode eyeLeftShape = new ModelNode("Sphere(eyeLeft)", sphereWhite);

        NameNode eyeRight = new NameNode("eyeRight");
        TransformNode eyeRightTransform = new TransformNode("eyeRight transform", m);
        ModelNode eyeRightShape = new ModelNode("Sphere(eyeRight)", sphereWhite);

        NameNode pupilLeft = new NameNode("pupilLeft");
        m = Mat4.multiply(Mat4Transform.scale(pupilScale,pupilScale,pupilScale), initTranslate);
        TransformNode pupilLeftTransform = new TransformNode("pupilLeft transform", m);
        ModelNode pupilLeftShape = new ModelNode("Sphere(pupilLeft)", sphereBlack);

        NameNode pupilRight = new NameNode("pupilRight");
        TransformNode pupilRightTransform = new TransformNode("pupilRight transform", m);
        ModelNode pupilRightShape = new ModelNode("Sphere(pupilRight)", sphereBlack);

        NameNode upperMouth = new NameNode("upper mouth");
        m = Mat4.multiply(Mat4Transform.scale(mouthScale,mouthScale/3,mouthScale*3), initTranslate);
        TransformNode upperMouthTransform = new TransformNode("upperMouth transform", m);
        ModelNode upperMouthShape = new ModelNode("Sphere(upperMouth)", sphereOrange);

        NameNode lowerMouth = new NameNode("lower mouth");
        m = Mat4.multiply(Mat4Transform.scale(mouthScale,mouthScale/3,mouthScale*3), initTranslate);
        TransformNode lowerMouthTransform = new TransformNode("lowerMouth transform", m);
        ModelNode lowerMouthShape = new ModelNode("Sphere(lowerMouth)", sphereOrange);

        NameNode neck = new NameNode("neck");
        m = Mat4.multiply(Mat4Transform.scale(neckScale,neckScale,neckScale), initTranslate);
        TransformNode neckTransform = new TransformNode("neck transform", m);
        ModelNode neckShape = new ModelNode("Sphere(neck)", sphereYellow);

        NameNode leg = new NameNode("leg");
        m = Mat4.multiply(Mat4Transform.scale(legScale,legScale,legScale), initTranslate);
        TransformNode legTransform = new TransformNode("leg transform", m);
        ModelNode legShape = new ModelNode("Sphere(leg)", sphereYellow);

        m = Mat4Transform.translate(0,bodyY,0);
        TransformNode translateOnTopBody = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,bodyY + neckScale,0);
        TransformNode translateOnTopNeck = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0.2f,headScale/2 , headScale/2.5f);
        TransformNode translateRightEyeOnHead = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(-0.2f,headScale/2, headScale/2.5f);
        TransformNode translateLeftEyeOnHead = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,eyeScale/4 , 0.15f);
        translateRightPupilOnEye = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,eyeScale/4 , 0.15f);
        translateLeftPupilOnEye = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,headScale/2.5f, headScale/2);
        TransformNode translateMouth = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,-bodyY/4, 0);
        TransformNode translateLegUnderBody = new TransformNode("translate(0,bodyY,0)", m);

        /* robot actions */
        robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,0,-5));
        rotateUpperLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(-10));
        rotateLowerLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(10));
        leanBody = new TransformNode("lean forward",Mat4Transform.rotateAroundX(0));
        turnHead = new TransformNode("lean forward",Mat4Transform.rotateAroundZ(0));

        /* robot scene graph */
        robotRoot.addChild(robotMoveTranslate);
            robotMoveTranslate.addChild(robotTranslate);
                robotTranslate.addChild(leanBody);
                    leanBody.addChild(body);
                        body.addChild(bodyTransform);
                            bodyTransform.addChild(bodyShape);
                        body.addChild(translateOnTopBody);
                            translateOnTopBody.addChild(neck);
                                neck.addChild(neckTransform);
                                    neckTransform.addChild(neckShape);
                        body.addChild(translateOnTopNeck);
                            translateOnTopNeck.addChild(turnHead);
                                turnHead.addChild(head);
                                    head.addChild(headTransform);
                                        headTransform.addChild(headShape);
                                    head.addChild(translateLeftEyeOnHead);
                                            translateLeftEyeOnHead.addChild(eyeLeft);
                                                eyeLeft.addChild(eyeLeftTransform);
                                                    eyeLeftTransform.addChild(eyeLeftShape);
                                                eyeLeft.addChild(pupilLeft);
                                                    pupilLeft.addChild(translateLeftPupilOnEye);
                                                        translateLeftPupilOnEye.addChild(pupilLeftTransform);
                                                            pupilLeftTransform.addChild(pupilLeftShape);
                                    head.addChild(translateRightEyeOnHead);
                                        translateRightEyeOnHead.addChild(eyeRight);
                                            eyeRight.addChild(eyeRightTransform);
                                                eyeRightTransform.addChild(eyeRightShape);
                                    eyeRight.addChild(pupilRight);
                                        pupilRight.addChild(translateRightPupilOnEye);
                                            translateRightPupilOnEye.addChild(pupilRightTransform);
                                                pupilRightTransform.addChild(pupilRightShape);
                                    head.addChild(upperMouth);
                                        upperMouth.addChild(translateMouth);
                                            translateMouth.addChild(rotateUpperLip);
                                                rotateUpperLip.addChild(upperMouthTransform);
                                                        upperMouthTransform.addChild(upperMouthShape);
                                    head.addChild(lowerMouth);
                                        lowerMouth.addChild(translateMouth);
                                            translateMouth.addChild(rotateLowerLip);
                                                rotateLowerLip.addChild(lowerMouthTransform);
                                                    lowerMouthTransform.addChild(lowerMouthShape);
                robotTranslate.addChild(leg);
                    leg.addChild(translateLegUnderBody);
                        translateLegUnderBody.addChild(legTransform);
                            legTransform.addChild(legShape);

        robotRoot.update();  // IMPORTANT - don't forget this
//        robotRoot.print(0, false);
//        System.exit(0);
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        swingingLight.setPosition(getSwingLightPosition());  // changing light position each frame
        generalLight1.setPosition(getGeneralLightPosition1());  // changing light position each frame
        generalLight2.setPosition(getGeneralLightPosition2());  // changing light position each frame

        floor.render(gl);
        wallBack.render(gl);

        // left wall grid
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
        mobilePhone.render(gl);
        lightTop.render(gl);
        lightTop.setModelMatrix(getMforLamp());
        lightCase.render(gl);
        lightCase.setModelMatrix(getMforLampCase());
        lampTop.render(gl);
        lampMid.render(gl);
        lampBtm.render(gl);

        if (faceAnimation){
            updateMouth();
            updatePupils();
        }

        if (light1on) generalLight1.switchOffLight();
        else generalLight1.switchOnLight();

        if (light2on) generalLight2.switchOffLight();
        else generalLight2.switchOnLight();

        if (spotLight) swingingLight.switchOffLight();
        else swingingLight.switchOnLight();

        robotRoot.draw(gl);
    }

    private void updateMouth() {
        double elapsedTime = getSeconds()-startTime;
        float rotateAngle = 10f * (float)Math.sin(elapsedTime*5);
        rotateUpperLip.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
        rotateUpperLip.update();
        rotateLowerLip.setTransform(Mat4Transform.rotateAroundX(-rotateAngle));
        rotateLowerLip.update();
    }

    private void updatePupils() {
        double elapsedTime = getSeconds()-startTime;
        float x = 0.03f * (float)(Math.cos(Math.toRadians(elapsedTime*200)));
        float y = 0.03f * (float)(Math.sin(Math.toRadians(elapsedTime*200)));
        float z = 0.1f;
        translateRightPupilOnEye.setTransform(Mat4Transform.translate(x,0.1f + y,z+0.02f));
        translateRightPupilOnEye.update();
        translateLeftPupilOnEye.setTransform(Mat4Transform.translate(x,-y + 0.1f,z+0.02f));
        translateLeftPupilOnEye.update();
    }

    private Vec3 getSwingLightPosition() {
        double elapsedTime = getSeconds() - startTime;
        float x = 5f;
        float y = 0.5f * (float) (Math.sin(elapsedTime));
        float z = 2 * (float) (Math.sin(elapsedTime));
        return new Vec3(x, Math.abs(y) + 3.5f, -z);
    }

    private Vec3 getGeneralLightPosition1() {
        return new Vec3(-6, 10, -6);
    }

    private Vec3 getGeneralLightPosition2() {
        return new Vec3(6, 10, 6);
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
    * @author Dr Steve Maddoc
     */

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    private int NUM_RANDOMS = 1000;
    private float[] randoms;

    private void createRandomNumbers() {
        randoms = new float[NUM_RANDOMS];
        for (int i = 0; i < NUM_RANDOMS; ++i) {
            randoms[i] = (float) Math.random();
        }
    }

}
