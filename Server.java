import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Collections;
import java.time.LocalDateTime;

public class Server extends Thread{

	// define some message
	public static final String WELCOME_MESSAGE = "Welcome To Our Key Value Pairs Server";

	public static final String FORMAT_STRING_ERROR = "Please input the right format";
	public static final String NUMBER_NOT_FOUND = "This is not found number";
	public static final String ALREADY_FIND_STRING = "This is a result: ";
	public static final String ASKING_NEW_COMMAND = "Are you satisfy with this result, do you want me find some thing for you?";
	public static final String DELETE_KEY = "This key is already deleted";
	public static final String ELEMENT_EXISTED = "This key and value is already existed";
	public static final String UPDATE_VALUE = "Thanks for contact us to update the new value key";
	public static final String ADDING_NEW_KEY = "This key is already added";
	public static final String GOOD_BYE = "Thanks for connecting our server. If you need something, please let us know";

	public int port;
	private ServerSocket serverSocket;
	public static Builtin builtin;
	public HashMap<String, String> key_value_pairs = new HashMap<String, String>();

	public Server(int port_number) throws IOException {
		port = port_number;
		serverSocket = new ServerSocket(port_number);
	}
	
	public void run()
	{
		reading_server_file("ServerFile.txt");
		while(true) {
			try {
	            Socket server = serverSocket.accept();

	            DataOutputStream out = new DataOutputStream(server.getOutputStream());

	            // get local socket address by the function server.getLocalSocketAddress()
	            writing_log_file("Server_LogFile.txt", "Just connected to " + server.getRemoteSocketAddress());
	            outputStream(out, WELCOME_MESSAGE);

	            while(true){
	            	DataInputStream in = new DataInputStream(server.getInputStream());
					String command = in.readUTF();
					System.out.println("Client says: " + command);
					if (command.equals("QUIT")){
						sending_and_writing_log(out, GOOD_BYE);
						server.close();
						break;
					}
					proccess_command(out, command);
	            }

				
			} catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;}
			catch(IOException e) {
				e.printStackTrace();}
		}
	}

	public void proccess_command(DataOutputStream out, String command){
		if (!builtin.check_command(command)) {
			sending_and_writing_log(out, FORMAT_STRING_ERROR);
			return;}

		String [] list_element = builtin.get_element(command);

		if (list_element[0].equals("GET") || list_element[0].equals("DELETE")) {
			String value = key_value_pairs.get(list_element[1]);
			if (value == null) {
				sending_and_writing_log(out, NUMBER_NOT_FOUND + "\n");}
			else {
				if (list_element[0].equals("GET")) {
					sending_and_writing_log(out, ALREADY_FIND_STRING + value + "\n" + ASKING_NEW_COMMAND);
				}
				else {
					key_value_pairs.remove(list_element[1]);
					sending_and_writing_log(out, DELETE_KEY + "\n" + ASKING_NEW_COMMAND);
					writing_file("ServerFile.txt");
				}
			}
		} else {
			String value = key_value_pairs.get(list_element[1]);
			String key = list_element[1];
			if(key_value_pairs.containsKey(key)) {
				if (value.equals(list_element[2])){
					sending_and_writing_log(out, ELEMENT_EXISTED + "\n" + ASKING_NEW_COMMAND);}
				else {
					sending_and_writing_log(out, UPDATE_VALUE + "\n" + ASKING_NEW_COMMAND);
					key_value_pairs.replace(key, list_element[2]);
					writing_file("ServerFile.txt");
				}
			} else {
				sending_and_writing_log(out, ADDING_NEW_KEY + "\n" + ASKING_NEW_COMMAND);
				key_value_pairs.put(key, list_element[2]);
				writing_file("ServerFile.txt");
			}
			
		}

	}

	public void reading_server_file(String filename){
		try {
			FileReader fileReader = new FileReader(filename);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				String [] key_value = line.split(" ");
				key_value_pairs.put(key_value[0].trim(), key_value[1].trim());}   
			bufferedReader.close();
		} catch(FileNotFoundException e) {
            System.out.println("Unable to open file '" + filename + "'");                
        }
        catch(IOException e) {
            System.out.println("Error reading file '" + filename + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
		
	}

	public void writing_file(String filename) {
		Path path = Paths.get(filename);
		try (BufferedWriter writer = Files.newBufferedWriter(path)){
			key_value_pairs.forEach((k,v) -> {
				try {writer.write(k + " " + v + "\n");}
				catch(IOException e) {
		            System.out.println("Error reading file '" + filename + "'");                  
		            // Or we could just do this: 
		            // ex.printStackTrace();
		        }
			});
			writer.close();
		} catch(IOException e) {
            System.out.println("Error reading file '" + filename + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
	}

	public void writing_log_file(String filename, String message){
		try {
			System.out.println("hehe");
			File file = new File(filename);
			if(!file.exists()){file.createNewFile();}
			FileWriter fileWritter = new FileWriter(file.getName(),true);
			BufferedWriter writer = new BufferedWriter(fileWritter);
			LocalDateTime timePoint = LocalDateTime.now();
			writer.write("At " + timePoint + ". Server says: " + message + "\n");
			writer.close();
		} catch(IOException e) {
            System.out.println("Error reading file '" + filename + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
	}

	public void sending_and_writing_log(DataOutputStream out, String message) {
		outputStream(out, message);
		writing_log_file("Server_LogFile.txt", message);
	}

	public void outputStream(DataOutputStream out, String sending){
		try {
			out.writeUTF(sending);}
		catch(IOException e) {
			e.printStackTrace();}
	}
	
	public static void main(String args[])
	{	      
		int port = Integer.parseInt(args[0]);
		try {
			Thread t = new Server(port);
			t.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
