package settings;

import java.awt.*;
import variable.*;
import utilities.*;
import gui.*;

public class SettingsManager {
	
	public static SettingsManager instance = null;
	
	private VariableCollection m_settings;
	
	public String settingsFileName = defaultSettingsFileName;
	public String versionFileURL = defaultVersionFileURL;
	public boolean autoSaveSettings;
	public boolean autoLoadPlugins;
	public String pluginDirectoryName;
	public String consoleLogFileName;
	public String logDirectoryName;
	public boolean logConsole;
	public boolean supressUpdates;
	public int pixelButtonSize;
	public int paletteSpacing;
	public int windowPositionX;
	public int windowPositionY;
	public int windowWidth;
	public int windowHeight;
	public boolean autoScrollConsole;
	public int maxConsoleHistory;
	public Color backgroundColour;
	
	public static final String defaultSettingsFileName = "Palette Editor.ini";
	public static final String defaultVersionFileURL = "http://www.nitro404.com/version/duke3d_palette_editor.xml";
	public static final boolean defaultAutoSaveSettings = true;
	public static final boolean defaultAutoLoadPlugins = true;
	public static final String defaultPluginDirectoryName = "Plugins";
	public static final String defaultConsoleLogFileName = "Console.log";
	public static final String defaultLogDirectoryName = "Logs";
	public static final boolean defaultLogConsole = false;
	public static final boolean defaultSupressUpdates = false;
	public static final int defaultPixelButtonSize = PixelButton.BUTTON_SIZE;
	public static final int defaultPaletteSpacing = PalettePanel.PALETTE_SPACING;
	public static final int defaultWindowPositionX = 0;
	public static final int defaultWindowPositionY = 0;
	public static final int defaultWindowWidth = 800;
	public static final int defaultWindowHeight = 600;
	public static final boolean defaultAutoScrollConsole = true;
	public static final int defaultMaxConsoleHistory = 512;
	public static final Color defaultBackgroundColour = new Color(238, 238, 238);
	
	public SettingsManager() {
		if(instance == null) {
			updateInstance();
		}
		
		m_settings = new VariableCollection();
		reset();
	}

	public void updateInstance() {
		instance = this;
	}
	
	public void reset() {
		versionFileURL = defaultVersionFileURL;
		autoSaveSettings = defaultAutoSaveSettings;
		autoLoadPlugins = defaultAutoLoadPlugins;
		consoleLogFileName = defaultConsoleLogFileName;
		pluginDirectoryName = defaultPluginDirectoryName;
		logDirectoryName = defaultLogDirectoryName;
		logConsole = defaultLogConsole;
		supressUpdates = defaultSupressUpdates;
		pixelButtonSize = defaultPixelButtonSize;
		paletteSpacing = defaultPaletteSpacing;
		windowPositionX = defaultWindowPositionX;
		windowPositionY = defaultWindowPositionY;
		windowWidth = defaultWindowWidth;
		windowHeight = defaultWindowHeight;
		autoScrollConsole = defaultAutoScrollConsole;
		maxConsoleHistory = defaultMaxConsoleHistory;
		backgroundColour = defaultBackgroundColour;
	}
	
	public boolean load() {
		return loadFrom(settingsFileName);
	}
	
	public boolean save() {
		return saveTo(settingsFileName);
	}
	
	public boolean loadFrom(String fileName) {
		VariableCollection variables = VariableCollection.readFrom(fileName);
		if(variables == null) { return false; }
		
		m_settings = variables;
		
		int tempInt = -1;
		String tempString = null;
		Point tempPoint = null;
		Dimension tempDimension = null;
		Color tempColour = null;
		
		// parse version file URL
		tempString = m_settings.getValue("Version File URL", "Paths");
		if(tempString != null) {
			versionFileURL = tempString;
		}
		
		// parse auto-save settings value
		tempString = m_settings.getValue("Auto-Save Settings", "Interface");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				autoSaveSettings = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				autoSaveSettings = false;
			}
		}
		
		// parse auto-load plugins value
		tempString = m_settings.getValue("Auto-Load Plugins", "Interface");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				autoLoadPlugins = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				autoLoadPlugins = false;
			}
		}
		
		// parse console log file name
		tempString = m_settings.getValue("Console Log File Name", "Paths");
		if(tempString != null) {
			consoleLogFileName = tempString;
		}
		
		// parse plugin directory name
		tempString = m_settings.getValue("Plugin Directory Name", "Paths");
		if(tempString != null) {
			pluginDirectoryName = tempString;
		}
		
		// parse log directory name
		tempString = m_settings.getValue("Log Directory Name", "Paths");
		if(tempString != null) {
			logDirectoryName = tempString;
		}
		
		// parse log console value
		tempString = m_settings.getValue("Log Console", "Console");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				logConsole = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				logConsole = false;
			}
		}
		
		// parse supress update notifications value
		tempString = m_settings.getValue("Supress Update Notifications", "Interface");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				supressUpdates = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				supressUpdates = false;
			}
		}

		// parse pixel button size
		tempInt = -1;
		try { tempInt = Integer.parseInt(m_settings.getValue("Pixel Button Size", "Interface")); } catch(NumberFormatException e) { } 
		if(tempInt >= 1) { pixelButtonSize = tempInt; }
		
		// parse palette spacing
		tempInt = -1;
		try { tempInt = Integer.parseInt(m_settings.getValue("Palette Spacing", "Interface")); } catch(NumberFormatException e) { } 
		if(tempInt >= 1) { paletteSpacing = tempInt; }
		
		// parse window position
		tempPoint = Utilities.parsePoint(m_settings.getValue("Window Position", "Interface"));
		if(tempPoint != null && tempPoint.x > 0 && tempPoint.y > 0) {
			windowPositionX = tempPoint.x;
			windowPositionY = tempPoint.y;
		}
		
		// parse window size
		tempDimension = Utilities.parseDimension(m_settings.getValue("Window Size", "Interface"));
		if(tempDimension != null && tempDimension.width > 0 && tempDimension.height > 0) {
			windowWidth = tempDimension.width;
			windowHeight = tempDimension.height;
		}
		
		// parse console auto-scrolling
		tempString = m_settings.getValue("Auto-Scroll Console", "Console");
		if(tempString != null) {
			tempString = tempString.trim().toLowerCase();
			if(tempString.equals("true")) {
				autoScrollConsole = true;
			}
			else if(tempString.equals("false")) {
				autoScrollConsole = false;
			}
		}

		// parse max console history
		tempInt = -1;
		try { tempInt = Integer.parseInt(m_settings.getValue("Max Console History", "Console")); } catch(NumberFormatException e) { } 
		if(tempInt >= 1) { maxConsoleHistory = tempInt; }
		
		// parse background colour
		tempColour = Utilities.parseColour(variables.getValue("Background Colour", "Interface"));
		if(tempColour != null) { backgroundColour = tempColour; }
		
		return true;
	}
	
	public boolean saveTo(String fileName) {
		// update variables collection
		m_settings.setValue("Version File URL", versionFileURL, "Paths");
		m_settings.setValue("Auto-Save Settings", autoSaveSettings, "Interface");
		m_settings.setValue("Auto-Load Plugins", autoLoadPlugins, "Interface");
		m_settings.setValue("Console Log File Name", consoleLogFileName, "Paths");
		m_settings.setValue("Plugin Directory Name", pluginDirectoryName, "Paths");
		m_settings.setValue("Log Directory Name", logDirectoryName, "Paths");
		m_settings.setValue("Log Console", logConsole, "Console");
		m_settings.setValue("Supress Update Notifications", supressUpdates, "Interface");
		m_settings.setValue("Pixel Button Size", pixelButtonSize, "Interface");
		m_settings.setValue("Palette Spacing", paletteSpacing, "Interface");
		m_settings.setValue("Window Position", windowPositionX + ", " + windowPositionY, "Interface");
		m_settings.setValue("Window Size", windowWidth + ", " + windowHeight, "Interface");
		m_settings.setValue("Auto-Scroll Console", autoScrollConsole, "Console");
		m_settings.setValue("Max Console History", maxConsoleHistory, "Console");
		m_settings.setValue("Background Colour", backgroundColour.getRed() + ", " + backgroundColour.getGreen() + ", " + backgroundColour.getBlue(), "Interface");
		
		m_settings.sort();
		
		return m_settings.writeTo(fileName);
	}
	
}
