import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.util.Arrays;

class Builtin {

	public static String [] list_of_command_line = {"PUT", "GET", "DELETE"};

	public static Boolean check_command(String command) {
		String [] command_element = command.split("[\\(\\)]");
		if (command_element.length >= 2 && 
			Arrays.asList(list_of_command_line).contains(command_element[0]) && 
			command_element[1] != "") {
			if (command_element[0] != "PUT") {
				return true;}
			else {
				String [] key_value_pairs = command_element[1].split(",");
				if (key_value_pairs.length >= 2 && !key_value_pairs[0].equals("") && !key_value_pairs[1].equals("")) {return true;}
			}
		}
		return false;
	}

	public static boolean isParsable(String input){
		boolean parsable = true;
		try {
		    Integer.parseInt(input);}
		catch(NumberFormatException e) {
		    parsable = false;}
		return parsable;
	}

	public static String[] get_element(String command) {
		String [] result = new String[3];
		String [] command_element = command.split("[\\(\\)]");
		result[0] = command_element[0].trim();
		if (command_element[0] != "PUT" && command_element[1].trim() != "" && !command_element[1].contains(",")) {
			result[1] = command_element[1];
			return result;
		}
		else {
			String [] key_value_pairs = command_element[1].split(",");	
			result[1] = key_value_pairs[0].trim();
			result[2] = key_value_pairs[1].trim();
			return result;
		}
	}
}