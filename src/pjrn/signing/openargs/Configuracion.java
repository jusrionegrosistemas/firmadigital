package pjrn.signing.openargs;

import java.io.InputStream;
import java.util.Properties;

import pjrn.signing.model.PjrnException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Configuracion.
 * 
 * @author pjrn
 *
 */

public class Configuracion {
  static final String DIRECTORIO = "FirmaConfig";
  static final String CONFIG_PROPERTIES = "config.properties";
  // "user.dir"; podría ser user.home
  static final String AMBIENTE_PROP_HOME = "user.home";

  private String libreria;
  private String temp;
  private String url;

  Properties props; // = new Properties();

  /**
   * Configuracion.
   */
  public Configuracion() {
    this.libreria = "";
    this.temp = System.getProperty("java.io.tmpdir");
    this.url = "";
    this.props = new Properties();
  }

  /**
   * Configuracion.
   * 
   * @param libreria
   * @param temp
   * @param url
   */
  public Configuracion(String libreria, String temp, String url) {
    this.libreria = libreria;
    this.temp = temp;
    this.url = url;
    this.props = new Properties();
  }

  public String getLibreria() {
    return this.libreria;
  }

  public void setLibreria(String libreria) {
    this.libreria = libreria;
  }

  public String getTemp() {
    return this.temp;
  }

  public void setTemp(String temp) {
    this.temp = temp;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String toString() {
    return "librería: " + this.libreria + " temp: " + this.temp + " url: "
        + this.url;
  }

  /**
   * leerConfiguracion.
   */
  public void leerConfiguracion() {
    inicializarArchivoPropiedades();

    this.libreria = props.getProperty("libreria");
    this.temp = props.getProperty("temp");
    this.url = props.getProperty("url");
  }

  /**
   * guardarConfiguracion.
   */
  public void guardarConfiguracion() {
    props.setProperty("libreria", this.libreria);
    props.setProperty("temp", this.temp);
    props.setProperty("url", this.url);

    String home = System.getProperty(Configuracion.AMBIENTE_PROP_HOME);
    try (OutputStream out = Files.newOutputStream(Paths.get(home,
        Configuracion.DIRECTORIO, Configuracion.CONFIG_PROPERTIES))) {
      props.save(out, null);

    } catch (IOException e) {
      throw new PjrnException(e);
    }

  }

  private void inicializarArchivoPropiedades() {
    String home = System.getProperty(Configuracion.AMBIENTE_PROP_HOME);
    // System.out.println(home);

    Path path = Paths.get(home, Configuracion.DIRECTORIO,
        Configuracion.CONFIG_PROPERTIES);
    // System.out.format("toString: %s%n", path.toString());
    // System.out.format("getFileName: %s%n", path.getFileName());
    // System.out.format("getName(0): %s%n", path.getName(0));
    // System.out.format("getNameCount: %s%n", path.getNameCount());
    // System.out.format("subPath(0,2): %s%n", path.subpath(0,2));
    // System.out.format("getParent: %s%n", path.getParent());
    // System.out.format("getRoot: %s%n", path.getRoot());

    try {
      // crear los directorios intermedios hasta el archivo si es que no existen
      Files.createDirectories(path.getParent());
    } catch (IOException e) {
      System.out
          .println("Error al crear estructura de directorios intermedios");
      System.out.println(e);
    }

    try { // si no hay archivo crear archivo vacio
      if (!Files.exists(path)) {
        Files.createFile(path);
        // System.out.println("Creando archivo");
        // inicializar el archivo con lo que tenga de configuración por defecto
        guardarConfiguracion();
      }

      try (InputStream in = Files.newInputStream(Paths.get(home,
          Configuracion.DIRECTORIO, Configuracion.CONFIG_PROPERTIES))) {
        props.load(in);

        this.libreria = props.getProperty("libreria", this.libreria);
        this.temp = props.getProperty("temp", this.temp);
        this.url = props.getProperty("url", this.url);

      } catch (IOException e) {
        System.out.println("Error al abrir el archivo de propiedades");
        System.out.println(e);
      }

    } catch (IOException e) {
      System.out.println("Error al crear el archivo de configuración");
      System.out.println(e);
    }

    /*
     * -chequear si está parametros.properties -si está abrirlo, si no crearlo
     * con valores por defecto según el sistema operativo --chequear si está la
     * librería donde se espera según el sistema operativo -permitir elegir
     * parámetros que se guardan en config.properties
     */

    /*
     * también aprovechar a chequear si está presente el token, si no lo está
     * mostrar mensaje
     */

  }

  /**
   * main.
   * 
   * @param args
   */
  public static void main(String[] args) {
    Configuracion conf = new Configuracion("/home/lib.so", "/tmp",
        "http://172.16.21.136:8073");
    System.out.println(conf);

    conf.leerConfiguracion();
    System.out.println("Después de leer la configuración:");
    System.out.println(conf);
    conf.setUrl("172.16.21.136:8073");
    System.out.println("Después de cambiar un parámetro:");
    System.out.println(conf);
    conf.guardarConfiguracion();

  }

}
