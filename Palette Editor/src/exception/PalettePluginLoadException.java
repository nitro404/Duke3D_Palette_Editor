package exception;

public class PalettePluginLoadException extends PluginLoadException {
	
	private static final long serialVersionUID = 9133136834466890721L;
	
	public PalettePluginLoadException() {
		super();
	}
	
	public PalettePluginLoadException(String message) {
		super(message);
	}
	
	public PalettePluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PalettePluginLoadException(Throwable cause) {
		super(cause);
	}
	
}
