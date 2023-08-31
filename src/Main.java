import java.io.IOException;
import java.net.*;

public class Main {
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
                    System.out.println("Timeout");
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

        System.out.println("Recieved: " + buffer[0] + ", " + buffer[1]);

        try (DatagramSocket socketNew = new DatagramSocket(null)) {
            SocketAddress address = packet.getSocketAddress();
            byte[] message = { 1 };

            DatagramPacket response = new DatagramPacket(message, message.length, address);
            socketNew.send(response);
        }

        System.out.println("Sent acknowledgement");
    }
}