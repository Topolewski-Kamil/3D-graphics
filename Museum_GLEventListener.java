import gmaths.*;
import com.jogamp.opengl.*;

/**
 * @author Dr Steve Maddock
 * @author Kamil Topolewski
 */
public class Museum_GLEventListener implements GLEventListener {

    Museum_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(4f, 14f, 12f));
    }

    /* Initialization
     * @author Dr Steve Maddock */
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
    /* Called to indicate the drawing surface has been moved and/or resized
     *  @author Dr Steve Maddock */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        float aspect = (float) width / (float) height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    /* @author Dr Steve Maddock*/
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /* Clean up memory, if necessary
    *  @author Kamil Topolewski */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        swingingLight.dispose(gl);
        generalLight1.dispose(gl);
        generalLight2.dispose(gl);
        room.floor.dispose(gl);
        room.wallBack.dispose(gl);
        room.wallLeft.dispose(gl);
        room.outside.dispose(gl);
        lightStand.lightTop.dispose(gl);
        lightStand.lampTop.dispose(gl);
        lightStand.lampMid.dispose(gl);
        lightStand.lampBtm.dispose(gl);
        phone.mobilePhone.dispose(gl);
        phone.standPhone.dispose(gl);
        egg.standEgg.dispose(gl);
        egg.eggFig.dispose(gl);
        lightStand.lightCase.dispose(gl);
        roboDuck.sphereYellow.dispose(gl);
        roboDuck.sphereWhite.dispose(gl);
        roboDuck.sphereBlack.dispose(gl);
        roboDuck.sphereOrange.dispose(gl);
    }

    /* ON/OFF for lights and animations
     *  @author Kamil Topolewski */
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
     * @author Kamil Topolewski
     */
    private Camera camera;

    Robot roboDuck;
    private Light swingingLight, generalLight1, generalLight2;
    private Room room;
    private Egg egg;
    private Phone phone;
    private SpotLightStand lightStand;

    /* @author Kamil Topolewski */
    private void initialise(GL3 gl) {

        // lights
        swingingLight = new Light(gl);
        swingingLight.setCamera(camera);
        generalLight1 = new Light(gl);
        generalLight1.setCamera(camera);
        generalLight2 = new Light(gl);
        generalLight2.setCamera(camera);

        // room init
        room = new Room(gl, camera, swingingLight, generalLight1, generalLight2);

        // robot init
        roboDuck = new Robot(gl, camera, swingingLight, generalLight1, generalLight2);

        //light stand init
        lightStand = new SpotLightStand(gl, camera, swingingLight, generalLight1, generalLight2);

        // egg init
        egg =  new Egg(gl, camera, swingingLight, generalLight1, generalLight2);

        //phone init
        phone = new Phone(gl, camera, swingingLight, generalLight1, generalLight2);
    }

    /* @author Kamil Topolewski */
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
                    room.wallLeft.setModelMatrix(room.getMforLeftWall(i, j));
                    room.wallLeft.render(gl);
                }
            }
        }

        egg.standEgg.render(gl);
        egg.eggFig.render(gl);
        phone.standPhone.render(gl);
        phone.mobilePhone.render(gl);
        lightStand.lightTop.render(gl);
        lightStand.lightTop.setModelMatrix(lightStand.getMforLamp());
        lightStand.lightCase.render(gl);
        lightStand.lightCase.setModelMatrix(lightStand.getMforLampCase());
        lightStand.lampTop.render(gl);
        lightStand.lampMid.render(gl);
        lightStand.lampBtm.render(gl);

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

    /* @author Kamil Topolewski */
    private Vec3 getSwingLightPosition() {
        double elapsedTime = getSeconds() - appStartTime;
        float y = 0.5f * (float) (Math.sin(elapsedTime));
        float z = 2 * (float) (Math.sin(elapsedTime));
        return new Vec3(6.5f, Math.abs(y) + 3.5f, -z);
    }

    /* @author Kamil Topolewski */
    private Vec3 getGeneralLightPosition1() {
        return new Vec3(-6, 10, -6);
    }

    /* @author Kamil Topolewski */
    private Vec3 getGeneralLightPosition2() {
        return new Vec3(6, 10, 6);
    }

    // ***************************************************
    /* TIME
    * @author Dr Steve Maddock
     */
    static double appStartTime;
    static double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
