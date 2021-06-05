import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WiFiPasswordGetter {
    /**
     * wifiPasswordExporter takes the HashMap which contains all SSID-Password pairs,
     * as well a String for the file name which is taken from the text box in our GUI
     * */
    public static void wifiPasswordExporter(HashMap<String,String> wifiPasswords, String fileName) {
        System.out.println("| INFO  | SSID-Password pairs found: " + wifiPasswords.size());
        try {

            Path path = Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
            System.out.println("| INFO  | A file has been created in " + path.toString());

            FileWriter writer = new FileWriter(path + "\\" + fileName + ".txt");
            for (Map.Entry<String,String> wifiPassword : wifiPasswords.entrySet()) {
                writer.write(wifiPassword.getKey() + " : " + wifiPassword.getValue() + "\n");
            }
            System.out.println("| INFO  | File has been populated with data.");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The passwordGetter takes a String as an argument. This String is the SSID and the
     * String that we're returning is the password of the given SSID.
     * */
    public static String passwordGetter(String s) {
        Process q;
        try {
            q = Runtime.getRuntime().exec("cmd /c netsh wlan show profiles \"" + s + "\" key=clear");
            BufferedReader reader = new BufferedReader(new InputStreamReader(q.getInputStream()));

            String line;
            String password = null;
            Pattern pattern = Pattern.compile("(?<=    Key Content            : ).*");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    password = matcher.group();
                }
            }
            return password;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
