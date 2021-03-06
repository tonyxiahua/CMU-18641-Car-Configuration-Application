package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import exception.AutoException;
import exception.ErrorType;
import model.Automobile;

public class FileIO {
	private String[] fl = new String [3];
	
	/**
	 * Serialization
	 * @param au is an instance of Automotive
	 * @return
	 */
	public Automobile serializeAutomotive(Automobile au) {
		Automobile staff = new Automobile();
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Automotive.dat"));
			out.writeObject(au);
			out.close();
			
			ObjectInputStream in =  new ObjectInputStream(new FileInputStream("Automotive.dat"));
			staff = (Automobile) in.readObject();
		} catch(Exception e) {  
			System.out.print("Error: " + e);
			System.exit(1);
		}
		return staff;
	}
	/**
	 * Build Automobile Object from a file.
	 * @param filename
	 * @return
	 */
	public Automobile buildAutomotiveObject(String filename) {
		try {
			File file = new File(filename);
			try {
			if(!file.exists()) {
				throw new AutoException(ErrorType.NO_FILE.ordinal(), "Exception: File is not found!");
			}
		} catch (AutoException ae) {
			filename = ae.fix(ErrorType.NO_FILE.ordinal());
			try {
				return fileReading(new File(filename));
			} catch (IOException e) {
				System.out.println("Error" + e.toString());
				return null;
			}
		}
			return fileReading(file);
		} catch (IOException e) {
			System.out.println("Error" + e.toString());
			return null;
		}
	}
	
	public Automobile fileReading(File file) throws IOException { 
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		if(line == null)
			return null;	
		fl = firstLine(line);
		String name = fl[0];
		float basePrice = 0;
		int sizeOptionSet = 0;
		try {
			try {
				if(fl[1].equals("") || fl[1].equals(" ")) {
					throw new AutoException(ErrorType.NO_BASEPRICE.ordinal(), "Exception: Base price is not found!");
				}
			} catch (AutoException ae) {
				fl[1] = ae.fix(ErrorType.NO_BASEPRICE.ordinal());
			} finally {
				basePrice = Float.parseFloat(fl[1]);
			}
				
			sizeOptionSet = Integer.parseInt(fl[2]);
		} catch(NumberFormatException e) {
			System.out.println("Error-- " + e.toString());
		}
		Automobile au = new Automobile(name, basePrice, sizeOptionSet);
		for(int i=0; i<sizeOptionSet; i++) {
			line = br.readLine();
			if(line == null) {
				return null;
			}
			oneLine(line, i, au);
		}
		br.close();
		return au;
	}
	/**
	 * Deal with the first line.
	 * @param s
	 * @return
	 */
	/*
	public int firstLine(String s) {
		String[] st = s.split(",");
		if(st.length < 3) {
			return 0;
		}
		
		int num = 0;
		try {
		    num = Integer.parseInt(st[2]);
		} catch(NumberFormatException e) {
			System.out.println("Error-- " + e.toString());
		}
		return num;
	}
	*/
	public String[] firstLine(String s) {
		String[] st = s.split(",");
		if(st.length != 3) {
			return null;
		}
		fl = st;
		/*
		int num = 0;
		try {
		    num = Integer.parseInt(st[2]);
		} catch(NumberFormatException e) {
			System.out.println("Error-- " + e.toString());
		}*/
		return fl;
	}
	/**
	 * Deal with one line for the remaining lines.
	 * @param s is a String
	 * @param n_opset is the No. of OptionSet
	 * @param au is an instance of Automotive.
	 */
	public void oneLine(String s, int n_opset, Automobile au) {
		String[] s_part = s.split(":");
		String optionSetName = s_part[0];
		String[] pair = s_part[1].split(",");
		int size_OptionSet = pair.length;
		au.setOptionSet(n_opset, optionSetName, size_OptionSet);
		String[] word;
		for(int i=0; i<size_OptionSet; i++) {
			word = pair[i].split("\\$");
			try {
				if(word[1].equals(" ")) {
					throw new AutoException(ErrorType.NO_OPTIONPRICE.ordinal(), "Exception: Price is not found!");
				}
			} catch(AutoException ae) {
				word[1] = ae.fix(ErrorType.NO_OPTIONPRICE.ordinal());		
			} finally {
			au.setOption(n_opset, i, word[0], Float.parseFloat(word[1]));
			}
		}
	}
}
