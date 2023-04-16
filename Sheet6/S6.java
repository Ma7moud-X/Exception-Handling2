import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


class NotVaildAutosarFileException extends Exception {
    public NotVaildAutosarFileException(String message) {
        super(message);
    }
}

class EmptyAutosarFileException extends RuntimeException {
    public EmptyAutosarFileException(String message) {
        super(message);
    }
}

class Container {
    private String id;
    private String shortName;
    private String longName;

    public Container(String id, String shortName, String longName) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
    }
    public String getShortName(){
        return this.shortName;
    }

    public String toString() {
        return "    <CONTAINER UUID=\"" + this.id + "\">\n"
             + "	    <SHORT-NAME>" + this.shortName + "</SHORT-NAME>\n"
             + "	    <LONG-NAME>" + this.longName + "</LONG-NAME>\n"
             + "    </CONTAINER>";
    }
}

public class S6 {

    static public void main(String[] args) throws NotVaildAutosarFileException{


        if (args.length == 0 || (!args[0].endsWith(".arxml") && !args[0].endsWith(".ARXML") ) ) {
            throw new NotVaildAutosarFileException("Please provide a valid Autosar file with .arxml extension as argument.");
        }


        ArrayList<Container> List = new ArrayList<>();
        File inputFile = new File (args[0]);
        File outputFile;

        if (args[0].endsWith(".arxml")) {
            outputFile = new File(inputFile.getName().replace(".arxml", "_mod.arxml"));
        } 
        else{
            outputFile = new File(inputFile.getName().replace(".ARXML", "_mod.ARXML"));
        }

        try (Scanner scanner = new Scanner(inputFile); PrintWriter writer = new PrintWriter(outputFile)) {

            if (!scanner.hasNext()) {
                throw new EmptyAutosarFileException("The input file is empty.");
            }

            String line = scanner.nextLine();
            String first = line.trim();

            while (scanner.hasNext()) {

                line = scanner.nextLine();

                if (line.contains("<CONTAINER UUID=")) {
                    String id = line.substring(line.indexOf("UUID=\"") + 6, line.indexOf("\">"));
                    String s = scanner.nextLine().trim();
                    String shortName = s.substring(12, s.indexOf("</SHORT-NAME>"));
                    s =  scanner.nextLine().trim();
                    String longName = s.substring(11, s.indexOf("</LONG-NAME>"));
                    List.add(new Container(id, shortName, longName));
                }
            }


            Collections.sort(List, Comparator.comparing(c -> c.getShortName()));

            writer.println(first);
            writer.println("<AUTOSAR>");
            for (Container container : List) {
                writer.println(container);
            }
            writer.println("</AUTOSAR>");
            
        } 
        catch (IOException e) {
        System.err.println("Error reading input file: " + e.getMessage());
        }

    }
}
