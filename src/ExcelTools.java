import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExcelTools {
    public static void ExcelTools(){}
    public static void exportTable(JTable tabla, File file) throws IOException {
        TableModel model = tabla.getModel();
        FileWriter out = new FileWriter(file);

        for (int i = 0; i < model.getColumnCount(); i++) {
            out.write(model.getColumnName(i) + ";"+"\t");
        }
        out.write("\n");
        for(int i=0; i<model.getRowCount();i++){
            for(int j = 0; j < model.getColumnCount();j++){
                if (model.getValueAt(i,j)!=null){
                    out.write(model.getValueAt(i,j).toString()+";"+"\t");
                }else{out.write("0;\t");}
                //out.write(model.getValueAt(i,j).toString()+";"+"\t");

            }
            out.write("\n");
        }
        out.close();
        //System.out.println("write out to: "+ file);
    }

}
