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
        swingingLight.dispose(gl);
        floor.dispose(gl);
    }


    // ***************************************************
    /* INTERACTION
     *
     *
     */

    private boolean faceAnimation = false;
    private double savedTime = 0;

    public void startFace() {
        faceAnimation = true;
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

//    public void loweredArms() {
//        stopAnimation();
//        leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
//        leftArmRotate.update();
//        rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
//        rightArmRotate.update();
//    }
//
//    public void raisedArms() {
//        stopAnimation();
//        leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
//        leftArmRotate.update();
//        rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
//        rightArmRotate.update();
//    }

    // ***************************************************
    /* THE SCENE
     * Now define all the methods to handle the scene.
     * This will be added to in later examples.
     */

    private Camera camera;
    private Mat4 perspective;
    private Model floor, wallBack, wallLeft, outside, standPhone, lightTop, standEgg, phone, egg, lightCase, lampTop,
            lampMid, lampBtm, sphere, sphereWhite, sphereBlack;
    private Light swingingLight, light1, light2;
    private SGNode robotRoot;

    private float xPosition = 0;
    private TransformNode rotateUpperLip, rotateLowerLip, robotMoveTranslate, leanForward, turnHead, translateRightPupilOnEye, translateLeftPupilOnEye;

    private void initialise(GL3 gl) {
        createRandomNumbers();
        double elapsedTime = getSeconds() - startTime;
        int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/phone.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
        int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
        int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/black.jpg");
        int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/yellow.jpg");
        int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/door.jpg");
        int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
        int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/white.jpg");

        swingingLight = new Light(gl);
        swingingLight.setCamera(camera);

        light1 = new Light(gl);
        light1.setCamera(camera);

        light2 = new Light(gl);
        light2.setCamera(camera);

        // floor
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
        Mat4 modelMatrix = Mat4Transform.scale(18, 1f, 18);
        floor = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId0);

        // wall back
        material = new Material(new Vec3(0.05f, 0.05f, 0.05f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 0.778125f);
        wallBack = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId8);
        wallBack.setModelMatrix(getMforBackWall());

        // wall left
        wallLeft = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId5);

        // outside
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.parse("08:00")) || now.isAfter(LocalTime.parse("20:00"))) {
            outside = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId4);
        } else {
            outside = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId4);
        }
        outside.setModelMatrix(getMforOutside());

        // stand phone
        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 0, -5), modelMatrix);
        standPhone = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1, textureId2);

        // stand egg
        modelMatrix = Mat4.multiply(Mat4Transform.scale(3, 1, 3), Mat4Transform.translate(0, 0.5f, 0));
        standEgg = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1, textureId2);

        // mobile phone
        mesh = new Mesh(gl, Phone.vertices.clone(), Phone.indices.clone());
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 0.5f),
                Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(5, 1, -5), modelMatrix);
        phone = new Model(gl, camera, swingingLight, light1,shader, material, modelMatrix, mesh, textureId3);

        // egg
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(2, 4, 2), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 1, 0), modelMatrix);
        egg = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId9);

        // light bulb
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        lightTop = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId10);

        //light square
        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        lightCase = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1);

        //light stand top
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.37f, 0.2f, 3), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(6.69f, 4.9f, 0), modelMatrix);
        lampTop = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1);

        //light stand middle
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.25f, 5, 0.25f), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8f, 0, 0), modelMatrix);
        lampMid = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1);

        //light stand bottom
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f, 0.15f, 0.5f), Mat4Transform.translate(0, 0.5f, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8, 0, 0), modelMatrix);
        lampBtm = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId1);

        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        sphere = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId7);
        sphereWhite = new Model(gl, camera, swingingLight,light1, shader, material, modelMatrix, mesh, textureId10);
        sphereBlack = new Model(gl, camera, swingingLight, light1, shader, material, modelMatrix, mesh, textureId6);


        // robot
        float bodyX = 1f;
        float bodyY = 2f;
        float headScale = 1.5f;
        float eyeScale = 0.4f;
        float pupilScale = 0.2f;
        float neckScale = 0.3f;
        float legScale = 0.5f;
        float mouthScale = 0.5f;

        robotRoot = new NameNode("root");
        robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));

        TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(-4,legScale,0));

        NameNode body = new NameNode("body");
        Mat4 m = Mat4Transform.scale(bodyX,bodyY,bodyX);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode bodyTransform = new TransformNode("body transform", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

        NameNode head = new NameNode("head");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode headTransform = new TransformNode("head transform", m);
        ModelNode headShape = new ModelNode("Sphere(head)", sphere);

        NameNode eyeLeft = new NameNode("eyeLeft");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode eyeLeftTransform = new TransformNode("eyeLeft transform", m);
        ModelNode eyeLeftShape = new ModelNode("Sphere(eyeLeft)", sphereWhite);

        NameNode eyeRight = new NameNode("eyeRight");
        TransformNode eyeRightTransform = new TransformNode("eyeRight transform", m);
        ModelNode eyeRightShape = new ModelNode("Sphere(eyeRight)", sphereWhite);

        NameNode pupilLeft = new NameNode("pupilLeft");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(pupilScale,pupilScale,pupilScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode pupilLeftTransform = new TransformNode("pupilLeft transform", m);
        ModelNode pupilLeftShape = new ModelNode("Sphere(pupilLeft)", sphereBlack);

        NameNode pupilRight = new NameNode("pupilRight");
        TransformNode pupilRightTransform = new TransformNode("pupilRight transform", m);
        ModelNode pupilRightShape = new ModelNode("Sphere(pupilRight)", sphereBlack);

        NameNode upperMouth = new NameNode("upper mouth");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(mouthScale,mouthScale/3,mouthScale*3));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode upperMouthTransform = new TransformNode("upperMouth transform", m);
        ModelNode upperMouthShape = new ModelNode("Sphere(upperMouth)", sphereWhite);

        NameNode lowerMouth = new NameNode("lower mouth");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(mouthScale,mouthScale/3,mouthScale*3));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode lowerMouthTransform = new TransformNode("lowerMouth transform", m);
        ModelNode lowerMouthShape = new ModelNode("Sphere(lowerMouth)", sphereWhite);

        NameNode neck = new NameNode("neck");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(neckScale,neckScale,neckScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode neckTransform = new TransformNode("neck transform", m);
        ModelNode neckShape = new ModelNode("Sphere(neck)", sphere);

        NameNode leg = new NameNode("leg");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(legScale,legScale,legScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode legTransform = new TransformNode("leg transform", m);
        ModelNode legShape = new ModelNode("Sphere(leg)", sphere);

        TransformNode translateOnTopBody
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,bodyY,0));

        TransformNode translateOnTopNeck
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,bodyY + neckScale,0));

        TransformNode translateRightEyeOnHead
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0.2f,headScale/2 , headScale/2.5f));

        TransformNode translateLeftEyeOnHead
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(-0.2f,headScale/2, headScale/2.5f));

        translateRightPupilOnEye = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,eyeScale/4 , 0.15f));

        translateLeftPupilOnEye = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,eyeScale/4 , 0.15f));

        TransformNode translateMouth
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,headScale/2.5f, headScale/2));

        TransformNode translateLegUnderBody
                = new TransformNode("translate(0,bodyY,0)",Mat4Transform.translate(0,-bodyY/4, 0));

        rotateUpperLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(-10));

        rotateLowerLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(10));

        leanForward = new TransformNode("lean forward",Mat4Transform.rotateAroundX(0));

        turnHead = new TransformNode("lean forward",Mat4Transform.rotateAroundX(0));

        robotRoot.addChild(robotMoveTranslate);
            robotMoveTranslate.addChild(robotTranslate);
                robotTranslate.addChild(leanForward);
                    leanForward.addChild(body);
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
        swingingLight.setPosition(getLightPosition());  // changing light position each frame
        light1.setPosition(getLightPosition2());  // changing light position each frame
        light2.setPosition(getLightPosition2());  // changing light position each frame

        light1.render(gl);

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

        if (faceAnimation){
            updateMouth();
            updatePupils();
        }
        robotRoot.draw(gl);
    }

    private void updateLeftArm() {
        double elapsedTime = getSeconds()-startTime;
        float rotateAngle = (float)Math.sin(elapsedTime);
        robotMoveTranslate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
        robotMoveTranslate.update();
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

    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds() - startTime;
        float x = 5f;
        float y = 0.5f * (float) (Math.sin(elapsedTime));
        float z = 2 * (float) (Math.sin(elapsedTime));
        return new Vec3(x, Math.abs(y) + 3.5f, -z);
//        return new Vec3(5f,3.4f,5f);
    }

    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition2() {
        return new Vec3(8, 2, 8);
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
