package nl.ou.testar.temporal.foundation;

public class DBManagerException extends RuntimeException {

    public DBManagerException() {super();}
    public DBManagerException(String message) {super(message);}
    public DBManagerException(String message, Throwable err) {super(message,err);}
}