package pjrn.signing.model;

/**
 * Clase PjrnException. Excepction PJRN.
 * 
 * @author pjrn
 *
 */
public class PjrnException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PjrnException(Exception e) {
    super(e);
  }

  public PjrnException(String message, Throwable cause) {
    super(message, cause);
  }

  public PjrnException(String message) {
    super(message);
 }

}
