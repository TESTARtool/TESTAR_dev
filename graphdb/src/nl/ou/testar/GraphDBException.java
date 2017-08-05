package nl.ou.testar;

/**
 * Exception class for GraphDB interaction
 * Created by floren on 9-6-2017.
 */
class GraphDBException extends RuntimeException {

	private static final long serialVersionUID = -785144613921175647L;

	GraphDBException(final String msg) {
        super(msg);
    }
}
