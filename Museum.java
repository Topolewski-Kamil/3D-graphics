import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * @author Dr Steve Maddock
 * @author Kamil Topolewski - (unauthored bits)
 */
public class Museum extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Museum_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  /**
   * @author Dr Steve Maddock
   */
  public static void main(String[] args) {
    Museum b1 = new Museum("M04");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  /**
   * @author Dr Steve Maddock
   * @author Kamil Topolewski - alterations for assignment purposes
   */
  public Museum(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Museum_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);
    
    JPanel p = new JPanel();
      JButton b = new JButton("animate movement");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("animate face");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("light 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("light 2");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("spotlight");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pose 1");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pose 2");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pose 3");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pose 4");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("pose 5");
      b.addActionListener(this);
      p.add(b);

    this.add(p, BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  /**
   * @author Dr Steve Maddock
   * @author Kamil Topolewski - alterations for assignment purposes
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("animate face")) {
      glEventListener.changeFaceAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("animate movement")) {
      glEventListener.changeMoveAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("light 1")) {
      glEventListener.changeLight1();
    }
    else if (e.getActionCommand().equalsIgnoreCase("light 2")) {
      glEventListener.changeLight2();
    }
    else if (e.getActionCommand().equalsIgnoreCase("spotlight")) {
      glEventListener.changeSpotLight();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pose 1")) {
      glEventListener.roboDuck.translateToPose1();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pose 2")) {
      glEventListener.roboDuck.translateToPose2();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pose 3")) {
      glEventListener.roboDuck.translateToPose3();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pose 4")) {
      glEventListener.roboDuck.translateToPose4();
    }
    else if (e.getActionCommand().equalsIgnoreCase("pose 5")) {
      glEventListener.roboDuck.translateToPose5();
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }
  
}

/**
 * @author Dr Steve Maddock
 */
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

/**
 * @author Dr Steve Maddock
 */
class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}
