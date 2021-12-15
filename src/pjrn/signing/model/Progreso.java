package pjrn.signing.model;

/**
 * Clase Progreso.
 * 
 * @author pjrn
 */

public interface Progreso {
  public void setStatus(String mensaje);
  
  public void setStatus(String mensaje, int segundos);

}
