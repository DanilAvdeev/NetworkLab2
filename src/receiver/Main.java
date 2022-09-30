package receiver;

public class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Thread receiver = new Thread(new Receiver(port));
        receiver.start();
    }
}
