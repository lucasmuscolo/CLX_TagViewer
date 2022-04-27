public class Persistencia {

    Object[] atii = new Object[Main.largoDataBase];
    Object[] enca = new Object[Main.largoDataBase];



    public Persistencia(int numBase){

        if(numBase == 0){
            DataBaseHandler persis = new DataBaseHandler(Main.path + "/appDataMem.db","persist",false);
            this.atii = persis.persisDatosGet();
        }else{
            DataBaseHandler persis = new DataBaseHandler(Main.path + "/appDataMem.db","enca",false);
            this.enca = persis.persisEncaGet();
        }



}



}
