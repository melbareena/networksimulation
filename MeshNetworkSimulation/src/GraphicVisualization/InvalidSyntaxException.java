package GraphicVisualization;

/**
 * @author Benjamin
 *
 */
public class InvalidSyntaxException extends Exception {
	
	private static final long serialVersionUID = 8060588533981596551L;

	public InvalidSyntaxException(String filename, String expectedSyntax) {
		super("Syntax error in file: " + filename + ". Expecting: " + expectedSyntax);
	}

}
