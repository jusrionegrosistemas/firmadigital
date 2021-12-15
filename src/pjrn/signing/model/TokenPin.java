package pjrn.signing.model;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Clase TokenPin.
 * 
 * @author pjrn
 * 
 */

public class TokenPin {

  /**
   * retorna el pin.
   * @return
   */
  public char[] pin() {
    JPanel panel = new JPanel();
    JLabel label = new JLabel("Ingrese su PIN: ");
    JPasswordField pass = new JPasswordField(10);
    panel.add(label);
    panel.add(pass);
    String[] options = new String[]{"OK"};
    int option = JOptionPane.showOptionDialog(null, panel, "Firma Digital",
        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

    if (option == 0) {
      // pressing OK button
      return pass.getPassword();
    }
    return "".toCharArray();
  }
}
