package server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import screen.ScreenEvent;
import screen.ScreenEventImpl;

public class Server extends JFrame implements ActionListener {

     private static final long serialVersionUID = 1L;
     InetAddress privateIP;
     JTextField password;

     public Server() {
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

          try {
               // Lấy đỉa chỉ private của máy
               privateIP = InetAddress.getLocalHost();			
          }
          catch (UnknownHostException e) {
               e.printStackTrace();
          }

          //GUI để đặt mật khẩu
          JLabel label = new JLabel("Đặt Password:");
          label.setFont(new Font("Arial", Font.PLAIN, 13));
          password = new JTextField(15);
          password.setToolTipText("Đặt mật khẩu. Chia sẻ mật khẩu này để các máy khác kết nối tới");
          password.setFont(new Font("Arial", Font.PLAIN, 16));
          
          JButton submit = new JButton("Submit");
          submit.setFont(new Font("Arial", Font.PLAIN, 13));

          // Sử dụng JTextField thay vì JLabel để có thể sao chép
          JTextField IPlabel = new JTextField();
          IPlabel.setText("Địa chỉ IP của máy bạn:  " + privateIP.getHostAddress());	
          IPlabel.setFont(new Font("Arial", Font.PLAIN, 12));
          IPlabel.setEditable(false);
          IPlabel.setBorder(null);

          JPanel westPanel = new JPanel();
          westPanel.setLayout(new BorderLayout());
          westPanel.add(label, BorderLayout.CENTER);
          JPanel eastPanel = new JPanel();
          eastPanel.add(submit);
          JPanel centerPanel = new JPanel();
          centerPanel.add(password);

          JPanel topPanel = new JPanel();
          topPanel.setLayout(new BorderLayout());
          topPanel.add(westPanel, BorderLayout.WEST);
          topPanel.add(centerPanel, BorderLayout.CENTER);
          topPanel.add(eastPanel, BorderLayout.EAST);

          JPanel gridPanel = new JPanel();
          gridPanel.setLayout(new GridLayout(2, 1));
          gridPanel.add(topPanel);
          gridPanel.add(IPlabel);

          submit.addActionListener(this);

          setLayout(new BorderLayout());
          add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
          add(gridPanel, BorderLayout.CENTER);
          add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);

          setVisible(true);
          setSize(400, 130);
          password.requestFocusInWindow();						
          setResizable(false);
          setLocation(500, 300);
          setTitle("Cài đặt mật khẩu!");
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }

     public static void main(String[] args) {
          new Server();
     }

     @Override
     public void actionPerformed(ActionEvent e) {
          dispose();
          /*
               * Tạo thanh ghi RMI trên cổng 1888 (Mặc định là 1099)
               * Naming class cung cấp các phương thức để lấy và lưu trữ đối tượng remote (stub) và bind đối tượng remote(burr)
           */
          try {		

               ScreenEvent stub = new ScreenEventImpl(password.getText());

               LocateRegistry.createRegistry(1888);

               Naming.rebind("rmi://" + privateIP.getHostAddress() + ":1888/burr", stub);			
               
               JOptionPane.showMessageDialog(this, "Server đang chạy");
          }
          catch (RemoteException ex) {
               ex.printStackTrace();
          }
          catch (MalformedURLException ex) {
               ex.printStackTrace();
          }
          JOptionPane.showConfirmDialog(this, "Có lỗi xảy ra");
     }

}
