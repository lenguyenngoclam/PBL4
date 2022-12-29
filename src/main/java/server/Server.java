package server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import screen.ScreenEvent;
import screen.ScreenEventImpl;

public class Server extends JFrame implements ActionListener{
    private static final long serialVersionUID = 1L;
	InetAddress privateIP;
        InetAddress publicIP;
	JTextField password;
	
	public Server() {
               try {   
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   
               }
               catch (ClassNotFoundException e) {}
               catch (InstantiationException e) {}
               catch (IllegalAccessException e) {}
               catch (UnsupportedLookAndFeelException e) {}        //Refines the look of the ui
		
               try {
                    privateIP = InetAddress.getLocalHost();			//Local (Private) IP of your Machine
                    URL whatismyip = new URL("http://checkip.amazonaws.com"); // Website that can show you your public IP Address
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                                    whatismyip.openStream()));

                    publicIP = InetAddress.getByName(in.readLine()); // Public IP of your machine
               } 
               catch (UnknownHostException e) {
                    e.printStackTrace();
               }
               catch (MalformedURLException ex) {
                    ex.printStackTrace();
               }
               catch (IOException ex) {
                    ex.printStackTrace();
               }
		
               //Creating a GUI which prompts the user to Set up a Password
               JLabel label = new JLabel("Set Password:");
               label.setFont(new Font("Arial", Font.PLAIN, 13));
               password = new JTextField(15);
               password.setToolTipText("Set a Password. Share the password to Connect with your Machine!");
               password.setFont(new Font("Arial", Font.PLAIN, 16));
               JButton submit = new JButton("Submit");
               submit.setFont(new Font("Arial", Font.PLAIN, 13));
		
               //using JTextField instead of a JLabel to display information because, the text in textField can be selected and copied
		JTextField  IPlabel = new JTextField ();					
		IPlabel.setText("Your Machine' public IP Address is:  " + publicIP.getHostAddress()); // for internet connection
		
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
		gridPanel.add(topPanel);  gridPanel.add(IPlabel);
		
		submit.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
		add(gridPanel, BorderLayout.CENTER);
		add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);
		
		setVisible(true);
		setSize(400, 130);
		password.requestFocusInWindow();						//Caret focus is on this Component
		setResizable(false);
		setLocation(500, 300);
		setTitle("Set a Password!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args) {
		new Server();
	}
		

	@Override
	public void actionPerformed(ActionEvent e) {
               dispose();									//when submit is clicked, the GUI is disposed
               /*
                * Creating RMI Registry on port 1888 (default for RMI is 1099)
                * Naming class provides methods to get and store the remote object (stub)
                * and bind the remote object by a name (burr)
               */
               try {
                    //use System.setProperty when using publicIP for Interner Connection, 
                    //swap publicIP for Router IP if Internet Connection passes through a Router
                    System.setProperty("java.rmi.server.hostname", publicIP.getHostAddress());		
			
                    ScreenEvent stub = new ScreenEventImpl(password.getText());
			
                    //Uncomment below line for internet Connection and remove extends UnicastRemoteObcject class from ScreeEventImpl Class
                    //1100 Port here is used as a Server Port
                    ScreenEvent serverStub = (ScreenEvent) UnicastRemoteObject.exportObject(stub, 1100);

                    //RMIRegistry on port 1888
                    LocateRegistry.createRegistry(1888);
			
                    //Naming rebind is done on the privateIP for both intraNet and internet Connection
                    Naming.rebind("rmi://" + privateIP.getHostAddress() + ":1888/burr", serverStub);			//Change remote Object stub to serverStub for internetConnection
                    System.out.println("Server Running!!!");
			
               } 
               catch (RemoteException ex) {
                    ex.printStackTrace();
               }
               catch (MalformedURLException ex) {
                    ex.printStackTrace();
               }
	}

}
