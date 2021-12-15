package pjrn.signing.openargs;

/**
 * Carpeta.
 * 
 * @author pjrn
 */
public class Carpeta {
  public String id; // id de la carpeta
  public DataFile[] pdfs;

  /**
   * toString.
   */
  public String toString() {
    String devolver = "";
    devolver = "id:" + id;
    String listaPdfs = "";
    if (pdfs != null) {
      for (int i = 0; i < this.pdfs.length; i++) {
        listaPdfs += pdfs[i].toString();
        if (i + 1 < pdfs.length) {
          listaPdfs += ",";
        }
      }
      if (pdfs.length > 0) {
        devolver += ", pdfs: [" + listaPdfs + "]";
      }
    }
    return devolver;
  }
}
