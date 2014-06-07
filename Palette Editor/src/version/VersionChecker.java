package version;

import java.io.*;
import java.net.*;
import javax.xml.namespace.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.swing.*;
import utilities.*;
import palette.*;

public class VersionChecker {
	
	public static final String PROGRAM = "program";
	public static final String NAME = "name";
	public static final String VERSION = "version";
	public static final String DATE = "date";
	public static final String LINK = "link";
	
	private VersionChecker() {
		
	}
	
	public static void checkVersion() {
		checkVersion(true);
	}
	
	public static void checkVersion(final boolean verbose) {
		new Thread(
			new Runnable() {
				public void run() {
					checkVersionHelper(verbose);
				}
			}
		).start();
	}
	
	private static boolean checkVersionHelper(boolean verbose) {
		if(PaletteEditor.settings.versionFileURL == null) {
			PaletteEditor.console.writeLine("Version file URL not set, maybe reset your settings?");
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Version file URL not set, maybe reset your settings?", "Invalid Version File URL", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		URL url;
		try {
			url = new URL(PaletteEditor.settings.versionFileURL);
		}
		catch(MalformedURLException e) {
			PaletteEditor.console.writeLine("Version file URL is invalid or malformed, please check that it is correct or reset your settings:" + e.getMessage());
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Version file URL is invalid or malformed, please check that it is correct or reset your settings:" + e.getMessage(), "Invalid URL", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		InputStream in;
		try {
			in = url.openStream();
		}
		catch(IOException e) {
			PaletteEditor.console.writeLine("Failed to open stream to version file, perhaps the url is wrong or the file is missing?");
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Failed to open stream to version file, perhaps the url is wrong or the file is missing?", "IO Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		String name = null;
		String version = null;
		String date = null;
		String link = null;
		XMLEvent event = null;
		StartElement element = null;
		Attribute temp = null;
		
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					element = event.asStartElement();
					
					if(element.getName().getLocalPart().equalsIgnoreCase(PROGRAM)) {
						temp = element.getAttributeByName(new QName(NAME));
						if(temp != null) { name = temp.getValue().trim(); }
						if(!name.equalsIgnoreCase("Duke Nukem 3D Palette Editor")) {
							PaletteEditor.console.writeLine("Program name in version file does not match name of program: \"" + name + "\".");
						}
						
						temp = element.getAttributeByName(new QName(VERSION));
						if(temp != null) { version = temp.getValue().trim(); }
						
						temp = element.getAttributeByName(new QName(DATE));
						if(temp != null) { date = temp.getValue().trim(); }
						
						temp = element.getAttributeByName(new QName(LINK));
						if(temp != null) { link = temp.getValue().trim(); }
					}
				}
			}
			
			in.close();
		}
		catch(XMLStreamException e) {
			PaletteEditor.console.writeLine("XML stream exception thrown while attempting to read version file stream: " + e.getMessage());
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "XML stream exception thrown while attempting to read version file stream: " + e.getMessage(), "XML Stream Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		catch(IOException e) {
			PaletteEditor.console.writeLine("Read exception thrown while parsing version file.");
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Read exception thrown while parsing version file.", "IO Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		try {
			switch(Utilities.compareVersions(PaletteEditor.VERSION, version)) {
				case -1:
					PaletteEditor.console.writeLine("A new version of Duke Nukem 3D Palette Editor is available! Download version " + version + " at the following link: \"" + link + "\".");
					
					if(verbose || !PaletteEditor.settings.supressUpdates) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "A new version of Duke Nukem 3D Palette Editor is available! Released " + date + ".\nDownload version " + version + " at the following link: \"" + link + "\".", "New Version Available", JOptionPane.INFORMATION_MESSAGE); }
					
					break;
				case 0:
					PaletteEditor.console.writeLine("Duke Nukem 3D Palette Editor is up to date with version " + PaletteEditor.VERSION + ", released " + date + ".");
					
					if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Duke Nukem 3D Palette Editor is up to date with version " + PaletteEditor.VERSION + ", released " + date + ".", "Up to Date", JOptionPane.INFORMATION_MESSAGE); }
					
					break;
				case 1:
					PaletteEditor.console.writeLine("Wow, you're from the future? Awesome. Hope you're enjoying your spiffy version " + PaletteEditor.VERSION + " of the Duke Nukem 3D Palette Editor!");
					
					JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Wow, you're from the future? Awesome.\nHope you're enjoying your spiffy version " + PaletteEditor.VERSION + " of the Duke Nukem 3D Palette Editor!", "Hello Time Traveller!", JOptionPane.INFORMATION_MESSAGE);
					
					break;
			}
		}
		catch(NumberFormatException e) {
			PaletteEditor.console.writeLine("Version check failed: Illegal non-numerical value encountered while parsing version.");
			
			if(verbose) { JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Version check failed: Illegal non-numerical value encountered while parsing version.", "Invalid Version", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		catch(IllegalArgumentException e) { return false; }
		
		return true;
	}
	
}
