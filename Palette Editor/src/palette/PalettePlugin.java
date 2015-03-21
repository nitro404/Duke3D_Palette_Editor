package palette;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import exception.*;
import utilities.*;
import console.*;
import variable.*;
import plugin.*;
import gui.*;

public class PalettePlugin extends Plugin {
	
	protected Vector<String> m_supportedPaletteFileFormats;
	protected String m_paletteClassName;
	protected String m_palettePanelClassName;
	protected Class<?> m_paletteClass;
	protected Class<?> m_palettePanelClass;
	
	public static final String PLUGIN_TYPE = "Palette";
	
	public PalettePlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryName) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryName);
		m_supportedPaletteFileFormats = new Vector<String>();
		m_paletteClassName = null;
		m_palettePanelClassName = null;
		m_paletteClass = null;
		m_palettePanelClass = null;
	}
	
	public String getType() {
		return PLUGIN_TYPE;
	}
	
	public int numberOfSupportedPaletteFileFormats() {
		return m_supportedPaletteFileFormats.size();
	}
	
	public String getSupportedPaletteFileFormat(int index) {
		if(index < 0 || index >= m_supportedPaletteFileFormats.size()) { return null; }
		return m_supportedPaletteFileFormats.elementAt(index);
	}
	
	public String getSupportedPaletteFileFormatsAsString() {
		String listOfSupportedPaletteFileFormats = "";
		
		for(int i=0;i<m_supportedPaletteFileFormats.size();i++) {
			listOfSupportedPaletteFileFormats += m_supportedPaletteFileFormats.elementAt(i);
			
			if(i < m_supportedPaletteFileFormats.size() - 1) {
				listOfSupportedPaletteFileFormats += ", ";
			}
		}
		
		return listOfSupportedPaletteFileFormats;
	}
	
	public Vector<String> getSupportedPaletteFileFormats() {
		return m_supportedPaletteFileFormats;
	}
	
	public boolean hasSupportedPaletteFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedfileFormat = fileFormat.trim();
		if(formattedfileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedPaletteFileFormats.size();i++) {
			if(m_supportedPaletteFileFormats.elementAt(i).equalsIgnoreCase(formattedfileFormat)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSharedSupportedPaletteFileFormat(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return false; }
		
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileFormats();i++) {
			if(hasSupportedPaletteFileFormat(palettePlugin.getSupportedPaletteFileFormat(i))) {
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSharedSupportedPaletteFileFormats(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return 0; }
		
		int numberOfSharedSupportedPaletteFileFormats = 0;
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileFormats();i++) {
			if(hasSupportedPaletteFileFormat(palettePlugin.getSupportedPaletteFileFormat(i))) {
				numberOfSharedSupportedPaletteFileFormats++;
			}
		}
		return numberOfSharedSupportedPaletteFileFormats;
	}

	public Vector<String> getSharedSupportedPaletteFileFormats(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return null; }
		
		Vector<String> sharedSupportedPaletteFileFormats = new Vector<String>();
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileFormats();i++) {
			if(hasSupportedPaletteFileFormat(palettePlugin.getSupportedPaletteFileFormat(i))) {
				sharedSupportedPaletteFileFormats.add(palettePlugin.getSupportedPaletteFileFormat(i));
			}
		}
		return sharedSupportedPaletteFileFormats;
	}
	
	public String getSharedSupportedPaletteFileFormatsAsString(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return null; }
		
		String sharedSupportedPaletteFileFormats = new String();
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileFormats();i++) {
			if(hasSupportedPaletteFileFormat(palettePlugin.getSupportedPaletteFileFormat(i))) {
				if(sharedSupportedPaletteFileFormats.length() > 0) {
					sharedSupportedPaletteFileFormats += ", ";
				}
				
				sharedSupportedPaletteFileFormats += palettePlugin.getSupportedPaletteFileFormat(i);
			}
		}
		return sharedSupportedPaletteFileFormats;
	}
	
	public int indexOfSupportedPaletteFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		for(int i=0;i<m_supportedPaletteFileFormats.size();i++) {
			if(m_supportedPaletteFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addSupportedPaletteFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedPaletteFileFormats.size();i++) {
			if(m_supportedPaletteFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return false;
			}
		}
		
		m_supportedPaletteFileFormats.add(formattedFileFormat);
		
		return true;
	}
	
	public String getPaletteClassName() {
		return m_paletteClassName;
	}
	
	public String getPalettePanelClassName() {
		return m_palettePanelClassName;
	}
	
	public Class<?> getPaletteClass() {
		return m_paletteClass;
	}
	
	public Class<?> getPalettePanelClass() {
		return m_palettePanelClass;
	}
	
	public Palette getNewPaletteInstance(File paletteFile) throws PaletteInstantiationException {
		if(m_palettePanelClass == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_paletteClass.getDeclaredConstructor(File.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new PaletteInstantiationException("Palette class \"" + m_paletteClassName + "\" must contain a constructor which takes a File as an argument.");
		}
		
		Palette newPalette = null;
		try {
			newPalette = (Palette) constructor.newInstance(paletteFile);
		}
		catch(Exception e) {
			throw new PaletteInstantiationException("Failed to instantiate palette class \"" + m_paletteClassName + "\": " + e.getMessage());
		}
		
		return newPalette;
	}
	
	public PalettePanel getNewPalettePanelInstance(Palette palette) throws PalettePanelInstantiationException {
		if(m_palettePanelClass == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_palettePanelClass.getDeclaredConstructor(Palette.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new PalettePanelInstantiationException("Palette panel class \"" + m_palettePanelClassName + "\" must contain a constructor which takes a Palette as an argument.");
		}
		
		PalettePanel newPalettePanel = null;
		try {
			newPalettePanel = (PalettePanel) constructor.newInstance(palette);
		}
		catch(Exception e) {
			throw new PalettePanelInstantiationException("Failed to instantiate palette panel class \"" + m_palettePanelClassName + "\": " + e.getMessage());
		}
		
		return newPalettePanel;
	}
	
	public static boolean isPalettePlugin(File file) {
		return Plugin.isPluginOfType(file, PLUGIN_TYPE);
	}
	
	protected boolean loadFromCFGFile(BufferedReader in) throws PluginLoadException {
		if(in == null) { return false; }
		
		String input = null;
		String line = null;
		Variable v = null;
		String supportedPaletteFileFormats = null;
		String paletteClassName = null;
		String palettePanelClassName = null;
		
		try {
			while(true) {
				input = in.readLine();
				if(input == null) {
					try { in.close(); } catch(IOException e) { }
					
					throw new PalettePluginLoadException("Unexpected end of file encountered when reading \"" + m_name + "\" plugin definition file.");
				}
				
				line = input.trim();
				if(line.length() == 0 || Utilities.isComment(line)) { continue; }
				
				v = Variable.parseFrom(line);
				if(v == null) { continue; }
				
				if(v.getID().equalsIgnoreCase("Supported Palette File Formats")) {
					supportedPaletteFileFormats = v.getValue();
					
					String supportedPaletteFileFormat = null;
					String supportedPaletteFileFormatList[] = supportedPaletteFileFormats.split("[;, \t]");
					for(int i=0;i<supportedPaletteFileFormatList.length;i++) {
						supportedPaletteFileFormat = supportedPaletteFileFormatList[i].trim();
						if(supportedPaletteFileFormat.length() > 0) {
							addSupportedPaletteFileFormat(supportedPaletteFileFormat);
						}
					}
					
					if(numberOfSupportedPaletteFileFormats() == 0) {
						try { in.close(); } catch(IOException e) { }
						
						throw new PalettePluginLoadException("Palette plugin \"" + m_name + "\" must support at least one file format.");
					}
				}
				else if(v.getID().equalsIgnoreCase("Palette Class Name")) {
					if(paletteClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for palette class name in \"" + m_name + "\" plugin definition file.");
					}
					
					paletteClassName = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Palette Panel Class Name")) {
					if(palettePanelClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for palette panel class name in \"" + m_name + "\" plugin definition file.");
					}
					
					palettePanelClassName = v.getValue();
				}
				else {
					SystemConsole.instance.writeLine("Encountered unexpected property \"" + v.getID() + "\" in \"" + m_name + "\" plugin definition file.");
				}
				
				if(supportedPaletteFileFormats != null && paletteClassName != null && palettePanelClassName != null) {
					break;
				}
			}
		}
		catch(IOException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new PalettePluginLoadException("Read exception thrown while reading \"" + m_name + "\" plugin definition file: " + e.getMessage());
		}
		
		m_paletteClassName = paletteClassName;
		m_palettePanelClassName = palettePanelClassName;
		
		m_paletteClass = null;
		try { m_paletteClass = ExtendedClassLoader.instance.loadClass(m_paletteClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new PalettePluginLoadException("Class " + m_paletteClassName + " is missing or not loaded.");
		}
		if(!(Palette.class.isAssignableFrom(m_paletteClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Class " + m_paletteClassName + " does not extend Palette class.");
		}
		
		m_palettePanelClass = null;
		try { m_palettePanelClass = ExtendedClassLoader.instance.loadClass(m_palettePanelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new PalettePluginLoadException("Class " + m_palettePanelClassName + " is missing or not loaded.");
		}
		if(!(PalettePanel.class.isAssignableFrom(m_palettePanelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Class " + m_palettePanelClassName + " does not extend PalettePanel class.");
		}
		
		return true;
	}
	
	protected boolean loadFromXMLFile(BufferedReader in, XMLEventReader eventReader) throws PluginLoadException {
		if(in == null || eventReader == null) { return false; }
		
		XMLEvent event = null;
		String node = null;
		Iterator<?> attributes = null;
		Attribute attribute = null;
		String attributeName = null;
		String attributeValue = null;
		String extension = null;
		String className = null;
		String classType = null;
		String paletteClassName = null;
		String palettePanelClassName = null;
		
		try {
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					node = event.asStartElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("formats")) {
						while(eventReader.hasNext()) {
							event = eventReader.nextEvent();
							
							if(event.isStartElement()) {
								node = event.asStartElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("format")) {
									attributes = event.asStartElement().getAttributes();
									
									while(attributes.hasNext()) {
										attribute = (Attribute) attributes.next();
										
										attributeName = attribute.getName().toString();
										attributeValue = attribute.getValue().toString().trim();
										
										if(attributeName.equalsIgnoreCase("extension")) {
											if(extension != null) {
												SystemConsole.instance.writeLine("Attribute \"extension\" specified multiple times inside format XML node of plugin definition file for palette plugin \"" + m_name + "\".");
											}
											
											extension = attributeValue;
										}
										else {
											SystemConsole.instance.writeLine("Unexpected XML node attribute encounted inside format node: \"" + attributeName + "\", expected \"extension\"."); 
										}
									}
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside formats node: \"" + node + "\", expected \"format\".");
								}
							}
							else if(event.isEndElement()) {
								node = event.asEndElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("format")) {
									if(extension == null) {
										SystemConsole.instance.writeLine("Missing \"extension\" attribute in format XML node of plugin definition file for palette plugin \"" + m_name + "\".");
									}
									else if(extension.length() == 0) {
										SystemConsole.instance.writeLine("Encountered empty file extension in format XML node of plugin definition file for palette plugin \"" + m_name + "\".");
									}
									else {
										addSupportedPaletteFileFormat(extension);
										
										extension = null;
									}
								}
								else if(node.equalsIgnoreCase("formats")) {
									break;
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node close tag encountered inside formats node: \"" + node + "\", expected \"format\" or \"formats\".");
								}
							}
						}
					}
					else if(node.equalsIgnoreCase("classes")) {
						while(eventReader.hasNext()) {
							event = eventReader.nextEvent();
							
							if(event.isStartElement()) {
								node = event.asStartElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("class")) {
									attributes = event.asStartElement().getAttributes();
									
									while(attributes.hasNext()) {
										attribute = (Attribute) attributes.next();
										
										attributeName = attribute.getName().toString();
										attributeValue = attribute.getValue().toString().trim();
										
										if(attributeName.equalsIgnoreCase("type")) {
											if(classType != null) {
												SystemConsole.instance.writeLine("Attribute \"type\" specified multiple times inside class XML node of plugin definition file for palette plugin \"" + m_name + "\".");
											}
											
											classType = attributeValue;
										}
										else if(attributeName.equalsIgnoreCase("name")) {
											if(className != null) {
												SystemConsole.instance.writeLine("Attribute \"name\" specified multiple times inside class XML node of plugin definition file for palette plugin \"" + m_name + "\".");
											}
											
											className = attributeValue;
										}
										else {
											SystemConsole.instance.writeLine("Unexpected XML node attribute encounted inside class node: \"" + attributeName + "\", expected \"type\" or \"name\"."); 
										}
									}
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside classes node: \"" + node + "\", expected \"class\".");
								}
							}
							else if(event.isEndElement()) {
								node = event.asEndElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("class")) {
									if(classType == null || className == null) {
										try { in.close(); } catch(IOException e) { }
										
										throw new PalettePluginLoadException("Missing attribute in class XML node, both \"type\" and \"name\" must be specified.");
									}
									else if(classType.equalsIgnoreCase("Palette")) {
										if(paletteClassName != null) {
											SystemConsole.instance.writeLine("Palette class specified multiple times inside classes XML node of plugin definition file for palette plugin \"" + m_name + "\".");
										}
										
										paletteClassName = className;
										
										if(paletteClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty palette class name specified inside classes XML node of plugin definition file for palette plugin \"" + m_name + "\".");
										}
									}
									else if(classType.equalsIgnoreCase("Palette Panel")) {
										if(palettePanelClassName != null) {
											SystemConsole.instance.writeLine("Palette panel class specified multiple times inside classes XML node of plugin definition file for palette plugin \"" + m_name + "\".");
										}
										
										palettePanelClassName = className;
										
										if(palettePanelClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty palette panel class name specified inside classes XML node of plugin definition file for palette plugin \"" + m_name + "\".");
										}
									}
									else {
										SystemConsole.instance.writeLine("Unexpected class type encountered inside classes node: \"" + node + "\", expected \"Palette\" or \"Palette Panel\".");
									}
									
									className = null;
									classType = null;
								}
								else if(node.equalsIgnoreCase("classes")) {
									break;
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node close tag encountered inside classes node: \"" + node + "\", expected \"class\" or \"classes\".");
								}
							}
						}
					}
					else {
						SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside plugin node: \"" + node + "\", expected \"formats\" or \"classes\".");
					}
				}
				else if(event.isEndElement()) {
					node = event.asEndElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("plugin")) {
						break;
					}
					else {
						SystemConsole.instance.writeLine("Unexpected XML node close tag encountered: \"" + node + "\", expected \"plugin\".");
					}
				}
			}
		}
		catch(XMLStreamException e) {
			try { in.close(); } catch (IOException e2) { }
			
			throw new PluginLoadException("XML exception thrown while reading plugin definition file: " + e.getMessage());
		}
		
		if(numberOfSupportedPaletteFileFormats() == 0) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Palette plugin \"" + m_name + "\" must support at least one file format.");
		}
		
		if(paletteClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Palette plugin \"" + m_name + "\" missing palette class name specification in plugin definition file.");
		}
		
		if(palettePanelClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Palette plugin \"" + m_name + "\" missing palette panel class name specification in plugin definition file.");
		}
		
		m_paletteClassName = paletteClassName;
		m_palettePanelClassName = palettePanelClassName;
		
		m_paletteClass = null;
		try { m_paletteClass = ExtendedClassLoader.instance.loadClass(m_paletteClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new PalettePluginLoadException("Class " + m_paletteClassName + " is missing or not loaded.");
		}
		if(!(Palette.class.isAssignableFrom(m_paletteClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Class " + m_paletteClassName + " does not extend Palette class.");
		}
		
		m_palettePanelClass = null;
		try { m_palettePanelClass = ExtendedClassLoader.instance.loadClass(m_palettePanelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new PalettePluginLoadException("Class " + m_palettePanelClassName + " is missing or not loaded.");
		}
		if(!(PalettePanel.class.isAssignableFrom(m_palettePanelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PalettePluginLoadException("Class " + m_palettePanelClassName + " does not extend PalettePanel class.");
		}
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof PalettePlugin)) { return false; }
		
		PalettePlugin p = ((PalettePlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
}
