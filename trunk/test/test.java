import java.io.*;

public class test {
    
    private static final String DOC = "<?xml version='1.0' encoding='UTF-8'?>\n<default_element>ö</default_element>";
    
    public static void main(String args[]) {
        try {
            
           // Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("debug2.xml"), "UTF-8"), 32768);
           // Reader in = new InputStreamReader(new ByteArrayInputStream(DOC.getBytes("UTF-8")), "UTF-8");
            
           // char[] buf = new char[5120];
           // int bytesRead = in.read(buf, 0, 5120);
            
           // writer.write(buf,0,bytesRead);
           // writer.flush();
           // writer.close();
            
            FileWriter fileWriter = new FileWriter("debug.xml");
            StringReader reader = new StringReader(DOC);
            char[] test = new char[DOC.length()];
            reader.read(test, 0, DOC.length());
            fileWriter.write(test);
            fileWriter.flush();
            fileWriter.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
}
