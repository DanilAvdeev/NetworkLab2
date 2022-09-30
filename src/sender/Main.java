package sender;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
 //       String fileName = args[2]; //мб захардкодить?)
        String fileName = "C:/Users/avdan/IdeaProjects/NetworkLab2/1.txt";

        try {
            File file = new File(fileName); //сделать проверку на существование файла
            InetAddress inetAddress = InetAddress.getByName(ip);
            Thread sender = new Thread(new Sender(inetAddress, port, file));
            sender.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
