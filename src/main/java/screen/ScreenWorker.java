package screen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.imgscalr.Scalr;

public class ScreenWorker extends SwingWorker<Void, Void>{
    private ScreenEvent stub;
    private JLabel label;

    public ScreenWorker(ScreenEvent stub, JLabel lable) {
        this.stub = stub;
        this.label = lable;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (true) {
            try {
                byte[] bytes = new byte[1024 * 1024];

                bytes = stub.sendScreen(); //array of bytes is read from the stub object

                BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(bytes)); //byte array is converted back to an image
                BufferedImage scaledImg = Scalr.resize(bImage, 1440, 826, Scalr.OP_ANTIALIAS);
                label.setIcon(new ImageIcon(scaledImg)); //image is set to the label

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
