import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
/**
 * @author Kamil Topolewski
 */
class Robot {

    SGNode robotRoot;
    private TransformNode rotateUpperLip, rotateLowerLip, robotMoveTranslate, leanBody, turnHead, rotateRightPupil, rotateLeftPupil;
    Model sphereYellow, sphereWhite, sphereBlack,sphereOrange;
    private GL3 gl;
    private Camera camera;
    private Light swingingLight, generalLight1, generalLight2;


    Robot(GL3 gl, Camera camera, Light swingingLight, Light generalLight1, Light generalLight2){
        this.gl = gl;
        this.camera = camera;
        this.swingingLight = swingingLight;
        this.generalLight1 = generalLight1;
        this.generalLight2 = generalLight2;
        buildRobot();
    }

    private void buildRobot(){
        float bodyX = 1f;
        float bodyY = 2f;
        float headScale = 1.5f;
        float eyeScale = 0.4f;
        float pupilScale = 0.2f;
        float neckScale = 0.3f;
        float legScale = 0.5f;
        float mouthScale = 0.5f;

        int[] yellow = TextureLibrary.loadTexture(gl, "textures/yellow.jpg");
        int[] black = TextureLibrary.loadTexture(gl, "textures/black.jpg");
        int[] white = TextureLibrary.loadTexture(gl, "textures/white.jpg");
        int[] orange = TextureLibrary.loadTexture(gl, "textures/orange.jpg");

        Material matt = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Shader shaderCube = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Mat4 initTranslate = Mat4Transform.translate(0, 0.5f, 0);

        /* robot elements */
        sphereYellow = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, yellow);
        sphereWhite = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, white);
        sphereBlack = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, black);
        sphereOrange = new Model(gl, camera, swingingLight, generalLight1, generalLight2, shaderCube, matt, null, sphere, orange);

        /* robot scene graph transformations */
        robotRoot = new NameNode("root");

        // init robot position
        TransformNode robotTranslate = new TransformNode("robot transform", Mat4Transform.translate(0, legScale,0));

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

        m = Mat4Transform.translate(0,headScale/2.5f, headScale/2);
        TransformNode translateMouth = new TransformNode("translate(0,bodyY,0)", m);

        m = Mat4Transform.translate(0,-bodyY/4, 0);
        TransformNode translateLegUnderBody = new TransformNode("translate(0,bodyY,0)", m);

        /* robot actions */
        robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,0,-5));
        leanBody = new TransformNode("lean forward",Mat4Transform.rotateAroundX(0));
        turnHead = new TransformNode("lean forward",Mat4Transform.rotateAroundZ(0));
        rotateUpperLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(-10));
        rotateLowerLip = new TransformNode("rotate upper lip", Mat4Transform.rotateAroundX(10));
        rotateRightPupil = new TransformNode("translate(0,bodyY,0)", Mat4Transform.translate(0,eyeScale/4 , 0.15f));
        rotateLeftPupil = new TransformNode("translate(0,bodyY,0)", Mat4Transform.translate(0,eyeScale/4 , 0.15f));

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
        pupilLeft.addChild(rotateLeftPupil);
        rotateLeftPupil.addChild(pupilLeftTransform);
        pupilLeftTransform.addChild(pupilLeftShape);
        head.addChild(translateRightEyeOnHead);
        translateRightEyeOnHead.addChild(eyeRight);
        eyeRight.addChild(eyeRightTransform);
        eyeRightTransform.addChild(eyeRightShape);
        eyeRight.addChild(pupilRight);
        pupilRight.addChild(rotateRightPupil);
        rotateRightPupil.addChild(pupilRightTransform);
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

    /*
  Animates robot to go around in circle and stops at poses 1-5
 */
    void animateRobot() {
        animationTime = getSeconds()- appStartTime - timeDelay; // keep a delay on animation timer

        // dont change the pose until 2 seconds elapse
        if (animationTime - time2 >= 2.1)
            keepPose = false;
        else
            return;

        float x = 5.0f*(float)(Math.sin(Math.toRadians(animationTime *50)));
        float z = 5.0f*(float)(Math.cos(Math.toRadians(animationTime *50)));

        // detect coordinates for different poses
        if ((x < 0.1 && x > -0.1 && z > -5.1 && z < -4.9)){
            translateToPose1();
        } else if ((x < 4.4 && x > 4.2 && z > -2.5 && z < -2.3)){
            translateToPose2();
        } else if ((x < 5.1 && x > 4.9 && z > -0.1 && z < 0.1)){
            translateToPose3();
        } else if ((x < 0.1 && x > -0.1 && z < 5.1 && z > 4.9)) {
            translateToPose4();
        } else if ((x < -4.8f && x > -5f && z > 0.3 && z <  0.5)){
            translateToPose5();
        } else { // if no pose at certain point - keep going
            resetPose();
            float angle = 200.0f*(float)(Math.cos(animationTime));
            Mat4 m = Mat4.multiply(Mat4Transform.translate(x,0,z), Mat4Transform.rotateAroundY(angle));
            robotMoveTranslate.setTransform(m);
            robotMoveTranslate.update();
            return;
        }

        timeDelay += 2;
        time2 = animationTime - 2;
        keepPose = true;
        Museum_GLEventListener.moveAnimation = true;
    }

    private void headRotate(float angle) {
        turnHead.setTransform(Mat4Transform.rotateAroundX(angle));
        turnHead.update();
    }

    private void resetPose(){
        headRotate(0);
        leanBody.setTransform(Mat4Transform.rotateAroundX(0));
        leanBody.update();
    }

    void translateToPose1(){
        Museum_GLEventListener.moveAnimation = false;
        resetPose();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0, -5), Mat4Transform.rotateAroundY(0));
        robotMoveTranslate.setTransform(modelMatrix);
        robotMoveTranslate.update();
        headRotate(0);
    }

    void translateToPose2(){
        Museum_GLEventListener.moveAnimation = false;
        resetPose();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(4,0, -3),Mat4Transform.rotateAroundY(180));
        leanBody.setTransform(Mat4Transform.rotateAroundZ(25));
        turnHead.setTransform(Mat4Transform.rotateAroundZ(-20));
        robotMoveTranslate.setTransform(modelMatrix);
        robotMoveTranslate.update();
    }

    void translateToPose3(){
        Museum_GLEventListener.moveAnimation = false;
        resetPose();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(5,0, 0),Mat4Transform.rotateAroundY(90));
        robotMoveTranslate.setTransform(modelMatrix);
        robotMoveTranslate.update();
        headRotate(60);
    }

    void translateToPose4(){
        Museum_GLEventListener.moveAnimation = false;
        resetPose();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 0, 5), Mat4Transform.rotateAroundY(180));
        leanBody.setTransform(Mat4Transform.rotateAroundX(30));
        turnHead.setTransform(Mat4.multiply(Mat4Transform.rotateAroundX(-30), Mat4Transform.rotateAroundZ(-30)));
        turnHead.update();
        robotMoveTranslate.setTransform(modelMatrix);
        robotMoveTranslate.update();
    }

    void translateToPose5(){
        Museum_GLEventListener.moveAnimation = false;
        resetPose();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(-5,0, 0),Mat4Transform.rotateAroundY(-90));
        robotMoveTranslate.setTransform(modelMatrix);
        robotMoveTranslate.update();
        headRotate(-30);
    }

    void updateMouth() {
        double elapsedTime = getSeconds()- appStartTime;
        float rotateAngle = 10f * (float)Math.sin(elapsedTime*5);
        rotateUpperLip.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
        rotateUpperLip.update();
        rotateLowerLip.setTransform(Mat4Transform.rotateAroundX(-rotateAngle));
        rotateLowerLip.update();
    }

    void updatePupils() {
        double elapsedTime = getSeconds()- appStartTime;
        float x = 0.03f * (float)(Math.cos(Math.toRadians(elapsedTime*200)));
        float y = 0.03f * (float)(Math.sin(Math.toRadians(elapsedTime*200)));
        rotateRightPupil.setTransform(Mat4Transform.translate(x,0.1f + y,0.2f));
        rotateRightPupil.update();
        rotateLeftPupil.setTransform(Mat4Transform.translate(x,-y + 0.1f,0.2f));
        rotateLeftPupil.update();
    }

    // TIMING VAR
    private double appStartTime;
    private boolean keepPose;
    private double time2;
    private double timeDelay = 0;
    private double animationTime;
    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

}
