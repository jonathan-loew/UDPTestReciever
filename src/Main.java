import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Main {
    public static int[] currentValues = new int[11];
    public static Random random = new Random();

    public static void main(String[] args) {
        run("192.168.240.221", 365);
    }

    public static void run(String hostname, int port) {
        try(DatagramSocket socket = new DatagramSocket(null)) {
            SocketAddress address = new InetSocketAddress(hostname, port);
            socket.bind(address);
            socket.setSoTimeout(5000);

            while(true){
                try{
                    getMessage(socket);
                }catch(SocketTimeoutException e) {
                    // LogClass.logger.log(Level.INFO, "Timeout has occurred on port " + this.port);
                    printCurrent();
                }
            }
        } catch (IOException e) {
            System.err.println("Weird Error");
            e.printStackTrace();
        }
    }

    /**
     * This method receives a datagram packet from a socket and place it into a byte array. After that it will start
     * another thread to compute the received packet.
     * @param socket                The {@link DatagramSocket} at which it should receive {@link DatagramPacket}s.
     * @throws IOException          If an I/O error occurs.
     */
    private static void getMessage(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[2];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        System.out.println("\t\t\t Recieved: " + buffer[0] + ", " + buffer[1]);
        currentValues[buffer[0]] = buffer[1];
        printCurrent();

        try (DatagramSocket socketNew = new DatagramSocket(null)) {
            SocketAddress address = packet.getSocketAddress();
            byte[] message = { 0 };
            if(random.nextFloat() < 0.1f) //Error Simulation
                message[0] = 2;

            DatagramPacket response = new DatagramPacket(message, message.length, address);
            socketNew.send(response);
        }

        System.out.println("Sent acknowledgement");
    }

    public static void printCurrent() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for(int i : currentValues) {
            stringBuilder.append(i);
            stringBuilder.append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        stringBuilder.append("]");
        System.out.println(stringBuilder.toString());
    }
}