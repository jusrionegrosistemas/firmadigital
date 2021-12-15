package pjrn.signing.openargs;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;

/**
 * Clase principal, el firmador se inicia con esta clase.
 * 
 * @author pjrn
 */
public class OpenArgs {

  /**
   * main.
   * 
   * @param args
   *          String[]
   */
  public static void main(String[] args) {

    Logger logger = Logger.getLogger(OpenArgs.class.getName()); // Logger.getGlobal();

    FileHandler firmaFileHandler;
    try {
      firmaFileHandler = new FileHandler("firmaPJRN.log", 1024 * 1024 * 1024, 3,
          true); // rotación cada 3Mb x 3 archivos
      firmaFileHandler.setLevel(Level.FINE);
      logger.addHandler(firmaFileHandler);
    } catch (SecurityException se) {
      System.out.println(se.getMessage());
    } catch (IOException ioe) {
      System.out.println(ioe.getMessage());
    }

    OpenArgsUI gui;

    String cadena = "";

    String tokenApi = "abckdeFGhs12kdhdti";
    String uuid = "";

    String rutaApi = "";

    String archivo = null;

    // Leo el archivo .sign y obtengo el id de carpeta
    if (args.length > 0 || archivo != null) {

      File file;
      /*
       * if (archivo != null) { file = new File(archivo); } else {
       */
      file = new File(args[0]);
      // }

      try {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          cadena += scanner.nextLine();
        }
      } catch (IOException ioe) {
        logger.log(Level.SEVERE, "Error al leer el archivo " + file, ioe);
      }

      uuid = cadena;

      try {
        gui = new OpenArgsUI(uuid, tokenApi, logger, rutaApi);
        gui.pack();
        gui.setVisible(true);
      } catch (IOException ioe) {
        logger.log(Level.SEVERE, "Error al utilizar GUI", ioe);
      }

    } else { // no tengo parámetros de entrada, voy por un archivo cualquiera
      JPanel panel = new JPanel();
      JLabel label = new JLabel("Faltan parámetros para ejecutar el programa");
      panel.add(label);
      /*
       * String[] options = new String[]{"OK"}; int option =
       * JOptionPane.showOptionDialog(null, panel, "Firma Digital",
       * JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
       */

      logger.log(Level.SEVERE, "Faltan parámetros");
    }
  }

}
