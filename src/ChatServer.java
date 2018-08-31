//PDYSHA009 RHMMUH005 LVYNAE001

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {

    /**
     * The port that the server will listen to.
     */
    private static final int PORT = 9001;

    /*Hashmap of users and their respective writers, used for access and checking if name is on server already*/
	//--------------------------------------------------------------------------------------------------------------------------------
	private static HashMap<String, PrintWriter> clientsMap= new HashMap<String, PrintWriter>();
	static boolean addFlag=false;
	//--------------------------------------------------------------------------------------------------------------------------------

    
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    //thread class to handle multiple actions
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        
        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                //ask user for client name repeatedly
                while (true) {
                    //PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);;
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    else {
                        for (Map.Entry<String, PrintWriter> entry:clientsMap.entrySet()) {
                            String key = entry.getKey();
                            PrintWriter writer = entry.getValue();
                            writer.println("GROUP" + name + " just logged on to the server");
                        }
                    }
                    synchronized (clientsMap) {
                        if (!clientsMap.containsKey(name)) {
                            break;
                        }
                    }
                }

                //once the name has been accepted
                out.println("NAMEACCEPTED");
                clientsMap.put(name, out);
                //out.println("NEW Online users are: ");
                for (Map.Entry<String, PrintWriter> entry:clientsMap.entrySet()) {
                    out.println("NEW");
                    String key = entry.getKey();
                    //PrintWriter writer = entry.getValue();
                    out.println(key);
                }

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }

			input= name+"#"+input;
			String[] inputArr = input.split("#");

			//Check if its a group message or private (one to one or broadcast)
			if(inputArr[1].equals("all")||inputArr[1].equals("All"))
			{
				for (Map.Entry<String,PrintWriter> entry:clientsMap.entrySet()) {
				String key = entry.getKey();
				PrintWriter writer = entry.getValue();
						writer.println("GROUP" + name + " To All: " + inputArr[2]);
                    		}
			}

			else{//check who to send message to
                    	for(int i =0; i<inputArr.length-1;i++)
                    	{
                    	    if(clientsMap.containsKey(inputArr[i]))
                    	    {
                    	        if(inputArr[i].equals(name))//message that shows up on sender's screen

                    	            {
                                        String nameList="";//list of recipients
                                        for(int j =1 ; j<inputArr.length-1 ; j++)
                                        {
                                            nameList = nameList+inputArr[j]+" ";
                                        }
                                        clientsMap.get(inputArr[i]).println("MESSAGE " + name +" Sent Private to " + nameList + ": " + inputArr[inputArr.length-1]);}
                    	        else{
                    	            clientsMap.get(inputArr[i]).println("MESSAGE " + name+" to "+inputArr[i]+" Private: " +inputArr[inputArr.length-1]);
                    	        }
                    	    }
                    	    else
                    	    {
                    	        clientsMap.get(inputArr[0]).println("MESSAGE "+inputArr[i]+" not on server");
                    	    }
                    	}
                    	
                }

                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going off  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    //names.remove(name);
			if (out != null) {
				clientsMap.remove(name);
                	}
                }
                if (out != null) {
                    //writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
