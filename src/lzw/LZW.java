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

public class LZW {

    public HashMap comprimirDiccionario;
    public HashMap descomprimirDiccionario;
    String fileName = "files/texto.txt";
    int lastcode = 0;
    int dlastcode = 0;

    LZW() {
        comprimirDiccionario = new HashMap<String, Integer>();
        descomprimirDiccionario = new HashMap<Integer, String>();
        crearDiccionario();
    }

    public void crearDiccionario() {
        try {
            int code;
            char ch;
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader reader = new InputStreamReader(fileInputStream, "utf-8");
            while ((code = (int) reader.read()) != -1) {
                ch = (char) code;

                if (!comprimirDiccionario.containsKey(ch)) {
                    comprimirDiccionario.put("" + ch, code);
                    descomprimirDiccionario.put(code, "" + ch);
                    if (code > lastcode) {
                        lastcode = code;
                        dlastcode = code;
                    }
                }
            }
            fileInputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void comprimirArchivo() {
        try {
            int code; 
            int codeword;
            char c;
            String s;
            ArrayList<Integer> outputCodes = new ArrayList<>();

            System.out.print("Comprimiendo...");
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader reader = new InputStreamReader(fileInputStream, "utf-8");
            FileOutputStream fileOutputStream = new FileOutputStream(fileName + "1.lzw");
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);

            s = (char) reader.read() + "";
            while ((code = (int) reader.read()) != -1) {
                c = (char) code;

                if (!comprimirDiccionario.containsKey(s + c)) {
                    codeword = Integer.parseInt(comprimirDiccionario.get(s).toString());
                    outputStream.writeInt(codeword);
                    comprimirDiccionario.put(s + c, ++lastcode);
                    outputCodes.add(codeword);
                    s = "" + c;
                } else {
                    s = s + c;
                }
                
            }
            
            

            outputCodes.add(Integer.parseInt(comprimirDiccionario.get(s).toString()));
            codeword = Integer.parseInt(comprimirDiccionario.get(s).toString());
            outputStream.writeInt(codeword);
            outputStream.writeInt(00);

            outputStream.close();
            fileInputStream.close();

            System.out.println("\n"+comprimirDiccionario);
            System.out.println(outputCodes);
            System.out.println(outputCodes.size());
            System.out.print("\nHecho!!!");
           

        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void descomprimirArchivo() {
        int priorcode = -1;
        int codeword = -1;
        char c;

        String priorstr; 
        String str;
        FileInputStream fileInputStream; 
        FileWriter writer; 
        ObjectInputStream inputStream;

        try {
            fileInputStream = new FileInputStream(fileName + "1.lzw");
            writer = new FileWriter(fileName + "2.txt");
            inputStream = new ObjectInputStream(fileInputStream);

            System.out.print("\n\nDescomprimiendo...");
            priorcode = inputStream.readInt();
            writer.write(descomprimirDiccionario.get(priorcode).toString());
            while ((codeword = inputStream.readInt()) != -1) {
                if(codeword == 00)
                    break;

                priorstr = descomprimirDiccionario.get(priorcode).toString();

                if (descomprimirDiccionario.containsKey(codeword)) {
                    str = descomprimirDiccionario.get(codeword).toString();
                    writer.write(str);
                    descomprimirDiccionario.put(++dlastcode, priorstr + str.charAt(0));
                } else {
                    descomprimirDiccionario.put(++dlastcode, priorstr + priorstr.charAt(0));
                    writer.write(priorstr + priorstr.charAt(0));
                }

                priorcode = codeword;
            }

            writer.close();
            fileInputStream.close();
            System.out.println("\n"+descomprimirDiccionario);
            System.out.print("\nHecho!!!");
            

        } catch (Exception ex) {
            //Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("\n\nError: " + ex.getMessage());
            System.out.print(codeword + " " + priorcode + " " + descomprimirDiccionario.get(133) + " " + dlastcode);
        }
    }

    public static void main(String args[]) {
        LZW lzw = new LZW();
        lzw.comprimirArchivo();
        lzw.descomprimirArchivo();
    }
}
