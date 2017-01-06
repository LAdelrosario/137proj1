

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main (String args[]) throws Exception {
        int synno = 0;
        int seqno = 0;
		int ackno = 0;

		int synbit = 0;
		int ackbit = 0;
		int finbit = 0;
		int winsize = 9;
		String data;

        int counter = 1;
        int choice = 0;

        int sentflag = 0;
        double chance = 0;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket serverSocket = new DatagramSocket(8789);
        DatagramPacket receivePacket;
        //final InetAddress IPAddress = InetAddress.getByName("localhost");
        final int portnum = 8789;
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        Random r = new Random();
        int stop = 0;

        String synno_string;
        String ackno_string;
        String synbit_string;
        String ackbit_string;
        String finbit_string;
        String winsize_string;
        // sentence format:
        // CONNECT___XXXXXXXXXXXXXXX
        // CONNECT___ can be replaced by DISCONNECT
        // X represents integers from 1 to 9
        // four digits each for syn no, ack no, and window size
        // one digit each for syn bit, ack bit, fin bit

        String sentence;
        // start of the running server
        loopstart:
        while(true) {
            synno = r.nextInt(100); // just for demonstration
            seqno = synno;
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            sentence = new String(receivePacket.getData());
            //System.out.println("RECEIVED REQUEST FROM CLIENT");
            System.out.println();
            synno = Integer.parseInt(sentence.substring(1,5));
            ackno = Integer.parseInt(sentence.substring(5,9));
            synbit = Integer.parseInt(sentence.substring(9,10));
            ackbit = Integer.parseInt(sentence.substring(10,11));
            finbit = Integer.parseInt(sentence.substring(11,12));
            winsize = Integer.parseInt(sentence.substring(12,16));
            data = sentence.substring(0,1);
            //System.out.println(data);
            if (data.equals("D")) {
                break loopstart;
            }
            //continues here if there is no disconnection signal
            System.out.println("DATA FROM CLIENT");
            System.out.println("> SYN NO: " + synno);
            System.out.println("> ACK NO: " + ackno);
            System.out.println("> SYN BIT: " + synbit);
            System.out.println("> ACK BIT: " + ackbit);
            System.out.println("> FIN BIT: " + finbit);
            System.out.println("> WINSIZE: " + winsize);
            System.out.println("> SIGNAL: " + data);
            System.out.println();
            ackno = synno + 1;
            synno = seqno;
            ackbit = 1;
            synbit = 1;
            synno_string = String.format("%04d", synno);
            ackno_string = String.format("%04d", ackno);
            synbit_string = String.format("%01d", synbit);
            ackbit_string = String.format("%01d", ackbit);
            finbit_string = String.format("%01d", finbit);
            winsize_string = String.format("%04d", winsize);
            sentence = data + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            System.out.println("SENDING A REQUEST BACK TO THE CLIENT");
            System.out.println();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);


            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence2 = new String(receivePacket.getData());
            synno = Integer.parseInt(sentence2.substring(1,5));
            ackno = Integer.parseInt(sentence2.substring(5,9));
            synbit = Integer.parseInt(sentence2.substring(9,10));
            ackbit = Integer.parseInt(sentence2.substring(10,11));
            finbit = Integer.parseInt(sentence2.substring(11,12));
            winsize = Integer.parseInt(sentence2.substring(12,16));
            data = sentence2.substring(0,1);
            //System.out.println(sentence2);
            System.out.println("CONFIRMED CONNECTION FROM CLIENT");
            System.out.println("> SYN NO: " + synno);
            System.out.println("> ACK NO: " + ackno);
            System.out.println("> SYN BIT: " + synbit);
            System.out.println("> ACK BIT: " + ackbit);
            System.out.println("> FIN BIT: " + finbit);
            System.out.println("> WINSIZE: " + winsize);
            System.out.println("> SIGNAL: " + data);
            System.out.println();
            System.out.println("CONNECTION ESTABLISHED.");
            System.out.println();

        }
        // closing the connection
        closing:
        System.out.println("RECEIVED DISCONNECTION SIGNAL FROM CLIENT.");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: " + data);
        System.out.println();
        int origsynno = r.nextInt(100);
        ackno = synno + 1;
        synno = 0;
        finbit = 0;
        synno_string = String.format("%04d", synno);
        ackno_string = String.format("%04d", ackno);
        synbit_string = String.format("%01d", synbit);
        ackbit_string = String.format("%01d", ackbit);
        finbit_string = String.format("%01d", finbit);
        winsize_string = String.format("%04d", winsize);
        sentence = data + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        System.out.println("SENDING DISCONNECTION SIGNAL TO THE CLIENT");
        System.out.println();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);

        finbit = 1;
        synno = origsynno;
        //Thread.sleep(3000);
        //finbit = origfinbit;
        synno_string = String.format("%04d", synno);
        ackno_string = String.format("%04d", ackno);
        synbit_string = String.format("%01d", synbit);
        ackbit_string = String.format("%01d", ackbit);
        finbit_string = String.format("%01d", finbit);
        winsize_string = String.format("%04d", winsize);
        sentence = data + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
        IPAddress = receivePacket.getAddress();
        port = receivePacket.getPort();
        System.out.println("SERVER READY TO CLOSE");
        System.out.println();
        sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        sentence = new String(receivePacket.getData());
        //System.out.println("RECEIVED REQUEST FROM CLIENT");
        System.out.println();
        synno = Integer.parseInt(sentence.substring(1,5));
        ackno = Integer.parseInt(sentence.substring(5,9));
        synbit = Integer.parseInt(sentence.substring(9,10));
        ackbit = Integer.parseInt(sentence.substring(10,11));
        finbit = Integer.parseInt(sentence.substring(11,12));
        winsize = Integer.parseInt(sentence.substring(12,16));
        data = sentence.substring(0,1);

        System.out.println("FINAL DATA FROM CLIENT");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: " + data);
        System.out.println();

        Thread.sleep(1000);

    }
}
