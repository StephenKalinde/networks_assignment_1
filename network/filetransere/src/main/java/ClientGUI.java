/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Takaedza Chigwedere and Stephen Kalinde
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.File;
 
 
/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {
 
    private static final long serialVersionUID = 1L;
    // will first hold "Username:", later on "Enter message"
    private JLabel label;
    // to hold the Username and later on the messages
    private JTextField tf;
    // to hold the server address an the port number
    private JTextField tfServer, tfPort;
    // to Logout and get the list of the users
    private JButton login, logout, whoIsIn, fileSelectBtn, sendFileBtn, listFilesBtn;
    // button to select file
    // text field with file path
    private JTextField filetextField;
    // for the chat room
    private JTextArea ta;
    // if it is for connection
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;

    private File file;
 
    // Constructor connection receiving a socket number
    ClientGUI(String host, int port) {
 
        super("Chat Client");
        defaultPort = port;
        defaultHost = host;
         
        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3,1));
        // the server name anmd the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
        // the two JTextField with default value for server address and port number
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
 
        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        serverAndPort.add(new JLabel(""));
        // adds the Server an port field to the GUI
        northPanel.add(serverAndPort);
 
        // the Label and the TextField
        label = new JLabel("Enter your username below", SwingConstants.CENTER);
        northPanel.add(label);
        tf = new JTextField("Anonymous");
        tf.setBackground(Color.WHITE);
        northPanel.add(tf);

        fileSelectBtn = new JButton("Browse");
        fileSelectBtn.addActionListener(this);
        fileSelectBtn.setEnabled(false);

        listFilesBtn = new JButton ("List Files");
        listFilesBtn.addActionListener(this);
        listFilesBtn.setEnabled(false);

        sendFileBtn = new JButton("Send");
        sendFileBtn.addActionListener(this);
        sendFileBtn.setEnabled(false);

        filetextField = new JTextField("");
        filetextField.setEnabled(false);

        northPanel.add(fileSelectBtn);
        northPanel.add(filetextField);
        northPanel.add(sendFileBtn);
        northPanel.add(listFilesBtn);

        add(northPanel, BorderLayout.NORTH);
 
        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1,1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);
 
        // the 3 buttons
        login = new JButton("Login");
        login.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);       // you have to login before being able to logout
        whoIsIn = new JButton("Who is in");
        whoIsIn.addActionListener(this);
        whoIsIn.setEnabled(false);      // you have to login before being able to Who is in
 
        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(logout);
        southPanel.add(whoIsIn);
        add(southPanel, BorderLayout.SOUTH);
 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);
        tf.requestFocus();
 
    }
 
    // called by the Client to append text in the TextArea
    void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
    }
    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        login.setEnabled(true);
        logout.setEnabled(false);
        whoIsIn.setEnabled(false);
        fileSelectBtn.setEnabled(false);
        filetextField.setEnabled(false);

        label.setText("Enter your username below");
        tf.setText("Anonymous");
        // reset port number and host name as a construction time
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        // let the user change them
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        // don't react to a <CR> after the username
        tf.removeActionListener(this);
        connected = false;
    }
         
    /*
    * Button or JTextField clicked
    */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        // if it is the Logout button
        if(o == logout) {

            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            fileSelectBtn.setEnabled(false);
            filetextField.setEnabled(false);
            sendFileBtn.setEnabled(false);
            listFilesBtn.setEnabled(false);
            return;
        }

        if(o == sendFileBtn)
        {
            client.sendMessage(new ChatMessage(3,file));
            filetextField.setText("");
        }

        if(o== fileSelectBtn)
        {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            int r = fileChooser.showOpenDialog(null);

            if(r == JFileChooser.APPROVE_OPTION)
            {

                file =fileChooser.getSelectedFile();
                filetextField.setText(file.getAbsolutePath());
                sendFileBtn.setEnabled(true);

            }

        }

        if(o == listFilesBtn)
        {
            client.sendMessage(new ChatMessage(4));
            return;
        }
        // if it the who is in button
        if(o == whoIsIn) {
            client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));              
            return;
        }
 
        // ok it is coming from the JTextField
        if(connected) {
            // just have to send the message
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));            
            tf.setText("");
            return;
        }
         
 
        if(o == login) {
            // ok it is a connection request
            String username = tf.getText().trim();
            // empty username ignore it
            if(username.length() == 0)
                return;
            // empty serverAddress ignore it
            String server = tfServer.getText().trim();
            if(server.length() == 0)
                return;
            // empty or invalid port numer, ignore it
            String portNumber = tfPort.getText().trim();
            if(portNumber.length() == 0)
                return;
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception en) {
                return;   // nothing I can do if port number is not valid
            }
 
            // try creating a new Client with GUI
            client = new Client(server, port, username, this);
            // test if we can start the Client
            if(!client.start())
                return;
            tf.setText("");
            label.setText("Enter your message below");

            fileSelectBtn.setEnabled(true);
            filetextField.setEnabled(true);
            sendFileBtn.setEnabled(true);
            listFilesBtn.setEnabled(true);

            connected = true;
             
            // disable login button
            login.setEnabled(false);
            // enable the 2 buttons
            logout.setEnabled(true);
            whoIsIn.setEnabled(true);
            // disable the Server and Port JTextField
            tfServer.setEditable(false);
            tfPort.setEditable(false);
            // Action listener for when the user enter a message
            tf.addActionListener(this);
        }
 
    }


    // to start the whole thing the server
    public static void main(String[] args) {
        new ClientGUI("localhost", 1696);
    }
 
}

