import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import static java.awt.Color.WHITE;

public class GUI {

    static JMenuBar menuBar;
    static JMenu menu;
    static JMenuItem m1,m2,m3,m4;
    public static boolean run = true;
    public static boolean runStop = true;
    public static int state = 0;
    public static DefaultTableModel modelo1;
    public static JTable tabla1;
    public static Object[] headers1;
    public static int tiempoAuto = Main.timeCant;
    public static Object[][]datosLast;
    private static Dimension screenSize = new Dimension();
    public static int screenWidth;
    public static int screenHeight;
    public static String clave = "241070";
    public static String clave2 = "habilitado";
    public static JFrame fr;
    public static Persistencia enca = Main.encabezados;
    public static Object[][] data1 = Main.datosFirst;
    public static String fecha = Main.fecha();
    public static boolean plcNot;


    public static void MainFrame(){
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Headers de tabla
         headers1 = new Object[]{"Hora",enca.enca[0],enca.enca[1],enca.enca[2],enca.enca[3],enca.enca[4]};

         //Frame principal
        fr = new JFrame(Main.appName);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.getContentPane().setBackground(Color.DARK_GRAY);
        setInitialSize(fr, 50, 50);
        centerWindow(fr);
        fr.pack();
        screenWidth = fr.getWidth();
        screenHeight = fr.getHeight();

        //Menu de acciones
        menuBar = new JMenuBar();
        menu = new JMenu("Funciones");
        menuBar.add(menu);
        m1 = new JMenuItem("Setup");
        m2 = new JMenuItem("Exportar a Excel");
        m3 = new JMenuItem("Borrar datos");
        m4 = new JMenuItem("salir");
        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        menu.add(m4);
        int btnX = perLar(screenWidth,35);
        int btnY = perLar(screenWidth,2);

        //Imagen de ATII
        BufferedImage atiiLogo =  null;
        try {
            atiiLogo= ImageIO.read(new File("C:/AtiiSoftware/ATII.JPG"));
        } catch (IOException e) {
            System.out.println("Problema con la imagen");
        }
        JLabel imagenAtii = new JLabel(new ImageIcon(atiiLogo));
        imagenAtii.setBounds(btnX+465,btnY,150,60);

        //Boton de carga manual
        JButton cargaDatos = new JButton("Carga Manual");
        cargaDatos.setBounds(btnX+310,btnY, 150,60);
        cargaDatos.setFont(new Font("Arial",Font.BOLD,15));
        cargaDatos.setEnabled(false);
        //Boton Automatico-Manual
        JToggleButton onoffLine = new JToggleButton("Auto RUN");
        onoffLine.setBounds(btnX+155,btnY,150,60);
        onoffLine.setBackground(Color.green);
        onoffLine.setFont(new Font("Arial",Font.BOLD,20));
        onoffLine.setFocusPainted(false);
        //Cambio de tiempo
        String[] values= {"1","2","5","10","15","20","30"};
        SpinnerModel modelSinner = new SpinnerListModel(values);
        JSpinner spinner = new JSpinner(modelSinner);
        spinner.setValue(String.valueOf(Main.timeCant));
        spinner.setBounds(btnX,btnY,150,60);
        spinner.setFont(new Font("Arial",Font.BOLD,30));
        spinner.setEnabled(false);
        //tabla de datos
        modelo1 = new DefaultTableModel(data1,headers1);
        tabla1 = new JTable(modelo1){
            public Component prepareRenderer(TableCellRenderer render, int row, int column){
                Component c = super.prepareRenderer(render,row,column);
                Color color1 = new Color(220,220,220);
                Color color2 = WHITE;
                if(!c.getBackground().equals(getSelectionBackground())){
                    Color colorr = (row % 2 == 0 ? color1 : color2);
                    c.setBackground(colorr);
                    colorr = null;
                }return c;
            }
        };
        TableColumnModel colModel1 = tabla1.getColumnModel();
        colModel1.getColumn(0).setPreferredWidth(115);
        JScrollPane scroll1 = new JScrollPane(tabla1);
        scroll1.setBorder(new BevelBorder(BevelBorder.RAISED));
        tabla1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scroll1.setBounds(perLar(screenWidth,3),perAlt(screenHeight,16),perLar(screenWidth,92),perAlt(screenHeight,65));

        //Listener del boton
        cargaDatos.addActionListener(e -> leeCargaDatos());
        onoffLine.addItemListener(e -> {
            int estado = e.getStateChange();
            if (estado != ItemEvent.SELECTED) {
                runStop = true;
                onoffLine.setText("Auto RUN");
                onoffLine.setBackground(Color.green);
                onoffLine.setForeground(Color.black);
                spinner.setEnabled(false);
                cargaDatos.setEnabled(false);
            }else{
                runStop = false;
                onoffLine.setText("Auto STOP");
                onoffLine.setBackground(Color.RED);
                onoffLine.setForeground(WHITE);
                spinner.setEnabled(true);
                cargaDatos.setEnabled(true);
            }
        });
        m1.addActionListener(event -> password(clave));
        m2.addActionListener(event -> {
            try {
                DataBaseHandler baseDatos = new DataBaseHandler(Main.path + "/TV_Data.db","datos",true);
                datosLast = baseDatos.listaDatos(baseDatos.url, baseDatos.largo);
                DefaultTableModel modeloExce = new DefaultTableModel(datosLast,headers1);
                JTable tablaexp = new JTable(modeloExce);
                convierteExcel(Main.path,"Excel_Export",tablaexp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m3.addActionListener(event -> password2(clave2));
        m4.addActionListener(event -> System.exit(0));
        //cambio de tiempo
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tiempoAuto = Integer.parseInt(String.valueOf(spinner.getValue()));
                DataBaseHandler.guardaTiempo(tiempoAuto);
            }
        });
        //adhesiones al MainFrame
        fr.setJMenuBar(menuBar);
        fr.add(cargaDatos);
        fr.add(spinner);
        fr.add(onoffLine);
        fr.add(imagenAtii);
        fr.add(scroll1);
        fr.setLayout(null);
        fr.setResizable(false);
        fr.setVisible(true);

        try {
            autoRWDatos(run);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void leeCargaDatos(){

        Object[] datosPreLista = PLC.obtenerDatos(Main.ipAdress,Main.slotNum,Main.listaVars);
        if (plcNot == true)
        {String hora = Main.fecha+"--"+Main.hora();
        DataBaseHandler.cargaSql(Main.path,hora,datosPreLista);
        modelo1.insertRow(tabla1.getRowCount(),DataBaseHandler.obtieneUltimo(Main.path,0));
        tabla1.changeSelection(tabla1.getRowCount(),0,false,false);
        int curRow1 = modelo1.getRowCount();
        tabla1.setRowSelectionInterval(curRow1-1,curRow1-1);
        tabla1.changeSelection(curRow1-1,0,false,false);}
        else{JOptionPane.showMessageDialog(fr,"No se puede conectar al PLC o las variables son incorrectas");}
    }
    public static void autoRWDatos(boolean active) throws InterruptedException {
        while (active){
            switch(state){
                case 0:
                    leeCargaDatos();
                    state = 1;
                    break;
                case 1:
                    TimeUnit.MINUTES.sleep(tiempoAuto);
                    state = 2;
                    break;
                case 2:
                    TimeUnit.SECONDS.sleep(1);
                    if(runStop){state = 0;}
                    else{state = 3;}
                    break;
                case 3:
                    TimeUnit.SECONDS.sleep(1);
                    state =2;
                    break;
            }
        }
    }
    public static void convierteExcel(String path,String nombre,JTable tabla) throws IOException {

        ExcelTools exp = new ExcelTools();
        String directory = path +"/Excel/";
        exp.exportTable(tabla, new File(directory+nombre+".csv"));
    }
    public static void tablaExcel(String path, String fecha, JTable tabla,JLabel label) throws IOException, InterruptedException {
        convierteExcel(path,fecha,tabla);
        label.setText("Archivo convertido Exitosamente");
    }
    public static JTable creaTablaExport(Object[][] data, Object[] headers){
        DefaultTableModel modelo = new DefaultTableModel(data,headers);
        JTable tabla = new JTable(modelo);

        return tabla;
    }
    public static void setupFrame(){
        JFrame d = new JFrame("Parametros");
        d.getContentPane().setBackground(Color.LIGHT_GRAY);
        int larField = perLar(screenWidth,9);
        int larEtiq = perLar(screenWidth,5);
        int margenX1 = perLar(screenWidth,1);
        int margenX2 = perLar(screenWidth,5);
        int margenX3 = perLar(screenWidth,15);
        int margenX4 = perLar(screenWidth,18);
        int margenX5 = perLar(screenWidth,29);
        int margenX6= perLar(screenWidth,34);
        //perLar(screenWidth,5),perAlt(screenHeight,10)
        JButton guarda = new JButton("Guardar");
        guarda.setBounds(10,630,150,50);
        JButton stp2 = new JButton("Encabezados");
        stp2.setBounds(180,630,150,50);
        //labels
        JLabel param1 = new JLabel("Dirección IP");
        param1.setBounds(margenX1,20,larEtiq,20);
        d.add(param1);
        JLabel param2 = new JLabel("Slot");
        param2.setBounds(margenX1,50,larEtiq,20);
        d.add(param2);
        JLabel param3 = new JLabel("tag_1");
        param3.setBounds(margenX1,80,larEtiq,20);
        d.add(param3);
        JLabel param4 = new JLabel("tag_2");
        param4.setBounds(margenX1,110,larEtiq,20);
        d.add(param4);
        JLabel param5 = new JLabel("tag_3");
        param5.setBounds(margenX1,140,larEtiq,20);
        d.add(param5);
        JLabel param6 = new JLabel("tag_4");
        param6.setBounds(margenX1,170,larEtiq,20);
        d.add(param6);
        JLabel param7 = new JLabel("tag_5");
        param7.setBounds(margenX1,200,larEtiq,20);
        d.add(param7);

        //Entry texts
        JTextField areaAnt1 = new JTextField();
        areaAnt1.setBounds(margenX2,20,larField,20);
        d.add(areaAnt1);
        JTextField areaAnt2 = new JTextField();
        areaAnt2.setBounds(margenX2,50,larField,20);
        d.add(areaAnt2);
        JTextField areaAnt3 = new JTextField();
        areaAnt3.setBounds(margenX2,80,larField,20);
        d.add(areaAnt3);
        JTextField areaAnt4 = new JTextField();
        areaAnt4.setBounds(margenX2,110,larField,20);
        d.add(areaAnt4);
        JTextField areaAnt5 = new JTextField();
        areaAnt5.setBounds(margenX2,140,larField,20);
        d.add(areaAnt5);
        JTextField areaAnt6 = new JTextField();
        areaAnt6.setBounds(margenX2,170,larField,20);
        d.add(areaAnt6);
        JTextField areaAnt7 = new JTextField();
        areaAnt7.setBounds(margenX2,200,larField,20);
        d.add(areaAnt7);



        guarda.addActionListener(event -> {
            aplicarCambios(areaAnt1.getText(),areaAnt2.getText(),areaAnt3.getText(),areaAnt4.getText(),areaAnt5.getText(),areaAnt6.getText(),
                    areaAnt7.getText(),tiempoAuto);
            d.dispose();
        });
        stp2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    encaFrame();
                    d.dispose();
        }});
        Persistencia persisSetup = new Persistencia(0);
        areaAnt1.setText(String.valueOf(persisSetup.atii[0]));
        areaAnt2.setText(String.valueOf(persisSetup.atii[1]));
        areaAnt3.setText(String.valueOf(persisSetup.atii[3]));
        areaAnt4.setText(String.valueOf(persisSetup.atii[4]));
        areaAnt5.setText(String.valueOf(persisSetup.atii[5]));
        areaAnt6.setText(String.valueOf(persisSetup.atii[6]));
        areaAnt7.setText(String.valueOf(persisSetup.atii[7]));


        d.add(guarda);
        d.add(stp2);
        d.setLayout(null);
        d.setSize(perLar(screenWidth,50),740);
        d.setResizable(false);
        d.setVisible(true);
    }
    public static void aplicarCambios(String dt1,String dt2,String dt3,String dt4,String dt5,String dt6,String dt7,int tiempo){

        String url = "jdbc:sqlite:" + Main.path + "/appDataMem.db";
        //WHERE tiempo = 3
        String queryaux ="UPDATE persist SET tag_40 = '"+dt4+"' ";
        String query = "UPDATE persist SET ip ='"+dt1+"',slot ='"+dt2+"',tag_1='"+dt3+"',tag_2='"+dt4+"',tag_3='"+dt5+
                "',tag_4='"+dt6+"',tag_5='"+dt7+"',tiempo='"+tiempo+"'";

        Connection conn;
        try {
            conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement(query);


            stmt.executeUpdate();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("No se puede cargar los datos en "+url);
        }
    }

    public static void aplicarEnca(String dt1,String dt2,String dt3,String dt4,String dt5,String dt6,String dt7){

        String url = "jdbc:sqlite:" + Main.path + "/appDataMem.db";
        //WHERE tiempo = 3
        String query = "UPDATE enca SET Field1 ='"+dt1+"',Field2 ='"+dt2+"',Field3='"+dt3+"',Field4='"+dt4+"',Field5='"+dt5+
                "',Field6='"+dt6+"',Field7='"+dt7+"'";

        Connection conn;
        try {
            conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement(query);


            stmt.executeUpdate();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("No se puede cargar los datos en "+url);
        }
    }
    public static void password(String password){
        JFrame p = new JFrame("Contraseña");
        p.getContentPane().setBackground(Color.LIGHT_GRAY);
        JLabel etiqueta = new JLabel("Contraseña : ");
        etiqueta.setBounds(10,20,100,20);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(120,20,100,20 );
        JButton confir = new JButton("Confirmar");
        confir.setBounds(230,20,100,20);
        JLabel mensaje = new JLabel();
        mensaje.setBounds(10,40,300,20);
        mensaje.setForeground(Color.RED);

        confir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String compara = String.valueOf(passwordField.getText());
                String compara2 = password;
                if(compara.equals(compara2)){
                    setupFrame();
                    p.dispose();
                }else {
                    mensaje.setText("Contraseña incorrecta... Vuelva a intentar");
                }}
        });


        p.add(etiqueta);
        p.add(passwordField);
        p.add(confir);
        p.add(mensaje);
        p.setLayout(null);
        p.setSize(380,100);
        p.setResizable(false);
        p.setVisible(true);
    }
    public static void password2(String password){
        JFrame p = new JFrame("Borrar Base de datos");
        p.getContentPane().setBackground(Color.LIGHT_GRAY);
        JLabel refer = new JLabel("Esta por borrar los datos de la base.");
        refer.setVerticalAlignment(0);
        refer.setBounds(10,10,300,20);
        JLabel refer2 = new JLabel("Ingrese el nombre con el que desear guardar el backup.");
        refer2.setBounds(10,30,350,20);
        JLabel nombre = new JLabel("Nombre: ");
        nombre.setBounds(10,50,80,20);
        JLabel etiqueta = new JLabel("Contraseña : ");
        JTextField nombreBack = new JTextField();
        nombreBack.setBounds(120,50,100,20);
        etiqueta.setBounds(10,70,100,20);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(120,70,100,20 );
        JButton confir = new JButton("Confirmar");
        confir.setBounds(230,120,100,20);
        JLabel mensaje = new JLabel();
        mensaje.setBounds(10,100,300,20);
        mensaje.setForeground(Color.RED);

        confir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String compara = String.valueOf(passwordField.getText());
                String compara2 = password;
                if(compara.equals(compara2)){
                    DataBaseHandler baseDatos = new DataBaseHandler(Main.path + "/TV_Data.db","datos",true);
                    datosLast = baseDatos.listaDatos(baseDatos.url, baseDatos.largo);
                    DefaultTableModel modeloExce = new DefaultTableModel(datosLast,headers1);
                    JTable tablaexp = new JTable(modeloExce);
                    try {
                        convierteExcel(Main.path,nombreBack.getText(),tablaexp);
                    } catch (IOException ioException) {
                    }
                    DataBaseHandler.deleteTable(Main.path);
                    JOptionPane.showMessageDialog(p,"Su Backup a sido guardado en :\n"+Main.path+"/Excel/"+nombreBack.getText()+".csv");
                    System.exit(0);
                    //p.dispose();
                }else {
                    mensaje.setText("Contraseña incorrecta... Vuelva a intentar");
                }}
        });


        p.add(etiqueta);
        p.add(refer);
        p.add(refer2);
        p.add(nombre);
        p.add(nombreBack);
        p.add(passwordField);
        p.add(confir);
        p.add(mensaje);
        p.setLayout(null);
        p.setSize(380,200);
        p.setResizable(false);
        p.setVisible(true);
    }
    private static void setInitialSize(JFrame frame, double widthPercent, double heightPercent)
    {
        Dimension newSize = new Dimension();

        newSize.setSize(
                ((screenSize.width * widthPercent) / 100),
                ((screenSize.height * heightPercent) / 100)
        );
        frame.setPreferredSize(newSize);
    }
    private static void centerWindow(JFrame frame)
    {
        Rectangle centerBounds = frame.getBounds();

        centerBounds.x = (screenSize.width/2) - (frame.getPreferredSize().width/2);
        centerBounds.y = (screenSize.height/2) - (frame.getPreferredSize().height/2);

        frame.setBounds(centerBounds);
    }
    static int perLar(int largo, int porcentaje){
        int resultado =0;
        resultado = (int) (largo*porcentaje)/100;
        return resultado ;
    }
    static int perAlt(int alto, int porcentaje){
        int resultado =0;
        resultado = (int) (porcentaje * alto)/100;
        return resultado ;
    }
    public static void encaFrame(){
        JFrame d = new JFrame("Parametros");
        d.getContentPane().setBackground(Color.LIGHT_GRAY);
        int larField = perLar(screenWidth,9);
        int larEtiq = perLar(screenWidth,5);
        int margenX1 = perLar(screenWidth,1);
        int margenX2 = perLar(screenWidth,5);
        int margenX3 = perLar(screenWidth,15);
        int margenX4 = perLar(screenWidth,19);
        int margenX5 = perLar(screenWidth,29);
        int margenX6= perLar(screenWidth,34);
        //perLar(screenWidth,5),perAlt(screenHeight,10)
        JButton guarda = new JButton("Guardar");
        guarda.setBounds(10,630,150,50);
        JButton params = new JButton("Parametros");
        params.setBounds(180,630,150,50);
        //labels
        JLabel param1 = new JLabel("Columna 1");
        param1.setBounds(margenX1,20,larEtiq,20);
        d.add(param1);
        JLabel param2 = new JLabel("Columna 2");
        param2.setBounds(margenX1,50,larEtiq,20);
        d.add(param2);
        JLabel param3 = new JLabel("Columna 3");
        param3.setBounds(margenX1,80,larEtiq,20);
        d.add(param3);
        JLabel param4 = new JLabel("Columna 4");
        param4.setBounds(margenX1,110,larEtiq,20);
        d.add(param4);
        JLabel param5 = new JLabel("Columna 5");
        param5.setBounds(margenX1,140,larEtiq,20);
        d.add(param5);
        JLabel param6 = new JLabel("Columna 6");
        param6.setBounds(margenX1,170,larEtiq,20);
        d.add(param6);
        JLabel param7 = new JLabel("Columna 7");
        param7.setBounds(margenX1,200,larEtiq,20);
        d.add(param7);

        //Entry texts
        JTextField areaAnt1 = new JTextField();
        areaAnt1.setBounds(margenX2,20,larField,20);
        d.add(areaAnt1);
        JTextField areaAnt2 = new JTextField();
        areaAnt2.setBounds(margenX2,50,larField,20);
        d.add(areaAnt2);
        JTextField areaAnt3 = new JTextField();
        areaAnt3.setBounds(margenX2,80,larField,20);
        d.add(areaAnt3);
        JTextField areaAnt4 = new JTextField();
        areaAnt4.setBounds(margenX2,110,larField,20);
        d.add(areaAnt4);
        JTextField areaAnt5 = new JTextField();
        areaAnt5.setBounds(margenX2,140,larField,20);
        d.add(areaAnt5);
        JTextField areaAnt6 = new JTextField();
        areaAnt6.setBounds(margenX2,170,larField,20);
        d.add(areaAnt6);
        JTextField areaAnt7 = new JTextField();
        areaAnt7.setBounds(margenX2,200,larField,20);
        d.add(areaAnt7);


        guarda.addActionListener(event -> {
            aplicarEnca(areaAnt1.getText(),areaAnt2.getText(),areaAnt3.getText(),areaAnt4.getText(),areaAnt5.getText(),areaAnt6.getText(),
                    areaAnt7.getText());
            d.dispose();
        });
        params.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupFrame();
                d.dispose();
            }});

        Persistencia persisSetup = new Persistencia(1);
        areaAnt1.setText(String.valueOf(persisSetup.enca[0]));
        areaAnt2.setText(String.valueOf(persisSetup.enca[1]));
        areaAnt3.setText(String.valueOf(persisSetup.enca[2]));
        areaAnt4.setText(String.valueOf(persisSetup.enca[3]));
        areaAnt5.setText(String.valueOf(persisSetup.enca[4]));
        areaAnt6.setText(String.valueOf(persisSetup.enca[5]));
        areaAnt7.setText(String.valueOf(persisSetup.enca[6]));


        d.add(guarda);
        d.add(params);
        d.setLayout(null);
        d.setSize(perLar(screenWidth,48),740);
        d.setResizable(false);
        d.setVisible(true);
    }


}
