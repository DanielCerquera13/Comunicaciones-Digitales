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

    public HashMap comprimirDiccionario; // Se crea un HashMap llamado comprimirDiccionario en el cual se agregan los c�digos de compresi�n
    public HashMap descomprimirDiccionario; // Se crea un HashMap llamadode descomprimirDiccionario en el cual se agregan los c�digos de descompresi�n
    String fileName = "files/texto.txt"; //fileName equivale a la ruta donde se encuentra el archivo original que va a ser comprimido
    int lastcode = 0; 
    int dlastcode = 0;
    public static int CODASCII = 8; //Constante que hace referencia a los 8 bits con los que trabajamos en este codigo
    double bitsoriginal = 0;
    double bitscodified = 0;

    LZW() {
    	
    	//Inicializaci�n de las variables comprimirDiccionario y descomprimirDiccionario
        comprimirDiccionario = new HashMap<String, Integer>(); 
        descomprimirDiccionario = new HashMap<Integer, String>();
        crearDiccionario();
    }

    // M�todo crearDiccionario, que como su nombre lo indica en este se extraen los c�digos del archivo original y se agregan a un HashMap para realizar 
    // cada uno de los procesos (compresi�n y descompresi�n)
    public void crearDiccionario() {
        try {
            int code;
            char ch;
            FileInputStream fileInputStream = new FileInputStream(fileName); //Es el encargado de extraer la ruta del archivo original para posteriormente ser le�do.
            																 //Se pasa por par�metro la ruta definida anteriormente en las variables
            
            InputStreamReader reader = new InputStreamReader(fileInputStream, "utf-8"); //Se encargada de leer el archivo anteriormente cargado en FileInputStream, 
            ArrayList<String> text = new ArrayList<String>();																			//Esta lectura se hace mediante el formato utf-8
            
            //Ciclo que agrega los c�digos al diccionario cada que va realizando la lectura del archivo
            while ((code = (int) reader.read()) != -1) {
                ch = (char) code;
                text.add(ch + "");
                

                // En caso de que el c�digo no se encuentre agragado en el HashMap correspondiente al diccionario, ya sea para comprimir o descomprimir,
                // se agrega ese nuevo c�digo
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
            System.out.println("\nTAMA�O DEL TEXTO:");
            System.out.println(text.size());
           
            bitsoriginal = text.size()*CODASCII; // BITS INFORMACION ORGINAL(PREGUNTA 1)
            
            fileInputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    

	// M�todo para comprimir el archivo original
    public void comprimirArchivo() {
        try {
        	
        	//Creaci�n de variables
            int code; 
            int codeword;
            char ch;
            String string;
            
            ArrayList<Integer> outputCodes = new ArrayList<>(); //ArrayList que hace referencia a los c�digos de salida al comprimir el archivp
            ArrayList<String> binaryCodes = new ArrayList<>();  //ArrayList que hace referencia a los c�digos de salida, pero esta vez en formato binario.
            

            System.out.print("\nComprimiendo...");
            FileInputStream fileInputStream = new FileInputStream(fileName); //Al igual que en el m�todo crearDiccionario, se extrae la ruta del archivo original.
            InputStreamReader reader = new InputStreamReader(fileInputStream, "US-ASCII"); //Realiza la lectura del archivo anteriormente cargado.
            FileOutputStream fileOutputStream = new FileOutputStream(fileName + "1.lzw"); //Se crea un nuevo archivo formato .lzw en el cual se va a guardar el archivo comprimido.
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream); //Escribe la salida del m�todo en el archivo anteriormente creado (.lzw)
           

            //Se agregan los c�digos al archivo para obtener la compresi�n adecuada
            string = (char) reader.read() + "";
            while ((code = (int) reader.read()) != -1) {
                ch = (char) code;

                if (!comprimirDiccionario.containsKey(string + ch)) {
                    codeword = Integer.parseInt(comprimirDiccionario.get(string).toString());
                    outputStream.writeInt(codeword);
                    
                    comprimirDiccionario.put(string + ch, ++lastcode);
                    outputCodes.add(codeword); //Se agrega cada una de las salidas del c�gido
                    String word = Integer.toBinaryString(codeword); //Se convierte la palabra c�digo a binario.
                    binaryCodes.add(word+ ""); //Se agrega cada uno de los c�digos anteriormente pasados a binarios, que corresponde a la salida del c�digo.  
                         
                    string = "" + ch;
                    
                } else {
                    string = string + ch;
                }
                
            }
            
            
            int phraseCode=Integer.parseInt(comprimirDiccionario.get(string).toString());             
            outputCodes.add(phraseCode); //Se agrega el �ltimo c�digo al archivo.
            
            binaryCodes.add(Integer.toBinaryString(phraseCode)); //Se agrega el �ltimo c�digo de salida, pero en este caso en formato binario.
            
            codeword = Integer.parseInt(comprimirDiccionario.get(string).toString());
            outputStream.writeInt(codeword); //Se escribe la ultima palabra c�digo en el archivo destino.
            outputStream.writeInt(00);
            

            outputStream.close();
            fileInputStream.close();

            System.out.println("\nDICCIONARIO: ");
            System.out.println(comprimirDiccionario); //Muestra por consola el diccionario obtenido en el proceso de compresi�n.
            System.out.println("\nTAMA�O DEL DICCIONARIO");
            System.out.println(comprimirDiccionario.size());
            System.out.println("\nC�DIGOS DE SALIDA EN DECIMAL");
            System.out.println(outputCodes); //Se imprime por consola los c�digos de salida en formato decimal.
            System.out.println("\nTAMA�O DEL ARRAYLIST DE C�DIGOS DECIMALES");
            System.out.println(outputCodes.size()); //Se imprime el tama�o del arraylist correspondiente a los c�digos de salida en decimal.
            System.out.println("\nC�DIGOS DE SALIDA EN BINARIO");
            System.out.println(binaryCodes); //Se imprime por consola los c�digos de salida en formato binario.
            System.out.println("\nTAMA�O DEL ARRAYLIST DE C�DIGOS BINARIOS");
            System.out.println(binaryCodes.size()); //Se muestra por consola el tama�o del arraylist correspondiente a los c�digos de salida en formato binario.
            
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

    
    //M�todo que descomprime el archivo anteriormente comprimido, para obtener el archivo original
    public void descomprimirArchivo() {
        int priorcode = -1;
        int codeword = -1;
        String priorstr; 
        String string;
        
           

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName + "1.lzw"); //Extrae la ruta del archivo que anteriormente se comprimi�, para empezar el proceso de descompresi�n.
            FileWriter writer = new FileWriter(fileName + "2.txt"); //Se crea un archivo .txt en el cual se va a guardar el texto al ser descomprimido.
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream); //Escribe la salida del m�todo de descompresi�n en el archivo .txt anteriormente creado.

            System.out.print("\n\nDescomprimiendo...");
            priorcode = inputStream.readInt(); //Se leen los c�digos del archivo del diccionario
            writer.write(descomprimirDiccionario.get(priorcode).toString()); //Se escriben los c�digos del diccionario
            while ((codeword = inputStream.readInt()) != -1) {
                if(codeword == 00)
                    break;

                priorstr = descomprimirDiccionario.get(priorcode).toString(); //Se pasa los c�digos que se tiene en fomato en el HashMap en formato entero a formato String

                //Se agregan  los c�digos que faltan al HashMap
                if (descomprimirDiccionario.containsKey(codeword)) {
                    string = descomprimirDiccionario.get(codeword).toString();
                    writer.write(string); //Se escriben los string, que corresponden a la concatenaci�n del texto.
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
        lzw.comprimirArchivo(); //Se ejecuta el m�todo para comprimir el archivo original
        lzw.descomprimirArchivo(); //Se ejecuta el m�todo para descomprimir el archivo que anteriormente se comprimi�
    }
}
