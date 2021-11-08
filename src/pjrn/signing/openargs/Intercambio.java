package pjrn.signing.openargs;

/** 
 * Para armar el body del request a las APIs recuperar a firmar y guardar los firmados 
 * @author pjrn
 */
public class Intercambio {
    
    public Carpeta Carpeta;
    
    public String toString(){
        if(Carpeta!=null){
            return Carpeta.toString();
        }else{
            return "{}";
        }
    }
    
}

