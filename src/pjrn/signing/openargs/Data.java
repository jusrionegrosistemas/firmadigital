package pjrn.signing.openargs;

/**
 * Estructura del response de la API que me recupera los archivos en Base64
 * @author pjrn
 */
public class Data{

    public DataFile[] data;
    
    public String toString(){
        String devolver = "";
        String listaPdfs = "";
        if(data!=null){
            for(int i=0;i<this.data.length;i++){
                listaPdfs += data[i].toString();
                if (i+1 < data.length){
                    listaPdfs += ",";
                }
            }
            if(data.length>0){
                devolver += "pdfs: [" + listaPdfs + "]";
            }
        }
        return devolver;
    }
    
    public void setData(DataFile[] data){
        this.data = data;
    }
    
    public DataFile[] getData(){
        return this.data;
    }
    
}

