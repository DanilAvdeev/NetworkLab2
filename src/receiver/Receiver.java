package receiver;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable{
    ServerSocket serverSocket;
    Socket socket;
    InputStream inputStream;
    String fileName;
    int fileSize;
    String fileDir = System.getProperty("user.dir") + "/uploads";
    File outFile;
    int BUF_SIZE = 1024;
    long bytesReceived = 0;

    public Receiver(int port){
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
            System.out.println("Server started.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
           while (!Thread.currentThread().isInterrupted()) {
               socket = serverSocket.accept();
               inputStream = socket.getInputStream();

               receiveInfo();
               receiveFile();
           }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void receiveFile() throws IOException{
        if (new File(fileDir).mkdirs()){
            System.out.println("/uploads was created.");
        } else {
            System.out.println("/uploads already exists.");
        }
        outFile = new File(fileDir + "/" + fileName);
        if (outFile.createNewFile()){
            System.out.println("Created file " + fileName);
        } else {
            System.out.println("Already exists. But I won't do anything with it..");
        }

        FileOutputStream outFileStream = new FileOutputStream(outFile);

        byte[] fileBuf = new byte[BUF_SIZE];
        long bytesRemain = fileSize;
        long startTime = System.currentTimeMillis();
        while (bytesRemain > 0) {
            int bytesReceivedNow = inputStream.read(fileBuf, 0,
                    bytesRemain < BUF_SIZE ? (int) bytesRemain : BUF_SIZE);
            bytesReceived += bytesReceivedNow;
            bytesRemain -= bytesReceivedNow;
            outFileStream.write(fileBuf, 0, bytesReceivedNow);
            outFileStream.flush();
            //System.out.println("Received " + bytesReceivedNow + " bytes.");
        }
        long finishTime = System.currentTimeMillis();
        long timeWasted = (finishTime - startTime) / 1000; //secs
        System.out.println("Time wasted:" + timeWasted + " sec");
        System.out.println("Avg speed: " + fileSize / timeWasted / 8/ 1024 / 1024 + " Mb/s");
        System.out.println("File " + fileName  + " received");
    }

    void receiveInfo() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());

        int fileNameLength = in.readInt();
        if (fileNameLength > 0) {
            byte[] fileNameBytes = new byte[fileNameLength];
            in.readFully(fileNameBytes, 0, fileNameBytes.length);
            fileName = new String(fileNameBytes);

            fileSize = in.readInt();
            if (fileSize > 0) {
                byte [] fileSizeBytes = new byte[fileSize];
                in.readFully(fileSizeBytes, 0, fileSize);
            } else {
                System.out.println("Couldn't download file size.");
            }
        } else {
            System.out.println("Couldn't download file name.");
        }

        System.out.println("File info received: '" + fileName + "', " + fileSize + " bytes");
    }
}
