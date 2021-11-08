package pjrn.signing.openargs;

/**
 * Estructura de los campos del response cuando recibo los archivos
 * @author pjrn
 */
public class DataFile{
    public String id;
    public String arch;
    
    public String toString(){
        String devolver;
        if (id!=null && arch!=null){
            devolver = "{ id:" + id + ", archivo:"+ arch +"}";
        }else{
            devolver ="{}";
        }
        return devolver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArch() {
        return arch;
    }

    public void setFile(String arch) {
        this.arch = arch;
    }
    
}