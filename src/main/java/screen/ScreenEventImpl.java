package screen;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ScreenEventImpl extends UnicastRemoteObject implements ScreenEvent {

     private static final long serialVersionUID = 1L;
     Robot robot = null;
     String password;
     double width, height;

     public ScreenEventImpl(String password) throws RemoteException {
          super();
          this.password = password;
     }

     @Override
     public byte[] sendScreen() {
          byte[] byteArray = null;

          try {
               //Gets the environment of the Service device 
               GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
               GraphicsDevice graphicsScreen = graphicsEnv.getDefaultScreenDevice();
               robot = new Robot(graphicsScreen);

               //Dimension of the Server Machine' Screen
               Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
               width = dimension.getWidth();
               height = dimension.getHeight();

               BufferedImage bImage = robot.createScreenCapture(new Rectangle(dimension));					//Capturing screen of the Server as an image
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               ImageIO.write(bImage, "jpeg", bos);										//Image is converted into an array of bytes
               bos.flush();

               byteArray = bos.toByteArray();
               bos.close();
          }
          catch (AWTException ex) {
               ex.printStackTrace();
          }
          catch (IOException ex) {
               ex.printStackTrace();
          }

          return byteArray;
     }

     @Override
     public boolean checkPassword(String inputPassword) throws RemoteException {			//checks if the entered password matches to that of the Server' Password
          if (password.equals(inputPassword)) {
               return true;
          }

          return false;
     }

     public double getWidth() { //Returns width of the Server Machine' Screen
          return width;
     }

     public double getHeight() {						//Returns height of the Server Machine' Screen
          return height;
     }

     @Override
     public void mouseMovedEvent(int xScale, int yScale) throws RemoteException {		//Robot evevnt for when the Mouse is Moved on the Client Machine
          robot.mouseMove(xScale, yScale); //Moves Cursor to the given coordinates
          System.out.println("Coordinate : " + xScale + "," + yScale);
     }

     @Override
     public void mousePressedEvent(int buttonPressed) throws RemoteException {			//Robot evevnt for when the Mouse is Pressed on the Client Machine
          if (buttonPressed == 1) //Left Key
          {
               robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
          }
          else if (buttonPressed == 2) //Middle Key
          {
               robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
          }
          else if (buttonPressed == 3) //Right Key
          {
               robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
          }
     }

     @Override
     public void mouseReleasedEvent(int buttonReleased) throws RemoteException {			//Robot evevnt for when the Mouse is Released on the Client Machine
          if (buttonReleased == 1) //Left Key
          {
               robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
          }
          else if (buttonReleased == 2) //Middle Key
          {
               robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
          }
          else if (buttonReleased == 3) //Right Key
          {
               robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
          }
     }

     @Override
     public void keyPressed(int keyPressed) throws RemoteException {						//Robot evevnt for when a Keyboard Button is Pressed on the Client Machine
          robot.keyPress(keyPressed);							//Presses the Particular Key
     }

     @Override
     public void keyReleased(int keyReleased) throws RemoteException {					//Robot evevnt for when a Keyboard Button is Released on the Client Machine
          robot.keyRelease(keyReleased);						//Releases the Particular Key
     }

     @Override
     public void mouseWheelMoved(int amount) throws RemoteException {
          try{
               robot.mouseWheel(amount);
               Thread.sleep(50);
          }catch(InterruptedException e){
               e.printStackTrace();
          }
     }
}
