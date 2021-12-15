package pjrn.signing.model;

/**
 * LoginIncorrectoException.
 * 
 * @author pjrn
 *
 */
public class LoginIncorrectoException extends RuntimeException {

  public LoginIncorrectoException(String string, Exception e) {
    super(string, e);
  }

  private static final long serialVersionUID = 1L;

}
