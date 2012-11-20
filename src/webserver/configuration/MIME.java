
package webserver.configuration;
/**
 * This class reads the MIME file and generates a hash map for the entries in the 
 * file. 
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MIME {
    
    
    private BufferedReader mimeFile; 
    private boolean hasMore;
    private String nextLine;
    
    public MIME(String mFile) {
        try {            
            this.mimeFile = new BufferedReader(new FileReader(mFile));
        } catch (FileNotFoundException ex) {
            System.out.println("MIME file was not found!");
            System.exit(1);
        }
        hasMore = true;       
    }
    
    private boolean hasMoreTypes() {
        try {
            nextLine = mimeFile.readLine();
            if (nextLine == null) 
                hasMore = false;
        } catch (IOException e) {
            System.out.println("Reading File Error: " + e.getMessage());
            System.exit(1);
        }
        return hasMore;
    }
    
    private void getNextType() {
        String temp[];
        
        String type;
        String delimiter = "\\s+";
        if (!nextLine.startsWith("#") && nextLine.length() != 0) {
            try {
                temp = nextLine.split(delimiter);
                type = temp[0];
                for (int i = 1; i < temp.length; i++) {
                    ConfigVars.putMIME(temp[i], type);
                }                
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("The '" + nextLine + "' type has no extention!");
                }
            }
            
        }        
    
    public void processMIMEFile() {
        while(hasMoreTypes()) {
            getNextType();
        }
    }
    
}
