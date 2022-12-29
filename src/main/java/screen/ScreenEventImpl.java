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

               //Dimension của máy server
               Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
               width = dimension.getWidth();
               height = dimension.getHeight();

               BufferedImage bImage = robot.createScreenCapture(new Rectangle(dimension));
               //Thâu lại màn hình server dưới dạng hình ảnh
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               ImageIO.write(bImage, "jpeg", bos);
               //Ảnh được chuyển về mảng bytes
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
     public boolean checkPassword(String inputPassword) throws RemoteException {
          //Kiểm tra mật khẩu client nhập có giống với mật khẩu server đã cài đặt không
          if (password.equals(inputPassword)) {
               return true;
          }

          return false;
     }

     public double getWidth() { 
          //Trả về chiều rộng của màn hình server
          return width;
     }

     public double getHeight() {						
          //Trả về chiều cao của màn hình server
          return height;
     }

     @Override
     public void mouseMovedEvent(int xScale, int yScale) throws RemoteException {		
          //Robot event cho di chuyển chuột bên client
          robot.mouseMove(xScale, yScale); //Moves Cursor to the given coordinates
          System.out.println("Coordinate : " + xScale + "," + yScale);
     }

     @Override
     public void mousePressedEvent(int buttonPressed) throws RemoteException {			
          //Robot event khi nhấn chuột bên client
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
     public void mouseReleasedEvent(int buttonReleased) throws RemoteException {		
          //Robot event khi thả chuột
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
     public void keyPressed(int keyPressed) throws RemoteException {						
          //Robot event khi nhấn phím
          robot.keyPress(keyPressed);							//Presses the Particular Key
     }

     @Override
     public void keyReleased(int keyReleased) throws RemoteException {					
          //Robot event khi thả phím
          robot.keyRelease(keyReleased);						//Releases the Particular Key
     }

     @Override
     public void mouseWheelMoved(int amount) throws RemoteException {
          try {
               //Robot event khi cuộn chuột
               robot.mouseWheel(amount);
               Thread.sleep(50);
          }
          catch (InterruptedException e) {
               e.printStackTrace();
          }
     }
}
