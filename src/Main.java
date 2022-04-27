import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {
//Flowserve_PH_Lab
    static String appName = "Flowserve Laboratorio PH";
    static String path = "C:/AtiiSoftware/CLX_TagViewerFree";
    static String ipAdress;
    static int slotNum;
    static int timeCant;
    static int largoDataBase = 10;
    static String fecha;
    static Object[][] datosFirst;
    static Persistencia encabezados;
    static boolean rstAuto = false;
    static Object[][] datos1,datos2,datos3;

    static Object[] listaVars = new Object[largoDataBase];
    public static void main(String[] args){
       fecha = fecha();
       Persistencia persis = new Persistencia(0);
       encabezados = new Persistencia(1);
       ipAdress = String.valueOf(persis.atii[0]);
       slotNum = Integer.parseInt(String.valueOf(persis.atii[1]));
       timeCant = Integer.parseInt(String.valueOf(persis.atii[2]));
        for (int i = 3; i < largoDataBase;i++){
            String aux = String.valueOf(persis.atii[i]);
            if(aux!="none"|| persis.atii[i]!= null){
            listaVars[i-3] = String.valueOf(persis.atii[i]);
        }}
       DataBaseHandler baseDatos = new DataBaseHandler(path + "/TV_Data.db","datos",true);
       datosFirst = baseDatos.listaDatos(baseDatos.url, baseDatos.largo);


       GUI.MainFrame();



    }

    public static String fecha(){
        //El problema con Calendar es que los meses los devuelve del 0 al 11, asi que se le suma 1 siempre para que de del 1 al 12
        Calendar fecha = new GregorianCalendar();
        int Auxmes = Integer.parseInt(String.valueOf(fecha.get(Calendar.MONTH)));
        int mes = Auxmes + 1;
        return fecha.get(Calendar.DAY_OF_MONTH)+"/"+mes+"/"+ fecha.get(Calendar.YEAR);
    }
    public static String hora(){
        Calendar hora = new GregorianCalendar();
        String aux = String.valueOf(hora.getTime());
        String aux2 = aux.substring(11,19);


        return aux2;
    }
    public static Object[][] splitDataBase(Object[][] datos,int largo){
        Object[][] newbase = new Object[datos.length][];
        int largoListaAux;
        if(largo==0){
            largoListaAux=22;
            for (int i = 0; i<datos.length;i++){
                Object[] aux = new Object[largoListaAux];
                for(int t = 0;t < largoListaAux;t++){
                    aux[t]= datos[i][t+largo];
                }
                newbase[i]= aux;
            }

        }if(largo==21){
            largoListaAux=20;
            for (int i = 0; i<datos.length;i++){
                Object[] aux = new Object[largoListaAux];
                aux[0]=datos[i][0];
                for(int t = 1;t < largoListaAux;t++){
                    aux[t]= datos[i][t+largo];
                }
                newbase[i]= aux;
            }
        }if(largo==40){
            largoListaAux=16;
            for (int i = 0; i<datos.length;i++){
                Object[] aux = new Object[largoListaAux];
                aux[0]=datos[i][0];
                for(int t = 1;t < largoListaAux;t++){
                    //System.out.println(t+largo);
                    aux[t]= datos[i][t+largo];
                }
                newbase[i]= aux;
            }
        }
        return newbase;
    }
}
