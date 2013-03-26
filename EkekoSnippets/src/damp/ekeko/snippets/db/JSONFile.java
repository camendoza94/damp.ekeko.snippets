package damp.ekeko.snippets.db;

/**
 * Write and read JSONObject to File
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONFile {

	public JSONFile() {
		// TODO Auto-generated constructor stub
	}
	 
	public static void write(String filename, JSONObject obj) {
		try {		 
			FileWriter file = new FileWriter(filename);
			file.write(obj.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print(obj);
	}

	public static JSONObject read(String filename) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(filename));
			System.out.print(obj);
			return (JSONObject) obj;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	 
}