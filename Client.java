import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {

	public static final String WELCOME_MESSAGE = "Welcome to key_value pair services.\n"  
	+ "Our server is only supported three types of the command: PUT, GET, DELETE\n" 
	+ "There are a format of these commands: PUT(key, value), GET(key), DELETE(key)\n" 
	+ "We have some examples that you can look at that to see how it works.\n"
	+ "\t\t\tIf you want to run PUT command example, just type 1\n"
	+ "\t\t\tIf you want to run GET command example, just type 2\n"
	+ "\t\t\tIf you want to run DELETE command example, just type 3\n"
	+ "After following these example formats, please to tell us what we can help you";

	public String host;
	public int port;
	public static Socket client = new Socket();
	
	public Client (String host_name, int port_number) {
		host = host_name;
		port = port_number;
	}

	public static Boolean sending_data(String command){
		Boolean result = true;
		try {
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(command);
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println(in.readUTF());
			if (command.equals("QUIT")){result = false;}
			else{result = true;}
		}
		catch(IOException e) {
			e.printStackTrace();}
		return result;
	}

	public static Boolean connection(String host_name, int port) {
		int i = 0;
		do {
			try {
				client.connect(new InetSocketAddress(host_name, port), 1); 
				return true;
			} catch(SocketTimeoutException s) {
				i++;
				System.out.println("Socket timed out!");
			} catch(IOException e) {
				e.printStackTrace();
			}
		} while(i<3 && i != 0);
		return false;
	}
	
	public static void main(String [] args)
	{
		// input port and host name
		Boolean result = true;
		if (args.length < 2 || args.length > 2){
			System.out.println("Please input host name and port that you want to connect");}
		else {
			String host_name = args[0];
			int port = Integer.parseInt(args[1]);
			
			// create socket client
			if (connection(host_name, port)) {
				// input String commands
				Scanner input = new Scanner(System.in);
				try {
					InputStream inFromServer = client.getInputStream();
         			DataInputStream in = new DataInputStream(inFromServer);
					System.out.println("Server says " + in.readUTF());
					System.out.println(WELCOME_MESSAGE);
				}
				catch(IOException e) {
				 e.printStackTrace();}
				
				while (true){
					String command = input.nextLine();
					result = sending_data(command);
					if (!result){break;}
				}
			}
		}
		if (!result){System.exit(0);}
	}
}
