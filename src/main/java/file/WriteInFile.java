package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteInFile {
    public WriteInFile(String fileName, String writeLog) {
        try {
            new File(fileName);//creeaza daca nu exista un fiser!
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(writeLog);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
