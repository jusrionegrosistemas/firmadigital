package pjrn.signing.openargs;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.google.gson.Gson;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import pjrn.signing.model.SrcDest;
import pjrn.signing.model.UsbToken;
import pjrn.signing.model.Progreso;

/**
 * * @author pjrn
 */
public class OpenArgsUI extends JFrame implements Progreso{
    
    private static int DELAY_SEGUNDOS = 0;
    
    private String tokenApi = "xxxxxxxxxx";
    private String rutaApi = "";
    private Gson gson;
    
    private JLabel etiqueta = new JLabel("               Firmar archivos                 ");
    private JLabel statusLabel = new JLabel("Estado:                                          ");
    
    private JLabel uuidLabel = new JLabel("UUID");
    private JLabel pwdLabel = new JLabel("Password");
    
    private JTextField uuidField = new JTextField(25);
    private JPasswordField pwdField = new JPasswordField(20);
    
    private JButton firmarBtn = new JButton("Validar y Firmar");
    private JButton salirBtn = new JButton("Salir");
    
    protected Logger logger;

    public OpenArgsUI() throws IOException{
        super("Firmador");


        //Si inicio OpenArgsUI sin argumentos formateo para que escriba igual en un log
        this.logger = Logger.getLogger(OpenArgs.class.getName());//Logger.getGlobal();
        FileHandler firmaFileHandler;
        try{
            firmaFileHandler = new FileHandler("firmaPJRN.log", 1024*1024*1024, 3,  true); //rotación cada 3Mb x 3 archivos
            firmaFileHandler.setLevel(Level.FINE);
            logger.addHandler(firmaFileHandler);
        }catch (SecurityException se){
            System.out.println(se.getMessage());
        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

        logger.setLevel(Level.FINE);
        
        
        this.initFrame();
    }
    public OpenArgsUI(String uuid, String tokenApi, Logger logger, String rutaApi) throws IOException{
        super("Firmador");
        
        this.logger = logger;
        
        this.uuidField.setText(uuid);
        this.tokenApi = tokenApi;
        this.rutaApi = rutaApi;
        this.gson = new Gson();
        
        this.uuidLabel.setVisible(false);
        this.uuidField.setVisible(false);
        
        this.initFrame();
    }

    private void initFrame() throws IOException{
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);


        int cien = 100;
        
        if(uuidField.getText().length()>0){
           uuidField.setEditable(false);
        }

        add(etiqueta, new GBC(0,0,2,1).setAnchor(GBC.NORTH).setFill(GBC.NONE).setInsets(10).setWeight(cien, cien));
        add(uuidLabel, new GBC(0,1).setAnchor(GBC.EAST).setFill(GBC.NONE).setInsets(5).setWeight(cien, cien));
        add(uuidField, new GBC(1,1).setAnchor(GBC.WEST).setFill(GBC.NONE).setInsets(5).setWeight(cien,cien));
        add(pwdLabel, new GBC(0,2).setAnchor(GBC.EAST).setFill(GBC.NONE).setInsets(5).setWeight(cien, cien));
        add(pwdField, new GBC(1,2).setAnchor(GBC.WEST).setFill(GBC.NONE).setInsets(5).setWeight(cien, cien));

        JPanel panelFirmaSalir = new JPanel(); //la línea de botones va en un panel propio
        panelFirmaSalir.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFirmaSalir.add(firmarBtn);
        panelFirmaSalir.add(salirBtn);
        add(panelFirmaSalir, new GBC(0,3,2,1).setAnchor(GBC.NORTH).setInsets(5).setWeight(cien, cien));

        add(statusLabel, new GBC(0,4,2,1).setAnchor(GBC.NORTH).setFill(GBC.NONE).setInsets(20).setWeight(cien, cien));


        pack();

        /*centrar la ventana*/
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int x = (d.width / 2) - this.getWidth()/2;
        int y = (d.height / 2) - this.getHeight()/2;
        this.setLocation(x,y);
        
        firmarBtn.addActionListener(this::firmar);
        salirBtn.addActionListener(this::salir);
    }
    

    private void initFrameGridLayout() throws IOException{
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = this.getContentPane();
        
        contentPane.setLayout(new GridLayout(3,3,5,5));
        
        //Add components
        contentPane.add(uuidLabel);
        contentPane.add(uuidField);
        
        contentPane.add(pwdLabel);
        contentPane.add(pwdField);
        
        contentPane.add(firmarBtn);
        contentPane.add(salirBtn);
        
        //Add action listener to buttons
        firmarBtn.addActionListener(this::firmar);
        salirBtn.addActionListener(this::salir);
        
    }
    
    
    private void initFrameOriginal() throws IOException{
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        
        //Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(uuidLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPane.add(uuidField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(pwdLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPane.add(pwdField, gbc);
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(firmarBtn);
        btnPanel.add(salirBtn);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        contentPane.add(btnPanel, gbc);
        
        //Add action listener to buttons
        firmarBtn.addActionListener(this::firmar);
        salirBtn.addActionListener(this::salir);
        
    }

    
    /**
     * Maneja la accion "Validar y Firmar" 
     * @param e
     */
    private void firmar(ActionEvent e){
        String cadenaJson;
        
        Configuracion conf = new Configuracion();
        conf.leerConfiguracion();

        String url = conf.getUrl();
        
        String libreria = conf.getLibreria();
        String temp = conf.getTemp();
        
        String urlRecuperarFirmar 		= "http://" + url + "recuperar-a-firmar";
        String urlGuardarFirmados 		= "http://" + url + "guardar-firmados";
        String urlConfirmarFinalizacion = "http://" + url + "finalice-firma";
        
        String nombresArchivos[];
        String nombresArchivosFirmados[];
        String nombreArchivo;
        String nombreArchivoFirmado;
        
        List<SrcDest> listaSrcDest;
        listaSrcDest = new ArrayList<SrcDest>();
        
        String uuid = uuidField.getText();
        char[] pwd = pwdField.getPassword();
        
        String mensajeOriginal = statusLabel.getText();
        
        Carpeta carpeta = new Carpeta();
        carpeta.id = uuid;
        Intercambio intercambio = new Intercambio();
        intercambio.Carpeta=carpeta;
        cadenaJson = gson.toJson(intercambio);
        
        HttpResponse<JsonNode> response=null;
        String responseString="";
        
        //Recupero los archivos PDF a firmar
        setStatus("Recuperando Archivos", DELAY_SEGUNDOS);
        try{
            response = Unirest.post(urlRecuperarFirmar) 
                  .header("accept", "application/json")
                  .header("authorization","Bearer " + tokenApi)
                  .body(cadenaJson)
                  .asJson()
                  .ifFailure(response2 -> {
                        JOptionPane.showMessageDialog(this, "Hubo un error al solicitar los archivos,  " + response2.getStatusText());
                        System.out.println("Hubo un error al solicitar los archivos,  " + response2.getStatusText());
                        setStatus(mensajeOriginal);
                        return;
                  });
            responseString = response.getBody().toString();
            
        }catch (Exception | Error ex){
            JOptionPane.showMessageDialog(this, "Error al conectar con " + urlRecuperarFirmar);
            System.out.println("Error al conectar con " + urlRecuperarFirmar);
            ex.printStackTrace();
            setStatus(mensajeOriginal);
            logger.log(Level.SEVERE, "Error al recuperar archivos ", ex);
            return;
        }

        //Guardo los PDFs a firmar en la carpeta temporal del usuario
        Data data;
        data = gson.fromJson(responseString, Data.class);
        
        setStatus("Guardando en espacio temporal", DELAY_SEGUNDOS);

        byte decodedFile[];
        int cont =0;

        nombresArchivos = new String[data.data.length];
        nombresArchivosFirmados = new String[data.data.length];

        Path tmpUuid = Paths.get(temp, uuid); 
        try{  //crear los directorios intermedios hasta el archivo si es que no existen
            Files.createDirectories(tmpUuid); 
        }catch(IOException | Error ex){
            JOptionPane.showMessageDialog(this, "Error al crear estructura de directorios intermedios temporales");
            System.out.println("Error al crear estructura de directorios intermedios temporales :  " + temp + " " + uuid);
            ex.printStackTrace();
            setStatus(mensajeOriginal);
            logger.log(Level.SEVERE, "Error al crear estructura de directorios intermedios ", ex);
            return;
        }

        //para cada archivo descargado
        for (cont=0;cont<data.data.length;cont++){

            decodedFile = Base64.getDecoder()
                    .decode(data.data[cont].arch);

            nombreArchivo = data.data[cont].id + ".pdf";
            nombreArchivoFirmado = data.data[cont].id + "_signed" + ".pdf";

            nombresArchivos[cont] = nombreArchivo;
            nombresArchivosFirmados[cont] = nombreArchivoFirmado;

            Path destinationFile = Paths.get(temp, uuid, nombreArchivo); //"/tmp/1.pdf"
            Path destinationFileSigned = Paths.get(temp, uuid, nombreArchivoFirmado); //"/tmp/1.pdf"

            listaSrcDest.add(new SrcDest(destinationFile.toString(), destinationFileSigned.toString()));
            
            try{
                Files.write(destinationFile, decodedFile);
            }catch(Exception | Error ex){
                JOptionPane.showMessageDialog(this, "Error al escribir en destino temporal");
                System.out.println("Error al escribir en destino temporal:" + destinationFile.toString());
                ex.printStackTrace();
                logger.log(Level.SEVERE, "Error al escribir en destino temporal " + destinationFile.toString(), ex);
            }

        }// fin para para cada archivo recibido

        setStatus("Reconociendo TOKEN", DELAY_SEGUNDOS);
        
        //Creo una instancia de SunPKCS11 Provider 
        //usando la libreria dinamica espcecifica povista por el proveedor del token
        UsbToken tokenUnix;
        try{
            tokenUnix = new UsbToken(libreria); // "/usr/lib/x64-athena/libASEP11.so" para linux
        }catch (RuntimeException ex){
            JOptionPane.showMessageDialog(this, "Error reconociendo el TOKEN\n" + ex);
            ex.printStackTrace();
            setStatus(mensajeOriginal, DELAY_SEGUNDOS);
            logger.log(Level.SEVERE, "Error reconociendo el TOKEN", ex);
            return;
        }

        setStatus(mensajeOriginal, DELAY_SEGUNDOS);
        
        char pin[] = this.pwdField.getPassword(); //Obtengo el PIN de manera segura

        if(this.pwdField.getPassword().length > 0){

            X509Certificate x509;
            try{
            	//Obtengo el certificado del Token
                Certificate c;
                c = tokenUnix.publicCertificate(pin); 
                x509 = (X509Certificate) c;

                etiqueta.setText("");
                etiqueta.paintImmediately(etiqueta.getVisibleRect());
                etiqueta.setText(x509.getSubjectX500Principal().getName().split("=")[1].split(",")[0]);
                //System.out.println("getName: " + x509.getSubjectX500Principal().getName().split("=")[1].split(",")[0]);
                etiqueta.paintImmediately(etiqueta.getVisibleRect());
            }catch(Exception ex1){
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta.");
                return;
            }
            
            //Firmo todos los PDFs
            try{
                tokenUnix.signPdfs(listaSrcDest, this, pin, x509);
            }catch(Exception ex){
                this.setStatus("Hubo un error, revise la contraseña",10);
                logger.log(Level.SEVERE, "problemas durante la firma de pdf", ex);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Ingrese una contraseña");
            return;
        }
        

        //leo el archivo firmado y lo cargo reemplazando data.data[0]
        //al terminar de firmar cargo todos los archivos firmados para subir al servidor de archivos
        byte readedFile[]={};
        for (cont =0 ; cont<data.data.length; cont++){
            try{
                readedFile =  Files.readAllBytes(Paths.get(temp, uuid, nombresArchivosFirmados[cont])); //"/tmp/1_signed.pdf"
                String encodedFile = Base64.getEncoder().encodeToString(readedFile);
                data.data[cont].arch=encodedFile; //data.data[0]=encodedFile;
                nombreArchivoFirmado = nombresArchivos[cont]; //quitar el .pdf  // devuelvo como nombre de archivo el original
                nombreArchivoFirmado = nombreArchivoFirmado.substring(0,nombreArchivoFirmado.length()-4);
                data.data[cont].id = nombreArchivoFirmado; //vuelvo a setear el nombre
            }catch(Exception | Error ex){
                System.out.println("Error al leer archivo firmado: " + nombresArchivosFirmados[cont]);
                ex.printStackTrace();
                
                data.data[cont] = null;
                logger.log(Level.SEVERE, "Error al leer archivo firmado " + nombresArchivosFirmados[cont], ex);
            }
        }

        Carpeta carpeta1 = new Carpeta();
        carpeta1.id = uuid;
        carpeta1.pdfs = data.data;
        Intercambio intercambio1 = new Intercambio();
        intercambio1.Carpeta = carpeta1;

        String cadenaJson1 = gson.toJson(intercambio1);

        //Envia todos los PDFs firmados
        setStatus("Enviando archivos firmados", DELAY_SEGUNDOS);
        try{
            HttpResponse<String> response1 = Unirest.post(urlGuardarFirmados) 
                  .header("accept", "application/json")
                  .header("authorization","Bearer " + tokenApi)
                  .body(cadenaJson1)
                  .asString()
                  .ifFailure(response2 -> {
                        JOptionPane.showMessageDialog(this, "Hubo un error al guardar los archivos firmados,  " + response2.getStatusText());
                        System.out.println("Hubo un error al guardar los archivos firmados,  " + response2.getStatusText());
                        setStatus(mensajeOriginal);
                        return;
                  });                    
        }catch (Exception | Error ex){
            JOptionPane.showMessageDialog(this, "Error al conectar con " + urlGuardarFirmados);
            System.out.println("Error al conectar con " + urlGuardarFirmados);
            ex.printStackTrace();
            setStatus(mensajeOriginal);
            logger.log(Level.SEVERE, "Error al conectar con  " + urlGuardarFirmados, ex);
            
            return;
        }

        //indicar que terminé de firmar y subir
        try{
        response = Unirest.post(urlConfirmarFinalizacion) 
              .header("accept", "application/json")
              .header("authorization","Bearer " + tokenApi)
              .body(cadenaJson)
              .asJson()
              .ifFailure(response2 -> {
                      JOptionPane.showMessageDialog(this, "Hubo un error al confirmar la finalización,  " + response2.getStatusText());
                      System.out.println("Hubo un error al confirmar la finalización,  " + response2.getStatusText());
                      setStatus(mensajeOriginal);
                      return;
              });                
        }catch (Exception | Error ex){
            JOptionPane.showMessageDialog(this, "Error al conectar con " + urlConfirmarFinalizacion);
            System.out.println("Error al conectar con " + urlConfirmarFinalizacion);
            ex.printStackTrace();
            setStatus(mensajeOriginal);
            logger.log(Level.SEVERE, "Error al conectar con  " + urlGuardarFirmados, ex);
            
            return;
        }

        this.pwdField.setText("");
        setStatus("Fin del proceso de firma", 5);
        salir(null);
    }
    
    
    //Implementación de la interfaz Progreso para indicar mensajes durante las acciones
    public void setStatus(String mensaje){
        statusLabel.setText("");
        statusLabel.paintImmediately(statusLabel.getVisibleRect());
        statusLabel.setText(mensaje);
        statusLabel.paintImmediately(statusLabel.getVisibleRect());
    }

    public void setStatus(String mensaje, int segundos){
        setStatus(mensaje);
        try {
            TimeUnit.SECONDS.sleep(segundos);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        
    }
    
    private void salir(ActionEvent e){
        System.exit(0);
    }

}
