package palette;

import java.io.*;
import java.awt.*;
import utilities.*;
import exception.*;

public class PaletteACT extends Palette {
	
	protected byte m_data[];
	
	public static final int BPP = 3;
	public static final int PALETTE_SIZE_RGB = NUMBER_OF_COLOURS * BPP;
	public static final String FILE_TYPE = "ACT";
	public static final String PALETTE_DESCRIPTION = "Default";
	
	public PaletteACT() {
		this(null);
	}
	
	public PaletteACT(File file) {
		super(file);
		m_data = null;
	}
	
	public int numberOfFileTypes() {
		return 1;
	}
	
	public String getFileType(int index) {
		return index == 0 ? FILE_TYPE : null;
	}
	
	public int indexOfFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		return FILE_TYPE.equalsIgnoreCase(type) ? 0 : -1;
	}

	public String getPaletteDescription(int index) {
		return index == 0 ? PALETTE_DESCRIPTION : null;
	}
	
	public Color getPixel(int index, int x, int y) {
		if(!m_loaded || m_data == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return null; }
		
		// compute the offset in the palette data array, accounting for the the y position and x position
		// note that each pixel is 3 bytes (RGB), hence BBP (bytes per pixel)
		int offset = (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each unsigned byte to an integer
		int r = m_data[offset    ] & 0xFF;
		int g = m_data[offset + 1] & 0xFF;
		int b = m_data[offset + 2] & 0xFF;
		
		// check that each colour channel is between 0 and 255
		if(r < 0 || r > 255) { System.out.println(  "Red channel exceeded 0-255 boundary in " + FILE_TYPE + " file \"" + m_file.getName() + "\" with value: " + r + " at offset: " + offset); }
		if(g < 0 || g > 255) { System.out.println("Green channel exceeded 0-255 boundary in " + FILE_TYPE + " file \"" + m_file.getName() + "\" with value: " + g + " at offset: " + offset + 1); }
		if(b < 0 || b > 255) { System.out.println( "Blue channel exceeded 0-255 boundary in " + FILE_TYPE + " file \"" + m_file.getName() + "\" with value: " + b + " at offset: " + offset + 2); }
		
		return new Color(r < 0 ? 0 : (r > 255 ? 255 : r),  g < 0 ? 0 : (g > 255 ? 255 : g), b < 0 ? 0 : (b > 255 ? 255 : b));
	}
	
	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!m_loaded || m_data == null || c == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return false; }
		
		// compute the offset in the palette data array, accounting for the the y position and x position
		// note that each pixel is 3 bytes (RGB), hence BBP (bytes per pixel)
		int offset = (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each integer to a byte
		m_data[offset    ] = (byte) (c.getRed());
		m_data[offset + 1] = (byte) (c.getGreen());
		m_data[offset + 2] = (byte) (c.getBlue());
		
		return true;
	}

	public Color[] getColourData(int index) {
		if(index != 0 || !m_loaded || m_data == null) { return null; }
		
		// iterate over the entire palette and convert each piece of data to a Color object
		Color colourData[] = new Color[NUMBER_OF_COLOURS];
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				colourData[(j * PALETTE_WIDTH) + i] = getPixel(i, j);
			}
		}
		return colourData;
	}
	
	public boolean updateColourData(int index, int dataIndex, Color colourData[]) {
		if(index != 0) { return false; }
		
		// verify that the colour data is not truncated
		int dataOffset = (dataIndex * NUMBER_OF_COLOURS);
		if(colourData.length - dataOffset < NUMBER_OF_COLOURS) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!m_loaded) {
			if(isInstantiable()) {
				m_data = new byte[PALETTE_SIZE_RGB];
				m_loaded = true;
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire data array and replace each byte with
		// the bytes corresponding to each pixel in the new colour data array
		int offset = 0;
		int pixelIndex = 0;
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				// compute the offsets for the data array and colour data array
				offset = (j * PALETTE_HEIGHT * BPP) + (i * BPP);
				pixelIndex = dataOffset + (j * PALETTE_WIDTH) + i;
				
				// convert each integer to a byte
				m_data[offset    ] = (byte) (colourData[pixelIndex].getRed());
				m_data[offset + 1] = (byte) (colourData[pixelIndex].getGreen());
				m_data[offset + 2] = (byte) (colourData[pixelIndex].getBlue());
			}
		}
		
		return true;
	}

	public boolean fillWithColour(Color c, int index) {
		if(index > 0 || c == null) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!m_loaded) {
			if(isInstantiable()) {
				m_data = new byte[PALETTE_SIZE_RGB];
				m_loaded = true;
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire data array and replace each byte with the bytes corresponding to
		// the bytes that make up the replacement colour
		int offset = 0;
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				// compute the offset for the data array
				offset = (j * PALETTE_HEIGHT * BPP) + (i * BPP);
				
				// convert each integer to a byte
				m_data[offset    ] = (byte) (c.getRed());
				m_data[offset + 1] = (byte) (c.getGreen());
				m_data[offset + 2] = (byte) (c.getBlue());
			}
		}
		
		return true;
	}
	
	public boolean load() throws PaletteReadException {
		if(m_file == null || !m_file.exists()) { return false; }

		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			throw new PaletteReadException("File " + m_file.getName() + " has no extension.");
		}

		// verify that the file extension is supported
		if(!(extension.equalsIgnoreCase(FILE_TYPE))) {
			throw new PaletteReadException("File " + m_file.getName() +  " has unsupported extension: " + extension);
		}

		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" is too large to store in memory.");
		}
		
		// read the file into memory
		InputStream in = null;
		byte data[] = new byte[(int) m_file.length()];
		try {
			in = new FileInputStream(m_file);
			in.read(data);
			in.close();
		}
		catch(FileNotFoundException e) {
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			throw new PaletteReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}
		
		// verify that the data is not missing any information, and contains all required colour data
		if(data.length < PALETTE_SIZE_RGB) {
			throw new PaletteReadException("Palette file \"" + m_file.getName() + " is incomplete or corrupted.");
		}
		
		// update the local memory to the data read in from file
		m_data = data;
		
		m_loaded = true;
		
		return true;
	}

	public boolean save(String fileType) throws PaletteWriteException {
		// check that the palette has a file set and that the palette is loaded / initialized
		if(!m_loaded || m_file == null) {
			throw new PaletteWriteException("Palette " + FILE_TYPE + " file must be loaded and initialized with a file to be saved.");
		}
		
		// if a file type is not specified, set to the default file type, then verify that the selected file type is supported for writing
		String type = null;
		if(fileType == null) { type = getDefaultFileType(); }
		else { type = fileType.trim(); }
		if(!hasFileType(type)) {
			throw new PaletteWriteException(FILE_TYPE + " palette file type \"" + type + "\" is not supported for writing.");
		}
		
		// write the data to the specified file
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(m_data);
			out.close();
		}
		catch(IOException e) {
			throw new PaletteWriteException("Error writing to file " + m_file.getName() +  ": " + e.getMessage());
		}
		
		return true;
	}
	
}
