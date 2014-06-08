package utilities;

import java.awt.*;
import java.io.*;

public class Utilities {
	
	private Utilities() { }
	
	public static Point parsePoint(String data) {
		if(data == null) { return null; }
		
		String[] pointData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(pointData.length != 2) { return null; }
		
		int x, y;
		try { x = Integer.parseInt(pointData[0]); } catch(NumberFormatException e) { return null; }
		try { y = Integer.parseInt(pointData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Point(x, y);
	}
	
	public static Dimension parseDimension(String data) {
		if(data == null) { return null; }
		
		String[] dimensionData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(dimensionData.length != 2) { return null; }
		
		int width, height;
		try { width = Integer.parseInt(dimensionData[0]); } catch(NumberFormatException e) { return null; }
		try { height = Integer.parseInt(dimensionData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Dimension(width, height);
	}
	
	public static Color parseColour(String data) {
		if(data == null) { return null; }
		
		String[] colourData = data.replaceAll("^[^-0-9.]+", "").replaceAll("[^-0-9.]+$", "").split("[^-0-9.]+");
		if(colourData.length != 3) { return null; }
		
		boolean normalized = false;
		for(int i=0;i<colourData.length;i++) {
			if(colourData[i].contains(".")) {
				normalized = true;
			}
		}
		
		if(normalized) {
			float[] colours = new float[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Float.parseFloat(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0.0f || colours[i] > 1.0f) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
		else {
			int[] colours = new int[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Integer.parseInt(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0 || colours[i] > 255) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
	}
	
	public static int compareCasePercentage(String text) {
		if(text == null) { return 0; }
		
		int upper = 0;
		int lower = 0;
		for(int i=0;i<text.length();i++) {
			if(text.charAt(i) >= 'a' && text.charAt(i) <= 'z') { lower++; }
			if(text.charAt(i) >= 'A' && text.charAt(i) <= 'Z') { upper++; }
		}
		
		return upper - lower;
	}
	
	public static String getFileNameNoExtension(String fileName) {
		if(fileName == null) { return null; }
		
		int index = fileName.lastIndexOf('.');
		if(index > 0) {
			return fileName.substring(0, index);
		}
		return fileName;
	}
	
	public static String getFileExtension(String fileName) {
		if(fileName == null) { return null; }
		
		int index = fileName.lastIndexOf('.');
		if(index > 0) {
			return fileName.substring(index + 1, fileName.length());
		}
		return null;
	}
	
	public static boolean fileHasExtension(String fileName, String fileExtension) {
		if(fileName == null || fileExtension == null) { return false; }
		
		String actualFileExtension = getFileExtension(fileName);
		return actualFileExtension != null && actualFileExtension.equalsIgnoreCase(fileExtension);
	}
	
	public static String getFilePath(File file) {
		if(file == null) { return null; }
		
		String path;
		try {
			path = file.getCanonicalPath();
		}
		catch(IOException e) {
			path = file.getAbsolutePath();
		}
		
		int index = -1;
		for(int i=path.length() - 1;i>=0;i--) {
			if(path.charAt(i) == '/' || path.charAt(i) == '\\') {
				index = i;
				break;
			}
		}
		
		if(index >= 0) {
			path = path.substring(0, index + 1);
		}
		
		if(path.charAt(path.length() - 1) != '/' && path.charAt(path.length() - 1) != '\\') {
			path += "/";
		}
		
		return path;
	}
	
	public static String appendSlash(String path) {
		if(path == null) { return null; }
		String data = path.trim();
		if(data.length() == 0) { return data; }
		
		if(data.charAt(data.length() - 1) != '/' && data.charAt(data.length() - 1) != '\\') {
			data  += "/";
		}
		
		return data;
	}
	
	public static int compareVersions(String v1, String v2) {
		if(v1 == null || v2 == null) {
			throw new IllegalArgumentException("Cannot compare to a null version.");
		}
		
		String version1 = v1.trim();
		String version2 = v2.trim();
		if(version1.length() == 0 || version2.length() == 0) {
			throw new IllegalArgumentException("Cannot compare empty versions.");
		}
		
		String matchRegex = "([0-9]\\.?)+";
		if(!version1.matches(matchRegex) || !version2.matches(matchRegex)) {
			throw new IllegalArgumentException("Cannot compare improperly formatted versions.");
		}
		
		String splitRegex = "[\\. \\t]+";
		String v1parts[] = version1.split(splitRegex);
		String v2parts[] = version2.split(splitRegex);
		
		int a, b;
		int index = 0;
		while(true) {
			if(index >= v1parts.length) {
				if(v1parts.length == v2parts.length) { return 0; }
				
				for(int i=index;i<v2parts.length;i++) {
					b = Integer.parseInt(v2parts[i]);
					
					if(b != 0) {
						return -1;
					}
				}
				return 0;
			}
			
			if(index >= v2parts.length) {
				if(v2parts.length == v1parts.length) { return 0; }
				
				for(int i=index;i<v1parts.length;i++) {
					a = Integer.parseInt(v1parts[i]);
					
					if(a != 0) {
						return 1;
					}
				}
				return 0;
			}
			
			a = Integer.parseInt(v1parts[index]);
			b = Integer.parseInt(v2parts[index]);
			
			     if(a > b) { return  1; }
			else if(a < b) { return -1; }
			
			index++;
		}
	}
	
}
