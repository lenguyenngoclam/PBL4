package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import screen.ScreenEvent;
import screen.ScreenWorker;

public class Client extends JFrame implements ActionListener {
     
     private static final long serialVersionUID = 1L;
     ScreenEvent stub;
     JTextField serverIP, password;
     double stubWidth, stubHeight;
     
     public Client() {
          try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          }
          catch (ClassNotFoundException e) {
          }
          catch (InstantiationException e) {
          }
          catch (IllegalAccessException e) {
          }
          catch (UnsupportedLookAndFeelException e) {
          }  

          //GUI để nhập password và địa chỉ IP của server
          JLabel IPlabel = new JLabel("Server IP: ");
          IPlabel.setFont(new Font("Arial", Font.PLAIN, 13));
          
          JLabel passwordLabel = new JLabel("Password:");
          passwordLabel.setFont(new Font("Arial", Font.PLAIN, 13));
   
          serverIP = new JTextField(15);
          serverIP.setToolTipText("Nhập địa chỉ IP của máy mà bạn muốn kết nối tới !");
          serverIP.setFont(new Font("Arial", Font.PLAIN, 16));
          password = new JTextField(15);
          password.setToolTipText("Nhập mật khẩu của máy mà bạn muốn kết nối tới");
          password.setFont(new Font("Arial", Font.PLAIN, 16));
          JButton submit = new JButton("Kết nối");
          submit.setFont(new Font("Arial", Font.PLAIN, 13));
          
          JPanel panel1 = new JPanel();
          panel1.setLayout(new BorderLayout());
          panel1.add(IPlabel, BorderLayout.CENTER);
          
          JPanel panel2 = new JPanel();
          panel2.add(serverIP);
          
          JPanel topPanel = new JPanel();
          topPanel.add(panel1);
          topPanel.add(panel2);
          
          JPanel panel3 = new JPanel();
          panel3.setLayout(new BorderLayout());
          panel3.add(passwordLabel, BorderLayout.CENTER);
          
          JPanel panel4 = new JPanel();
          panel4.add(password);
          
          JPanel midPanel = new JPanel();
          midPanel.add(panel3);
          midPanel.add(panel4);
          
          JPanel bottomPanel = new JPanel();
          bottomPanel.add(submit);
          
          JPanel gridPanel = new JPanel();
          gridPanel.setLayout(new GridLayout(3, 1));
          gridPanel.add(topPanel);
          gridPanel.add(midPanel);
          gridPanel.add(bottomPanel);
          
          submit.addActionListener(this);
          
          setLayout(new BorderLayout());
          add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
          add(gridPanel, BorderLayout.CENTER);
          add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);
          
          setVisible(true);
          setSize(330, 175);
          setResizable(false);
          setLocation(500, 300);
          setTitle("Remote Desktop Application!");
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }
     
     public static void main(String[] args) {
          new Client();
     }
     
     @Override
     public void actionPerformed(ActionEvent e) {
          /*
		 * Naming class lấy đối tượng stub thông qua phương thức lookup()
           */
          try {
               stub = (ScreenEvent) Naming.lookup("rmi://" + serverIP.getText() + ":1888/burr");
               
               if (!(stub.checkPassword(password.getText()))) {
                    JOptionPane.showMessageDialog(this, "Thông tin bạn nhập bị sai !");
                    return;
               }
               else {
                    JOptionPane.showMessageDialog(this, "Kết nối thành công !");
                    dispose();              
               }

               //Lưu trữ chiều dài và chiều rộng của máy server
               stubWidth = stub.getWidth();
               stubHeight = stub.getHeight();
               
               new ScreenFrame();
          }
          catch (MalformedURLException ex) {
               ex.printStackTrace();
          }
          catch (RemoteException ex) {
               ex.printStackTrace();
          }
          catch (NotBoundException ex) {
               ex.printStackTrace();
          }
          JOptionPane.showMessageDialog(this, "Đã có lỗi xảy ra!");
          return;
     }
     
     class ScreenFrame extends JFrame implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

          private static final long serialVersionUID = 1L;
          JLabel label;
          JPanel panel;
          JInternalFrame internalFrame;
          
          public ScreenFrame() {
               try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               }
               catch (ClassNotFoundException e) {
               }
               catch (InstantiationException e) {
               }
               catch (IllegalAccessException e) {
               }
               catch (UnsupportedLookAndFeelException e) {
               }       

               //GUI hiển thị màn hình máy tính server
               label = new JLabel();
               JDesktopPane desktopPane = new JDesktopPane();
               add(desktopPane, BorderLayout.CENTER);
               panel = new JPanel();
               panel.add(label);
               
               internalFrame = new JInternalFrame("Server Screen", true, true, true);
               internalFrame.setLayout(new BorderLayout());
               internalFrame.getContentPane().add(panel, BorderLayout.CENTER);

               //Tìm dimension của máy server để cho màn hình server khít vừa vào màn hình máy client
               Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
               setSize(r.width, r.height);      
               setMaximumSize(new Dimension(r.width, r.height));
               setMinimumSize(new Dimension((int) (r.width / 1.2), (int) (r.height / 1.2)));
               
               internalFrame.setSize(getWidth(), getHeight());
               // Xoá border của internal frame
               internalFrame.setBorder(null);				
               // Xoá header của internal frame
               ((javax.swing.plaf.basic.BasicInternalFrameUI) internalFrame.getUI()).setNorthPane(null);	

               desktopPane.add(internalFrame);
               setVisible(true);
               internalFrame.setVisible(true);
               try {
                    internalFrame.setMaximum(true);				
               }
               catch (PropertyVetoException e) {
                    e.printStackTrace();
               }
               // Cài đặt panel là focused component để có thể sử dụng KeyListener
               panel.setFocusable(true);
               panel.requestFocusInWindow();
               
               setTitle("Remote Desktop Application");
               setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               
               new ScreenWorker(stub, label, r.width, r.height).execute();
               
               panel.addKeyListener(this);
               panel.addMouseListener(this);
               panel.addMouseMotionListener(this);
               panel.addMouseWheelListener(this);
          }
          
          @Override
          public void mouseClicked(MouseEvent e) {
          }
          
          @Override
          public void mousePressed(MouseEvent e) {				
               //Event khi nhấn chuột
               int buttonPressed = e.getButton();
               System.out.println(buttonPressed);
               
               try {
                    stub.mousePressedEvent(buttonPressed);
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
          
          @Override
          public void mouseReleased(MouseEvent e) {				
               //event khi chuột được thả
               int buttonReleased = e.getButton();
               
               try {
                    stub.mouseReleasedEvent(buttonReleased);
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
          
          @Override
          public void mouseEntered(MouseEvent e) {
          }
          
          @Override
          public void mouseExited(MouseEvent e) {
          }
          
          @Override
          public void mouseDragged(MouseEvent e) {
               //event khi rê chuột
               mouseMoved(e);
          }
          
          @Override
          public void mouseMoved(MouseEvent e) { 
               //event khi di chuyển chuột
               double xAxis = (double) stubWidth / panel.getWidth();				//scale trên trục X 
               double yAxis = (double) stubHeight / panel.getHeight();				//scale trên trục Y 

               try {
                    
                    stub.mouseMovedEvent((int) (e.getX() * xAxis), (int) (e.getY() * yAxis));
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
          
          @Override
          public void keyTyped(KeyEvent e) {
          }
          
          @Override
          public void keyPressed(KeyEvent e) {					
               //event khi nhấn phím
               try {
                    stub.keyPressed(e.getKeyCode());
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
          
          @Override
          public void keyReleased(KeyEvent e) {					
               //event khi thả phím
               try {
                    stub.keyReleased(e.getKeyCode());
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
          
          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
               //event khi cuộn chuột
               try {
                    stub.mouseWheelMoved(e.getScrollAmount() * e.getWheelRotation());
               }
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
          }
     }
}
