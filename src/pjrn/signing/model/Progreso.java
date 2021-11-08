package pjrn.signing.model;

/**
 * @author pjrn
 */

public interface Progreso {
    public void setStatus(String mensaje);
    public void setStatus(String mensaje, int segundos);
    
}
