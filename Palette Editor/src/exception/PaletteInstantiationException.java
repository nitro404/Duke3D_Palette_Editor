package exception;

public class PaletteInstantiationException extends Exception {
	
	private static final long serialVersionUID = -2945404446783626477L;
	
	public PaletteInstantiationException() {
		super();
	}
	
	public PaletteInstantiationException(String message) {
		super(message);
	}
	
	public PaletteInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PaletteInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
