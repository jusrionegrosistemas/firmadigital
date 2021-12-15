package pjrn.signing.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.util.List;
import java.util.Objects;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import java.security.cert.Certificate;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.SignatureUtil;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import sun.security.pkcs11.SunPKCS11;

/**
 * UsbToken.
 * 
 * @author pjrn
 */
public class UsbToken {

  private SunPKCS11 provider;
  private String mensajeError = "Algo salió mal";
  /**
   * UsbToken.
   * 
   * @param tokenLibraryPath
   */
  public UsbToken(String tokenLibraryPath) {
    Objects.nonNull(tokenLibraryPath);
    if (Files.notExists(Paths.get(tokenLibraryPath))) {
      throw new PjrnException(
          "The library file is not found, check if the path is correct");
    }

    this.provider = this.buildProvider(tokenLibraryPath);
  }

  private SunPKCS11 buildProvider(String tokenLibraryPath) {
    try {
      File tmpConfigFile = File.createTempFile("pkcs11-", "conf");
      tmpConfigFile.deleteOnExit();
      try (PrintWriter configWriter = new PrintWriter(
          new FileOutputStream(tmpConfigFile), true)) {
        configWriter.println("name=anyname");
        configWriter.println("library=" + tokenLibraryPath);
      }

      return new SunPKCS11(tmpConfigFile.getAbsolutePath());

      // si va con java 11 usar las 3 líneas de abajo
      // SunPKCS11 spkcs11 = new SunPKCS11();
      // spkcs11.configure(tmpConfigFile.getAbsolutePath());
      // return spkcs11;
    } catch (Exception e) {
      throw new PjrnException(this.mensajeError, e);
    }
  }

  /**
   * Obtiene el certificado del token.
   * 
   * @param pin
   * @return
   */
  public Certificate publicCertificate(char[] pin) {
    try {
      KeyStore keyStore = retrieveKeyStore(pin);
      return keyStore.getCertificate(keyStore.aliases().nextElement());

    } catch (Exception e) {
      throw new PjrnException(this.mensajeError, e);
    }
  }

  /**
   * Firma los PDFs.
   * 
   * @param srcDest
   *          (source path of the Pdf to be signed and destination path of the
   *          signed Pdf)
   * @param frame
   * @param pin
   * @param x509
   */
  public void signPdfs(List<SrcDest> srcDest, Progreso frame, char[] pin,
      X509Certificate x509) {
    Objects.requireNonNull(srcDest);
    try {

      int total = srcDest.size();
      int cont = 0;

      frame.setStatus("Inicializando Token", 2);

      // Obtengo la clave privada del Token
      KeyStore keyStore = retrieveKeyStore(pin);
      String alias = keyStore.aliases().nextElement();
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);

      frame.setStatus("Firmando: ");

      // apellido y nombre del certificado
      String l2text = x509.getSubjectX500Principal().getName().split("=")[1]
          .split(",")[0];
      // Gets the current date and time, with your default time-zone
      ZonedDateTime dateTime = ZonedDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter
          .ofPattern("dd.MM.yyyy HH:mm:ss");
      l2text += "\nFecha y hora: " + dateTime.format(formatter);

      int cantFirmas = 0;
      float x = 10;
      float y = 10;
      float desplazamiento;
      float ancho = 100;
      float alto = 8;
      // cm = 40; // 1 cm son 40 puntos

      float altoPagina1;

      // Firma cada uno de los PDF
      for (SrcDest path : srcDest) {
        cont++;
        frame.setStatus("Firmando: " + cont + " de " + total);

        PdfReader reader = new PdfReader(path.src());

        StampingProperties sp = new StampingProperties();
        sp.useAppendMode();

        PdfSigner signer = new PdfSigner(reader,
            new FileOutputStream(path.dest()), sp);
        cantFirmas = cantFirmasPdf(signer); // path.src()
        desplazamiento = alto + 20; // 110 por firma
        desplazamiento = cantFirmas * desplazamiento; // calculo nueva altura

        // resto 150 porque si no me quedaba muy arriba y no se veía
        altoPagina1 = signer.getDocument().getFirstPage().getPageSize()
            .getHeight() - 150;

        String newSig = signer.getNewSigFieldName();

        signer.setFieldName(newSig);
        // signer.setCertificationLevel(PdfSigner.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS);
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        // las coordenadas arrancan desde abajo por eso tengo que invertir y con
        // y1
        // Rectangle rect = new
        // esto es si se usa setBox
        Rectangle rect = new Rectangle(x, altoPagina1 - y - desplazamiento,
            ancho, alto);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        appearance.setLayer2FontSize(7f)
            .setLayer2Text("Firmado digitalmente por " + l2text)
            // Specify if the appearance before field is signed will be used
            // as a background for the signed field. The "false" value is the
            // default value.
            .setReuseAppearance(false).setPageRect(rect).setPageNumber(1);

        signer.setFieldName(newSig);

        // Firma el PDF
        IExternalSignature pks = new PrivateKeySignature(privateKey,
            DigestAlgorithms.SHA256, this.provider.getName());
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, keyStore.getCertificateChain(alias),
            null, null, null, 0, PdfSigner.CryptoStandard.CMS);

      }
      resetProvider();
    } catch (Exception e) {
      JPanel panel = new JPanel();
      JLabel label = new JLabel("Hubo un error con la contraseña del token");
      panel.add(label);
      /*
       * String[] options = new String[]{"OK"}; int option =
       * JOptionPane.showOptionDialog(null, panel, "Error",
       */
      e.printStackTrace();

      Throwable rootCause = findRooCause(e);
      if (rootCause instanceof sun.security.pkcs11.wrapper.PKCS11Exception) {
        if ("CKR_PIN_INCORRECT".equals(rootCause.getMessage())) {
          throw new LoginIncorrectoException("PIN incorrecto... ", e);
        }
        if ("CKR_PIN_LOCKED".equals(rootCause.getMessage())) {
          throw new PjrnException(
              "Su Token esta bloqueado por intentos incorrectos", e);
        }
      }
      throw new PjrnException(this.mensajeError, e);
    } finally {
      this.resetProvider();
    }
  }

  /**
   * Retorno la cantidad de firmas que tiene el PDF
   * 
   * @param signer
   * @return
   * @throws IOException
   */
  private int cantFirmasPdf(PdfSigner signer) throws IOException { // String src
    int cont = 0;
    // PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
    PdfDocument pdfDoc = signer.getDocument();
    SignatureUtil signUtil = new SignatureUtil(pdfDoc);
    List<String> names = signUtil.getSignatureNames();

    cont = names.size();
    return cont;
  }

  private void resetProvider() {
    // this removes the provider asigned before signing
    Security.removeProvider(this.provider.getName());
    try {
      // This force de app to ask for PIN every time
      this.provider.logout();
    } catch (LoginException e) {
      throw new PjrnException("Ups, something went wrong logging out... ", e);
    }
  }

  private KeyStore retrieveKeyStore(char[] pin) throws Exception {
    Security.addProvider(this.provider);

    // callback to obtain and set a password...:
    KeyStore.CallbackHandlerProtection callbackHandler = new KeyStore.CallbackHandlerProtection(
        new CallbackHandler() {
          @Override
          public void handle(Callback[] callbacks)
              throws IOException, UnsupportedCallbackException {
            Callback callback = callbacks[0];
            PasswordCallback pc = (PasswordCallback) callback;
            char[] password = pin;
            pc.setPassword(password);
          }
        });

    KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", null,
        callbackHandler);
    return builder.getKeyStore();
  }

  private Throwable findRooCause(Throwable e) {
    Throwable rootCause = e;
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }
}
