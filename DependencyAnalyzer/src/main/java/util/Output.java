package util;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;

 

public class Output {
	public  String filename = new File("").getAbsolutePath();
	public  FileDescriptor out;
	public  void newFile(String str,String filename) throws IOException{
		 BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		 out.write(str);
		 out.close();
		}	
	public void writetotxt(String str,String filename)  {
		try {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		 out.append(' ');
		 out.append(str + "\n");
		 out.flush();
		 out.close();
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	public  void toFile(String filename) throws IOException{
		File file = new File(filename);
		FileOutputStream os;
		if (filename.equals("") ) { // No file name provided, write to console
	        os = new FileOutputStream(FileDescriptor.out);
	    }
		else {// File name provided, write to this file name
			if(!file.exists()) {
				file.createNewFile();
			}
			
		   FileOutputStream fileoutput = new FileOutputStream(filename,true);
		   PrintStream print = new PrintStream(fileoutput);
		   System.setOut(print);
		}
		
	} 
	
	public  String jsonfyList (List<String>str) {
		return new Gson().toJson(str);
	}
	
	public  String jsonPrettyPrint(String uglyJSONString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		return gson.toJson(je);
	}
	
	public void writerCSV(String[] data,String file) {
		File csvfile = new File(file);
		try {
			FileWriter outputfile = new FileWriter(csvfile,true);
			// create CSVWriter with '|' as separator 
		    CSVWriter writer = new CSVWriter(outputfile); 
			writer.writeNext(data);
			writer.close();
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
				
	}
 
}
