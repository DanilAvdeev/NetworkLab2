package sender;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Sender implements Runnable{
    Socket socket;
    File file;
    BufferedOutputStream outputStream;

    public Sender(InetAddress inetAddress, int port, File file){
        this.file = file;
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(inetAddress, port));
            System.out.println("Sender started.");
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            sendInfo();
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
                System.out.println("Socket closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendFile() throws IOException {
        int BUF_SIZE = 1024;
        byte[] buf = new byte[BUF_SIZE];
        long bytesSent = 0;
        long bytesLeft = file.length();
        FileInputStream inputStream = new FileInputStream(file);
        System.out.println("Started sending.");
        while(bytesSent < file.length()){
            int bytesToSend;
            if (bytesLeft < BUF_SIZE) {
                bytesToSend = (int)bytesLeft;
            } else {
                bytesToSend = BUF_SIZE;
            }
            int bytesRead = inputStream.read(buf, 0, bytesToSend);
            outputStream.write(buf, 0, bytesRead);
            outputStream.flush();
            bytesSent += bytesRead;
            bytesLeft -= bytesRead;
            System.out.println("Sent " + bytesRead + " bytes");
        }
        inputStream.close();
        System.out.println("File sent.");
    }

    void sendInfo() throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] fileNameBytes = file.getName().getBytes();
        byte[] fileSizeBytes = new byte[(int)file.length()];

        dataOutputStream.writeInt(fileNameBytes.length);
        dataOutputStream.write(fileNameBytes);

        dataOutputStream.writeInt(fileSizeBytes.length);
        dataOutputStream.write(fileSizeBytes);

        System.out.println("Header sent");
    }
}
