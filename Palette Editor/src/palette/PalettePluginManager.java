package palette;

import java.util.*;
import java.io.*;
import javax.swing.*;
import variable.*;
import settings.*;
import console.*;
import plugin.*;

public class PalettePluginManager extends PluginManager {
	
	public static PalettePluginManager instance = null;
	
	public PalettePluginManager() {
		if(instance == null) {
			updateInstance();
		}
		
		addPluginType(PalettePlugin.PLUGIN_TYPE, PalettePlugin.class);
	}
	
	public void updateInstance() {
		instance = this;
	}
	
	public Vector<PalettePlugin> getPalettePluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		PalettePlugin palettePlugin = null;
		Vector<PalettePlugin> palettePlugins = new Vector<PalettePlugin>();
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedPaletteFileFormats();j++) {
				if(palettePlugin.getSupportedPaletteFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					palettePlugins.add(palettePlugin);
				}
			}
		}
		return palettePlugins;
	}
	
	public boolean hasPalettePluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedPaletteFileFormats();j++) {
				if(palettePlugin.getSupportedPaletteFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int numberOfPalettePluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return 0; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return 0; }
		
		int numberOfPalettePlugins = 0;
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedPaletteFileFormats();j++) {
				if(palettePlugin.getSupportedPaletteFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					numberOfPalettePlugins++;
				}
			}
		}
		return numberOfPalettePlugins;
	}
	
	public int indexOfFirstPalettePluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedPaletteFileFormats();j++) {
				if(palettePlugin.getSupportedPaletteFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String getPalettePluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		String palettePluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				if(palettePlugin.hasSupportedPaletteFileFormat(formattedFileFormat)) {
					if(palettePluginsList.length() > 0) {
						palettePluginsList += ", ";
					}
					
					palettePluginsList += palettePlugin.getName();
				}
			}
		}
		
		return palettePluginsList;
	}
	
	public Vector<PalettePlugin> getPalettePluginsExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		Vector<PalettePlugin> palettePlugins = new Vector<PalettePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				if(palettePlugin.hasSupportedPaletteFileFormat(formattedFileFormat)) {
					palettePlugins.add(palettePlugin);
				}
			}
		}
		
		return palettePlugins;
	}
	
	public Vector<String> getSupportedPaletteFileFormats() {
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		String fileFormat = null;
		boolean duplicateFileFormat = false;
		Vector<String> fileFormats = new Vector<String>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				for(int j=0;j<palettePlugin.numberOfSupportedPaletteFileFormats();j++) {
					fileFormat = palettePlugin.getSupportedPaletteFileFormat(j).toUpperCase();
					duplicateFileFormat = false;
					
					for(int k=0;k<fileFormats.size();k++) {
						if(fileFormats.elementAt(k).equalsIgnoreCase(fileFormat)) {
							duplicateFileFormat = true;
							break;
						}
					}
					
					if(duplicateFileFormat) { continue; }
					
					fileFormats.add(fileFormat);
				}
			}
		}
		
		return fileFormats;
	}
	
	public Vector<String> getSupportedAndPreferredPaletteFileFormats() {
		Vector<String> fileFormats = new Vector<String>();
		
		Vector<String> supportedFileFormats = getSupportedPaletteFileFormats();
		
		if(supportedFileFormats != null) {
			for(int i=0;i<supportedFileFormats.size();i++) {
				fileFormats.add(supportedFileFormats.elementAt(i));
			}
		}
		
		Collection<String> preferredFileFormats = getPreferredFileFormats(PalettePlugin.class);
		
		boolean duplicateFileFormat = false;
		if(preferredFileFormats != null) {
			for(String preferredFileFormat : preferredFileFormats) {
				duplicateFileFormat = false;
				
				for(int j=0;j<fileFormats.size();j++) {
					if(fileFormats.elementAt(j).equalsIgnoreCase(preferredFileFormat)) {
						duplicateFileFormat = true;
					}
				}
				
				if(duplicateFileFormat) { continue; }
				
				fileFormats.add(preferredFileFormat);
			}
		}
		
		return fileFormats;
	}
	
	public PalettePlugin getPreferredPalettePluginPrompt(String fileFormat) {
		if(fileFormat == null) { return null; }
		
		Vector<PalettePlugin> plugins = getPalettePluginsForFileFormat(fileFormat);
		if(plugins == null || plugins.size() == 0) { return null; }
		
		PalettePlugin plugin = null;
		
		if(hasPreferredPluginForFileFormat(fileFormat, PalettePlugin.class)) {
			String preferredPluginName = getPreferredPluginForFileFormat(fileFormat, PalettePlugin.class);
			plugin = getPlugin(preferredPluginName, PalettePlugin.class);
			
			// if the preferred plugin is not loaded
			if(plugin == null) {
				// get a collection of all unloaded plugins
				VariableCollection unloadedPlugins = getUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
				
				// get the info for the currently unloaded preferred plugin
				Variable preferredPluginInfo = unloadedPlugins.getVariable(preferredPluginName);
				
				// if the preferred plugin does not exist, run the plugin selection prompt
				if(preferredPluginInfo == null) {
					plugin = null;
				}
				// otherwise if the preferred plugin is currently unloaded, prompt the user to load it
				else {
					int choice = JOptionPane.showConfirmDialog(null, "Preferred plugin is not loaded. Would you like to load it or choose a different plugin?", "Plugin Not Loaded", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
					if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION) { plugin = null; }
					else if(choice == JOptionPane.YES_OPTION) {
						if(!loadPlugin(preferredPluginInfo.getID(), preferredPluginInfo.getValue())) {
							plugin = null;
							
							String message = "Failed to load preferred plugin \"" + preferredPluginInfo.getID() + "\"!";
							
							SystemConsole.instance.writeLine(message);
							
							JOptionPane.showMessageDialog(null, message, "Plugin Loading Failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
		
		boolean preferredPluginSelected = plugin != null;
		
		if(plugin == null) {
			plugin = plugins.elementAt(0);
		}
		
		if(plugins.size() > 1 && !preferredPluginSelected) {
			int pluginIndex = -1;
			Object choices[] = plugins.toArray();
			Object value = JOptionPane.showInputDialog(null, "Found multiple plugins supporting this file format.\nChoose a plugin to process this file with:", "Choose Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			if(value == null) { return null; }
			for(int i=0;i<choices.length;i++) {
				if(choices[i] == value) {
					pluginIndex = i;
					break;
				}
			}
			if(pluginIndex < 0 || pluginIndex >= plugins.size()) { return null; }
			
			plugin = plugins.elementAt(pluginIndex);
			
			String currentPreferredPluginName = getPreferredPluginForFileFormat(fileFormat, PalettePlugin.class);
			if(currentPreferredPluginName == null || !currentPreferredPluginName.equalsIgnoreCase(plugin.getName())) {
				int choice = JOptionPane.showConfirmDialog(null, "Would you like to set this plugin as your preferred plugin for the " + fileFormat + " file format?", "Set Preferred Plugin", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.YES_OPTION) {
					if(!setPreferredPluginForFileFormat(fileFormat, plugin.getName(), PalettePlugin.class)) {
						SystemConsole.instance.writeLine("Failed to set \"" + plugin.getName() + "\" as preferred plugin for " + fileFormat + " file format.");
					}
				}
			}
		}
		
		return plugin;
	}
	
}
