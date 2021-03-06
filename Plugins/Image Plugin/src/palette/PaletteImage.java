package palette;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import exception.*;
import utilities.*;

public class PaletteImage extends Palette {
	
	protected BufferedImage m_image;
	protected int m_scale;
	
	public static final String[] FILE_TYPES = { "PNG", "GIF", "BMP" };
	public static final String PALETTE_DESCRIPTION = "Default";
	
	public PaletteImage() {
		this(null);
	}
	
	public PaletteImage(File file) {
		super(file);
		m_image = null;
		m_scale = 1;
	}
	
	public String getType() {
		return "Image";
	}
	
	public int numberOfFileTypes() {
		return FILE_TYPES.length;
	}
	
	public String getFileType(int index) {
		if(index < 0 || index >= FILE_TYPES.length) { return null; }
		return FILE_TYPES[index];
	}
	
	public int indexOfFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<FILE_TYPES.length;i++) {
			if(FILE_TYPES[i].equalsIgnoreCase(type)) {
				return i;
			}
		}
		return -1;
	}
	
	public BufferedImage getImage() {
		return m_image;
	}
	
	public int getScale() {
		return m_scale;
	}

	public String getPaletteDescription(int index) {
		return index == 0 ? PALETTE_DESCRIPTION : null;
	}
	
	public Color getPixel(int index, int x, int y) {
		if(!m_loaded || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return null; }
		
		// get the pixel colour at the specified position
		return new Color(m_image.getRGB(x * m_scale, y * m_scale));
	}
	
	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!m_loaded || m_image == null || c == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return false; }
		
		// update the pixel colour at the specified position
		m_image.setRGB(x, y, c.getRGB());
		
		return true;
	}

	public Color[] getColourData(int index) {
		if(index != 0 || !m_loaded || m_image == null) { return null; }
		
		// iterate over the entire palette and copy each colour into the colour data array
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
		if(!m_loaded || m_scale != 1) {
			if(isInstantiable()) {
				m_scale = 1;
				m_image = new BufferedImage(PALETTE_WIDTH, PALETTE_HEIGHT, BufferedImage.TYPE_INT_RGB);
				m_loaded = true;
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire image and replace each pixel with
		// the corresponding colour in the new colour data array
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				m_image.setRGB(i, j, colourData[dataOffset + (j * PALETTE_WIDTH) + i].getRGB());
			}
		}
		
		return true;
	}
	
	public boolean fillWithColour(Color c, int index) {
		if(index > 0) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!m_loaded || m_scale != 1) {
			if(isInstantiable()) {
				m_scale = 1;
				m_image = new BufferedImage(PALETTE_WIDTH, PALETTE_HEIGHT, BufferedImage.TYPE_INT_RGB);
				m_loaded = true;
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire image and replace each pixel with the replacement colour
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				m_image.setRGB(i, j, c.getRGB());
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
		if(!hasFileType(extension)) {
			throw new PaletteReadException("File " + m_file.getName() +  " has unsupported extension: " + extension);
		}
		
		// read the palette image into memory using ImageIO
		try { m_image = ImageIO.read(m_file); }
		catch(IOException e) { throw new PaletteReadException("ImageIO failed to load image: " + m_file.getName()); }
		if(m_image == null) { throw new PaletteReadException("Null image returned, ImageIO failed to load image: " + m_file.getName()); }
		
		// verify that the image is square
		if(m_image.getWidth() != m_image.getHeight()) {
			throw new PaletteReadException("Palette image \"" + m_file.getName() + "\" must be square.");
		}
		
		// check the minimum size of the width and height of the image
		if(m_image.getWidth() < PALETTE_WIDTH || m_image.getHeight() < PALETTE_HEIGHT) {
			throw new PaletteReadException("Palette image \"" + m_file.getName() + "\" dimensions must be at least " + PALETTE_WIDTH + " x " + PALETTE_HEIGHT + ".");
		}
		
		// check that the image is a multiple of the width and height
		// (ie. to account for images scaled upwards using nearest neighbour) 
		if(m_image.getWidth() % PALETTE_WIDTH != 0 || m_image.getHeight() % PALETTE_WIDTH != 0) {
			throw new PaletteReadException("Palette image \"" + m_file.getName() + "\" dimensions must be a multiple of " + PALETTE_WIDTH + ".");
		}
		
		// calculate the scale used in the image
		m_scale = m_image.getWidth() / PALETTE_WIDTH;
		
		m_loaded = true;
		
		return true;
	}

	public boolean save(String fileType) throws PaletteWriteException {
		// check that the palette has a file set and that the palette is loaded / initialized
		if(!m_loaded || m_file == null) {
			throw new PaletteWriteException("Palette image file must be loaded and initialized with a file to be saved.");
		}
		
		// if a file type is not specified, set to the default file type, then verify that the selected file type is supported for writing
		String type = null;
		if(fileType == null) { type = getDefaultFileType(); }
		else { type = fileType.trim(); }
		if(!hasFileType(type)) {
			throw new PaletteWriteException("Image palette file type \"" + type + "\" is not supported for writing.");
		}
		
		// write the image to the specified file
		try {
			return ImageIO.write(m_image, type.toLowerCase(), m_file);
		}
		catch(IOException e) {
			throw new PaletteWriteException("Failed to save palette image to file: \"" + m_file.getName() + "\".");
		}
	}
	
}
