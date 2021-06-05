import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WIFiPasswordGetterGUI {
    private JPanel mainPanel;
    private JButton getDataButton;
    private JTextField txtField;
    private JTextArea doneLabel;

    public WIFiPasswordGetterGUI() {
        getDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doneLabel.setText("");
                doneLabel.update(doneLabel.getGraphics());
                doneLabel.setText("| START | Process has started.");
                doneLabel.update(doneLabel.getGraphics());

                System.out.println("| START | Process has started.");
                Process p;
                try {
                    p = Runtime.getRuntime().exec("cmd /c netsh wlan show profiles");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    List<String> wifiUnprocessed = new ArrayList<>();
                    List<String> wifiProcessed = new ArrayList<>();

                    HashMap<String, String> wifiPassword = new HashMap<>();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        wifiUnprocessed.add(line); // Adding each record into our ArrayList<String>
                    }

                    Pattern pattern = Pattern.compile("(?<=    All User Profile     : ).*");

                    System.out.println("| INFO  | Processing passwords. This may take a while...");
                    doneLabel.setText(doneLabel.getText() + "\n| INFO  | Processing passwords. \n          This may take a while...");
                    doneLabel.update(doneLabel.getGraphics());
                    for (String w : wifiUnprocessed) {
                        Matcher matcher = pattern.matcher(w);
                        while (matcher.find()) {
                            w = matcher.group();
                            wifiProcessed.add(w);
                            wifiPassword.put(w, WiFiPasswordGetter.passwordGetter(w));
                        }
                    }

                    doneLabel.setText(doneLabel.getText() + "\n| INFO  | SSID-Password pairs found: " + wifiPassword.size());
                    doneLabel.update(doneLabel.getGraphics());

                    WiFiPasswordGetter.wifiPasswordExporter(wifiPassword, txtField.getText());
                    doneLabel.setText(doneLabel.getText() + "\n| INFO  | '" + txtField.getText() + ".txt' \n          created on your Desktop");
                    doneLabel.update(doneLabel.getGraphics());
                    doneLabel.setText(doneLabel.getText() + "\n| END   | Process finished.");
                    doneLabel.update(doneLabel.getGraphics());
                    txtField.setText("");

                    System.out.println("| END   | Process finished.");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Wi-Fi Password Exporter");
        frame.setContentPane(new WIFiPasswordGetterGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(600,350);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
