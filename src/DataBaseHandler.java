import java.sql.*;

public class DataBaseHandler {

    String url = "jdbc:sqlite:";
    int largo;
    String fecha;


    public DataBaseHandler(String path,String tabladb,Boolean IdOn) {
        this.url = this.url + path;
        Connect(url);
        this.largo = rango(url,tabladb,IdOn);
        System.out.println(url);
    }
    public static void Connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Conexion a base de datos Exitosa");
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException er) {
                System.out.print(er.getMessage());
            }
        }
    }
    public static int rango(String url,String base, Boolean IdOn) {
       String query ="";
        if (IdOn){
            query = "SELECT * FROM "+base+" ORDER by ID ASC";
        }
        else{
            query = "SELECT * FROM "+base;
        }


        Connection conn;
        int valor = 0;
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                valor = valor + 1;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //System.out.println(valor);
        return valor;
    }
    //Creo el Objeto de datos para la tabla
    public Object[][] listaDatos(String url, int largo) {
        String query = "SELECT * FROM datos ORDER by ID ASC";
        Connection conn;
        Object[][] lista = new Object[largo][];
        final int cantColum = Main.largoDataBase-2;

        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            int index = 0;
            while (res.next()) {
                lista[index] = new Object[cantColum];
                lista[index][0] = res.getString("hora");
                //lista[index][1] = res.getString("hora");

                for (int i =1; i<cantColum;i++){
                    lista[index][i] = res.getDouble("Dato"+String.valueOf(i));
                    //System.out.println(i);
                }
                index = index + 1;
            }
            conn.close();
        } catch (SQLException throwables) {

        //ver que mensaje poner
        }

        return lista;
    }
    public static void cargaSql(String path, String hora, Object[] lista) {
        String url = "jdbc:sqlite:" + path + "/TV_Data.db";
        String query = "INSERT INTO datos(hora,Dato1,Dato2,Dato3,Dato4,Dato5,Dato6,Dato7," +
                "Dato8,Dato9,Dato10,Dato11,Dato12,Dato13,Dato14,Dato15,Dato16,Dato17,Dato18," +
                "Dato19,Dato20,Dato21,Dato22,Dato23,Dato24,Dato25,Dato26,Dato27,Dato28,Dato29,Dato30," +
                "Dato31,Dato32,Dato33,Dato34,Dato35,Dato36,Dato37,Dato38,Dato39,Dato40," +
                "Dato41,Dato42,Dato43,Dato44,Dato45,Dato46,Dato47,Dato48,Dato49,Dato50,Dato51,Dato52,Dato53,Dato54,Dato55)"+
                "\nVALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn;
        try {
            conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1,hora);
            for (int i =2;i < Main.largoDataBase-1;i++){

                if (lista[i-2]!=null){
                    stmt.setDouble(i,Double.parseDouble(String.valueOf(lista[i-2])));
                }
                else{
                    stmt.setDouble(i,0.0);
                }
            }
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException throwables) {
            System.out.println("No se puede cargar los datos en "+url);
        }
    }
    public static Object[] obtieneUltimo(String path,int inicio) {
        String url = "jdbc:sqlite:" + path + "/TV_Data.db";
        String query = "SELECT * FROM datos WHERE ID = (SELECT MAX(ID)FROM datos)";
        Connection conn;
        int largoListaAux = 0;
        if(inicio==0){largoListaAux=22;}if(inicio==21){largoListaAux=20;}if(inicio==40){largoListaAux=16;}
        Object[] lista = new Object[largoListaAux];
        try {
                conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(query);
                lista[0] = res.getString("hora");
                for (int i = 1; i < largoListaAux;i++){
                    lista[i] = res.getDouble("Dato"+ String.valueOf(i+inicio));
                    }
                conn.close();
            } catch (SQLException e) {e.printStackTrace();
        System.out.println("eseste");}
        return lista;
    }
    public Object[] persisDatosGet(){
        String query = "SELECT * FROM persist";
        Connection conn;
        Object[] datosProg = new Object[Main.largoDataBase];
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            datosProg[0] = res.getString("ip");
            datosProg[1] = res.getInt("slot");
            datosProg[2] = res.getLong("tiempo");
            for (int i = 3; i < Main.largoDataBase; i++){
                datosProg[i] = res.getString("tag_"+(i-2));

            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return datosProg ;
    }
    public Object[] persisEncaGet(){
        String query = "SELECT * FROM enca";
        Connection conn;
        Object[] datosProg = new Object[Main.largoDataBase];
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            for (int i = 0; i < Main.largoDataBase-3; i++){
                datosProg[i] = res.getString("Field"+(i+1));
            }


            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return datosProg ;
    }
    public static void guardaTiempo(int tiempo){
        String url = "jdbc:sqlite:C:/AtiiSoftware/Flowserve_PH_Lab/appDataMem.db";
        String query = "UPDATE persist SET tiempo ='"+tiempo+"'";
        Connection conn;
        try {
            conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }



    }
    public static void deleteTable(String path){
        String url = "jdbc:sqlite:" + path + "/TV_Data.db";
        String query = "DELETE FROM datos WHERE ID ";
        Connection conn;
        try {
            conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException throwables) {
            System.out.println("No se puede borrar datos de "+url);
        }
    }
}
