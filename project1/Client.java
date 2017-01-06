

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
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
        DatagramSocket clientSocket = new DatagramSocket();
        final InetAddress IPAddress = InetAddress.getByName("localhost");
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

        String sentence = "C";

        loopstart:
        do {
            // sets the chance that the packet will be dropped
            sentflag = 0;
            choice = r.nextInt(4);
            if (choice == 3) {
              chance = 0.75;
            } else if (choice == 2) {
              chance = 0.5;
            } else if (choice == 2) {
              chance = 0.25;
            } else {
              chance = 0;
            }
            if (chance == 0) {
              sentflag = 1; // packet will be sent
            } else {
              if (r.nextDouble() >= chance) {
                sentflag = 1; // packet will be sent
              }
            }
            synbit = 1;
            synno = r.nextInt(100); // just for demonstration
            seqno = synno;
            synno_string = String.format("%04d", synno);
            ackno_string = String.format("%04d", ackno);
            synbit_string = String.format("%01d", synbit);
            ackbit_string = String.format("%01d", ackbit);
            finbit_string = String.format("%01d", finbit);
            winsize_string = String.format("%04d", winsize);
            sentence = "C" + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
            System.out.println("INITIAL DATA");
            System.out.println("> SYN NO: " + synno);
            System.out.println("> ACK NO: " + ackno);
            System.out.println("> SYN BIT: " + synbit);
            System.out.println("> ACK BIT: " + ackbit);
            System.out.println("> FIN BIT: " + finbit);
            System.out.println("> WINSIZE: " + winsize);
            System.out.println("> SIGNAL: C");
            //System.out.println(sentence);
            System.out.println();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portnum);
            if (sentflag == 1) {
              System.out.println("CLIENT SENT A REQUEST TO THE SERVER.");
              Thread.sleep(1000);
              clientSocket.send(sendPacket);
              Thread.sleep(1000);
            } else {
              Thread.sleep(2000);
              System.out.println("NETWORK TIMEOUT. RESENDING...\n");
              Thread.sleep(2000);
              continue;
            }

            System.out.println();
            // RECEIVING
            DatagramPacket receivePacket;
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("RECEIVED REQUEST FROM SERVER");
            System.out.println();
            synno = Integer.parseInt(modifiedSentence.substring(1,5));
            ackno = Integer.parseInt(modifiedSentence.substring(5,9));
            synbit = Integer.parseInt(modifiedSentence.substring(9,10));
            ackbit = Integer.parseInt(modifiedSentence.substring(10,11));
            finbit = Integer.parseInt(modifiedSentence.substring(11,12));
            winsize = Integer.parseInt(modifiedSentence.substring(12,16));
            data = modifiedSentence.substring(0,1);

            System.out.println("DATA FROM SERVER");
            System.out.println("> SYN NO: " + synno);
            System.out.println("> ACK NO: " + ackno);
            System.out.println("> SYN BIT: " + synbit);
            System.out.println("> ACK BIT: " + ackbit);
            System.out.println("> FIN BIT: " + finbit);
            System.out.println("> WINSIZE: " + winsize);
            System.out.println("> SIGNAL: " + data);
            System.out.println();

            synbit = 0;
            ackbit = 1;
            int temp = synno;
            synno = ackno;
            ackno = temp + 1;
            synno_string = String.format("%04d", synno);
            ackno_string = String.format("%04d", ackno);
            synbit_string = String.format("%01d", synbit);
            ackbit_string = String.format("%01d", ackbit);
            finbit_string = String.format("%01d", finbit);
            winsize_string = String.format("%04d", winsize);
            sentence = data + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portnum);
            System.out.println("SENDING A REQUEST BACK TO THE SERVER");
            System.out.println();
            clientSocket.send(sendPacket);
            synno = 0;
            seqno = 0;
    		ackno = 0;

    		synbit = 0;
    		ackbit = 0;		
    		finbit = 0;
            stop = r.nextInt(20);
            //stop = 1;
            winsize = r.nextInt(101);
            if (stop < 3) {
                sentence = "D";
            }

        } while (sentence != "D");

        Thread.sleep(1000);
        finbit = 1;
        int origfinbit = 1;
        synno = r.nextInt(100);
        synno_string = String.format("%04d", synno);
        ackno_string = String.format("%04d", ackno);
        synbit_string = String.format("%01d", synbit);
        ackbit_string = String.format("%01d", ackbit);
        finbit_string = String.format("%01d", finbit);
        winsize_string = String.format("%04d", winsize);
        sentence = "D" + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
        System.out.println("SENDING SIGNAL FOR DISCONNECTION...");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: D");
        System.out.println();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portnum);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket;
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        synno = Integer.parseInt(modifiedSentence.substring(1,5));
        ackno = Integer.parseInt(modifiedSentence.substring(5,9));
        synbit = Integer.parseInt(modifiedSentence.substring(9,10));
        ackbit = Integer.parseInt(modifiedSentence.substring(10,11));
        finbit = Integer.parseInt(modifiedSentence.substring(11,12));
        winsize = Integer.parseInt(modifiedSentence.substring(12,16));
        data = modifiedSentence.substring(0,1);

        System.out.println("RECEIVED DISCONNECTON SIGNAL FROM SERVER");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: " + data);
        System.out.println();

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        modifiedSentence = new String(receivePacket.getData());
        synno = Integer.parseInt(modifiedSentence.substring(1,5));
        ackno = Integer.parseInt(modifiedSentence.substring(5,9));
        synbit = Integer.parseInt(modifiedSentence.substring(9,10));
        ackbit = Integer.parseInt(modifiedSentence.substring(10,11));
        finbit = Integer.parseInt(modifiedSentence.substring(11,12));
        winsize = Integer.parseInt(modifiedSentence.substring(12,16));
        data = modifiedSentence.substring(0,1);

        System.out.println("SERVER READY TO CLOSE CONNECTION");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: " + data);
        System.out.println();

        //int temp = finbit;
        ackno = synno + 1;
        //finbit = origfinbit;
        synno_string = String.format("%04d", synno);
        ackno_string = String.format("%04d", ackno);
        synbit_string = String.format("%01d", synbit);
        ackbit_string = String.format("%01d", ackbit);
        finbit_string = String.format("%01d", finbit);
        winsize_string = String.format("%04d", winsize);
        sentence = "D" + synno_string + ackno_string + synbit_string + ackbit_string + finbit_string + winsize_string;
        System.out.println("CLIENT READY TO CLOSE");
        System.out.println("> SYN NO: " + synno);
        System.out.println("> ACK NO: " + ackno);
        System.out.println("> SYN BIT: " + synbit);
        System.out.println("> ACK BIT: " + ackbit);
        System.out.println("> FIN BIT: " + finbit);
        System.out.println("> WINSIZE: " + winsize);
        System.out.println("> SIGNAL: D");
        System.out.println();
        sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portnum);
        clientSocket.send(sendPacket);

        Thread.sleep(10000);
        System.out.println("CONNECTION CLOSED.");
    }
}
