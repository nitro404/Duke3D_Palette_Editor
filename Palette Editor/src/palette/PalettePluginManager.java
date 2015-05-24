package palette;

import java.util.*;
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
	
}
