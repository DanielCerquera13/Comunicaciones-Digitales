package lzw;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


// Clase LZW 
public class LZW {

    public HashMap comprimirDiccionario; // Se crea un HashMap llamado comprimirDiccionario en el cual se agregan los códigos de compresión
    public HashMap descomprimirDiccionario; // Se crea un HashMap llamadode descomprimirDiccionario en el cual se agregan los códigos de descompresión
    String fileName = "files/texto.txt"; //fileName equivale a la ruta donde se encuentra el archivo original que va a ser comprimido
    int lastcode = 0; 
    int dlastcode = 0;
    public static int CODASCII = 8; //Constante que hace referencia a los 8 bits con los que trabajamos en este codigo
    double bitsoriginal = 0;
    double bitscodified = 0;

    LZW() {
    	
    	//Inicialización de las variables comprimirDiccionario y descomprimirDiccionario
        comprimirDiccionario = new HashMap<String, Integer>(); 
        descomprimirDiccionario = new HashMap<Integer, String>();
        crearDiccionario();
    }

    // Método crearDiccionario, que como su nombre lo indica en este se extraen los códigos del archivo original y se agregan a un HashMap para realizar 
    // cada uno de los procesos (compresión y descompresión)
    public void crearDiccionario() {
        try {
            int code;
            char ch;
            FileInputStream fileInputStream = new FileInputStream(fileName); //Es el encargado de extraer la ruta del archivo original para posteriormente ser leído.
            																 //Se pasa por parámetro la ruta definida anteriormente en las variables
            
            InputStreamReader reader = new InputStreamReader(fileInputStream, "utf-8"); //Se encargada de leer el archivo anteriormente cargado en FileInputStream, 
            ArrayList<String> text = new ArrayList<String>();																			//Esta lectura se hace mediante el formato utf-8
            
            //Ciclo que agrega los códigos al diccionario cada que va realizando la lectura del archivo
            while ((code = (int) reader.read()) != -1) {
                ch = (char) code;
                text.add(ch + "");
                

                // En caso de que el código no se encuentre agragado en el HashMap correspondiente al diccionario, ya sea para comprimir o descomprimir,
                // se agrega ese nuevo código
                if (!comprimirDiccionario.containsKey(ch)) {
                    comprimirDiccionario.put("" + ch, code);
                    descomprimirDiccionario.put(code, "" + ch);
                    if (code > lastcode) {
                        lastcode = code;
                        dlastcode = code;
                    }
                }
            }
            System.out.println("TEXTO:");
            System.out.println(text);
            System.out.println("\nTAMAÑO DEL TEXTO:");
            System.out.println(text.size());
           
            bitsoriginal = text.size()*CODASCII; // BITS INFORMACION ORGINAL(PREGUNTA 1)
            
            fileInputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    

	// Método para comprimir el archivo original
    public void comprimirArchivo() {
        try {
        	
        	//Creación de variables
            int code; 
            int codeword;
            char ch;
            String string;
            
            ArrayList<Integer> outputCodes = new ArrayList<>(); //ArrayList que hace referencia a los códigos de salida al comprimir el archivp
            ArrayList<String> binaryCodes = new ArrayList<>();  //ArrayList que hace referencia a los códigos de salida, pero esta vez en formato binario.
            

            System.out.print("\nComprimiendo...");
            FileInputStream fileInputStream = new FileInputStream(fileName); //Al igual que en el método crearDiccionario, se extrae la ruta del archivo original.
            InputStreamReader reader = new InputStreamReader(fileInputStream, "US-ASCII"); //Realiza la lectura del archivo anteriormente cargado.
            FileOutputStream fileOutputStream = new FileOutputStream(fileName + "1.lzw"); //Se crea un nuevo archivo formato .lzw en el cual se va a guardar el archivo comprimido.
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream); //Escribe la salida del método en el archivo anteriormente creado (.lzw)
           

            //Se agregan los códigos al archivo para obtener la compresión adecuada
            string = (char) reader.read() + "";
            while ((code = (int) reader.read()) != -1) {
                ch = (char) code;

                if (!comprimirDiccionario.containsKey(string + ch)) {
                    codeword = Integer.parseInt(comprimirDiccionario.get(string).toString());
                    outputStream.writeInt(codeword);
                    
                    comprimirDiccionario.put(string + ch, ++lastcode);
                    outputCodes.add(codeword); //Se agrega cada una de las salidas del cógido
                    String word = Integer.toBinaryString(codeword); //Se convierte la palabra código a binario.
                    binaryCodes.add(word+ ""); //Se agrega cada uno de los códigos anteriormente pasados a binarios, que corresponde a la salida del código.  
                         
                    string = "" + ch;
                    
                } else {
                    string = string + ch;
                }
                
            }
            
            
            int phraseCode=Integer.parseInt(comprimirDiccionario.get(string).toString());             
            outputCodes.add(phraseCode); //Se agrega el último código al archivo.
            
            binaryCodes.add(Integer.toBinaryString(phraseCode)); //Se agrega el último código de salida, pero en este caso en formato binario.
            
            codeword = Integer.parseInt(comprimirDiccionario.get(string).toString());
            outputStream.writeInt(codeword); //Se escribe la ultima palabra código en el archivo destino.
            outputStream.writeInt(00);
            

            outputStream.close();
            fileInputStream.close();

            System.out.println("\nDICCIONARIO: ");
            System.out.println(comprimirDiccionario); //Muestra por consola el diccionario obtenido en el proceso de compresión.
            System.out.println("\nTAMAÑO DEL DICCIONARIO");
            System.out.println(comprimirDiccionario.size());
            System.out.println("\nCÓDIGOS DE SALIDA EN DECIMAL");
            System.out.println(outputCodes); //Se imprime por consola los códigos de salida en formato decimal.
            System.out.println("\nTAMAÑO DEL ARRAYLIST DE CÓDIGOS DECIMALES");
            System.out.println(outputCodes.size()); //Se imprime el tamaño del arraylist correspondiente a los códigos de salida en decimal.
            System.out.println("\nCÓDIGOS DE SALIDA EN BINARIO");
            System.out.println(binaryCodes); //Se imprime por consola los códigos de salida en formato binario.
            System.out.println("\nTAMAÑO DEL ARRAYLIST DE CÓDIGOS BINARIOS");
            System.out.println(binaryCodes.size()); //Se muestra por consola el tamaño del arraylist correspondiente a los códigos de salida en formato binario.
            
            String texto ="";
            for(int i=0; i<binaryCodes.size(); i++) {
            	
            	
            	texto += binaryCodes.get(i);
            	
            }
            
            
            
            System.out.println("\nTEXTO EN BINARIO (1 y 0)");
            System.out.println(texto);
            
            System.out.println("\nBITS INFORMACION ORGINAL(PREGUNTA 1):");
            System.out.println(bitsoriginal);
            
            System.out.println("\nBITS DE LA INFORMACION CODIFICADA DEL TEXTO(PREGUNTA 2):");
            double log = Math.log(comprimirDiccionario.size()) / Math.log(2); //Calculo de la informacion del texto al ser codificado
            int logwceil = (int)Math.ceil(log);
            System.out.println(bitscodified = logwceil * outputCodes.size());
           
            System.out.println("\nRELACION DE COMPRESION(PREGUNTA 3):");
            System.out.println(bitsoriginal/bitscodified); //Calculo de la relacion de compresion
            
            System.out.print("\nHecho!!!");
           

        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    //Método que descomprime el archivo anteriormente comprimido, para obtener el archivo original
    public void descomprimirArchivo() {
        int priorcode = -1;
        int codeword = -1;
        String priorstr; 
        String string;
        
           

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName + "1.lzw"); //Extrae la ruta del archivo que anteriormente se comprimió, para empezar el proceso de descompresión.
            FileWriter writer = new FileWriter(fileName + "2.txt"); //Se crea un archivo .txt en el cual se va a guardar el texto al ser descomprimido.
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream); //Escribe la salida del método de descompresión en el archivo .txt anteriormente creado.

            System.out.print("\n\nDescomprimiendo...");
            priorcode = inputStream.readInt(); //Se leen los códigos del archivo del diccionario
            writer.write(descomprimirDiccionario.get(priorcode).toString()); //Se escriben los códigos del diccionario
            while ((codeword = inputStream.readInt()) != -1) {
                if(codeword == 00)
                    break;

                priorstr = descomprimirDiccionario.get(priorcode).toString(); //Se pasa los códigos que se tiene en fomato en el HashMap en formato entero a formato String

                //Se agregan  los códigos que faltan al HashMap
                if (descomprimirDiccionario.containsKey(codeword)) {
                    string = descomprimirDiccionario.get(codeword).toString();
                    writer.write(string); //Se escriben los string, que corresponden a la concatenación del texto.
                    descomprimirDiccionario.put(++dlastcode, priorstr + string.charAt(0));
                } else {
                    descomprimirDiccionario.put(++dlastcode, priorstr + priorstr.charAt(0));
                    writer.write(priorstr + priorstr.charAt(0));
                }

                priorcode = codeword;
            }

            writer.close();
            fileInputStream.close();
            System.out.println("\nDICCIONARIO:");
            System.out.println(descomprimirDiccionario);
            System.out.print("\nHecho!!!");
            

        } catch (Exception ex) {
            //Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("\n\nError: " + ex.getMessage());
            System.out.print(codeword + " " + priorcode + " " + descomprimirDiccionario.get(133) + " " + dlastcode);
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    

    public static void main(String args[]) {
        LZW lzw = new LZW();
        lzw.comprimirArchivo(); //Se ejecuta el método para comprimir el archivo original
        lzw.descomprimirArchivo(); //Se ejecuta el método para descomprimir el archivo que anteriormente se comprimió
    }
}
