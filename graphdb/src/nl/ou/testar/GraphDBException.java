package nl.ou.testar;

/**
 * Exception class for GraphDB interaction
 * Created by floren on 9-6-2017.
 */
class GraphDBException extends RuntimeException {


    GraphDBException(final String msg) {
        super(msg);
    }
}
