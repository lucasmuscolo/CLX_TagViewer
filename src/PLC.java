
import etherip.EtherNetIP;
import etherip.types.CIPData;
import javax.swing.*;

public class PLC {

    public static Object[] obtenerDatos(String ip, int slot,Object[] tags){
        Object[] listaEnviar = new Object[Main.largoDataBase-3];
        String lectura,aux;
        System.out.println("Conectando a: "+ ip + "/" + slot);
        EtherNetIP plc = new EtherNetIP(ip,slot);
        try {
            plc.connectTcp();
        } catch (Exception e) {
            System.out.println("Error de conexión al PLC");

        }
        for (int r = 0; r<listaEnviar.length;r++){
            if(tags[r]!="none"){
        try {

                    CIPData valor = plc.readTag(String.valueOf(tags[r]));
                    lectura = String.valueOf(valor);
                    aux= lectura.substring(20,lectura.length()-1);
                    listaEnviar[r] = Double.parseDouble(aux);
                    GUI.plcNot = true;

            }
         catch (Exception e) {
            System.out.println("Missing Tag Name on PLC");
            GUI.plcNot = false;
            break;

        }
        }else{listaEnviar[r] = 0.0;}}
        return listaEnviar;
    }
    public static int consultaBit(String ip, int slot,String tag,String tag2){
        EtherNetIP plc = new EtherNetIP(ip,slot);
        String lectura,aux,lectura2,aux2;
        boolean bitState1 = false;
        boolean bitState2 = false;
        int result = 0;
        try {
            plc.connectTcp();
            CIPData valor = plc.readTag(tag);
            lectura = String.valueOf(valor);
            aux= lectura.substring(20,lectura.length()-1);
            if (aux.equals("0")){bitState1 = false;}
            else{bitState1 = true;}
            CIPData valor2 = plc.readTag(tag2);
            lectura2 = String.valueOf(valor2);
            aux2= lectura2.substring(20,lectura2.length()-1);
            if (aux2.equals("0")){bitState2 = false;}
            else{bitState2 = true;}
        } catch (Exception e) {
            System.out.println("Missing Tag Name on PLC");
        }
        if (bitState1 ==true && bitState2 ==false){
            result = 1;
        }
        if (bitState1 ==false && bitState2 ==true){
            result = 2;
        }
        if (bitState1 ==true && bitState2 ==true){
            result = 3;
        }
        return result;
    }
    public static String leeStringPLC(EtherNetIP plc,String tag, int largo){
        String valRet="";
        String aux1,aux2,auxTag;
        for (int i = 0;i < largo;i++){
            auxTag = tag.substring(0,tag.length()-2);
            auxTag = auxTag + i +"]";
            try {
                CIPData lectura = plc.readTag(auxTag);
                aux1 = String.valueOf(lectura);
                aux2 = aux1.substring(20,aux1.length()-1);
                int o = Integer.parseInt(aux2);
                if (o != 0){char c = (char)o;
                    valRet = valRet + c;}
            }catch (Exception e) { e.printStackTrace();}}
        return valRet;
    }
    public static double leeDoublePLC(EtherNetIP plc, String tag){
        String aux1,aux2;
        Double result = 0.0;
        try {
            CIPData lectura = plc.readTag(tag);
            aux1 = String.valueOf(lectura);
            aux2 = aux1.substring(20,aux1.length()-1);
            result = Double.parseDouble(aux2);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }
    public static String leeHoraDoublePLC(EtherNetIP plc, String tag){
        String aux1,aux2;
        char[] res = new char[7];
        char[] auxRes = new char[8];
        String horaFormato = "";
        try {
            CIPData lectura = plc.readTag(tag);
            aux1 = String.valueOf(lectura);
            aux2 = aux1.substring(20,aux1.length()-1);
            res = aux2.toCharArray();
            auxRes[0] = res[0];
            auxRes[1] = res[1];
            auxRes[2] = ':';
            auxRes[3] = res[2];
            auxRes[4] = res[3];
            auxRes[5] = ':';
            auxRes[6] = res[5];
            auxRes[7] = res[6];
            horaFormato = String.valueOf(auxRes);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return horaFormato;
    }

}
