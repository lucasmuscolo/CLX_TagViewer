import java.io.*;

public class FilesManager {

    public static boolean searchForFile(String path, String fecha,String name){
        File carpeta = new File(path);
        String[] listaCarp = carpeta.list();
        String Aux = name +"_"+fecha+".db";
        //GUI.MonitorLogSetTexT("Buscando\n"+name+"_\n"+fecha+".db");
        boolean count = true;
        String item="";
        if (listaCarp == null || listaCarp.length == 0){
         //   GUI.MonitorLogSetTexT("Archivo no encontrado\n");
        }
        else {
            for (int i = 0; i< listaCarp.length;i++){
                item = listaCarp[i];
                if (item.equals(Aux))
                {count = true;
                break;}
                else{count = false;};
            }
        }

        return count;
    }
    public static void nuevoArchivoFecha(String fecha, String path){
        String exten = "";
        if (path.contains(".")) {
            int i = path.lastIndexOf('.');
            exten = i > 0 ? path.substring(i + 1) : "";
        }
        InputStream archivoOriginal = null;
        OutputStream archivoCopia = null;
        File fileOriginal = new File(path);
        String aux1 = "."+exten;
        String aux2 = "_"+fecha+"."+exten;
        String nuevoArch = path.replace(aux1,aux2);
        File fileCopia = new File(nuevoArch);
        //GUI.MonitorLogSetTexT("Generando Archivo..\n" + nuevoArch);
        try {
            archivoOriginal = new FileInputStream(fileOriginal);
            archivoCopia = new FileOutputStream(fileCopia);
            byte[] buffer = new byte[1024];
            int largo;
            while ((largo = archivoOriginal.read(buffer)) > 0) {
                archivoCopia.write(buffer,0,largo);
            }
            archivoOriginal.close();
            archivoCopia.close();
            //GUI.MonitorLogSetTexT("Archivo generado");
        } catch (IOException e) {
            //GUI.MonitorLogSetTexT("Archivo no generado");
        }
    }
}
