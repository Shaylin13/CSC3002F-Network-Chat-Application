//PDYSHA009 RHMMUH005 LVYNAE001

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

public class ChatClient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Client");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(12, 40);
    JTextArea groupArea = new JTextArea(8, 40);

    DefaultListModel<String> model = new DefaultListModel<>();
    JList<String> contacts = new JList<>( model );


    //JButton btnAddFriends = new JButton("Send File");
    JButton btnAddFriends = new JButton("Add Friend");



    public ChatClient() {

        textField.setEditable(false);
        messageArea.setEditable(false);
	groupArea.setEditable(false);

	contacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	JScrollPane scrollPane = new JScrollPane(contacts);


	scrollPane.setPreferredSize(new Dimension(100, 100));

        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
	frame.getContentPane().add(new JScrollPane(groupArea), "South");
        frame.getContentPane().add(scrollPane, "West");
	frame.getContentPane().add(btnAddFriends, "East");

        frame.pack();

        textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

	contacts.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
        		if (e.getClickCount() == 2) {
           			String selectedItem = (String) contacts.getSelectedValue();
				textField.setText(textField.getText()+selectedItem);
				textField.requestFocus();
			}

    		}
	});

	btnAddFriends.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            String newContact =JOptionPane.showInputDialog(frame,"Enter contact",null);
              if ( newContact == null ) {
                 return;
              }
              if( newContact.equalsIgnoreCase( "" ) ){
                return;
              }
              else{
                model.addElement( newContact +"#");
              }
          }
	});


    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();

            String file = selectedFile.getAbsolutePath();
        }
    }

    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }


    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**private String getFileRec()
    {
        return JOptionPane.showInputDialog(
            frame,
            "Enter Recipient:",
            "Recipient Name",
            JOptionPane.PLAIN_MESSAGE);
    }**/

    private int getResponse ( String userName )
    {
        int input = JOptionPane.showConfirmDialog(frame, userName,null,JOptionPane.YES_NO_OPTION);
        System.out.println(input);
        return input;
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        //control messages
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
	           else if (line.startsWith("GROUP")) {
                groupArea.append(line.substring(5) + "\n");
            }
            else if (line.startsWith("NEW")) {
                //groupArea.append("Online users are: ");
                String name = in.readLine();
                groupArea.append(name + " is online \n");
            }

        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
