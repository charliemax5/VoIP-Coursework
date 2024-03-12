/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.*;


public class VOIPSocket3Sender{

    static DatagramSocket3 sending_socket;

    public static void main (String[] args) throws Exception{

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;

        AudioRecorder recorder = new AudioRecorder();

        //Recording time in seconds
        int recordTime = 10;
        try {
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        try{
            sending_socket = new DatagramSocket3();
        } catch (SocketException e){
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        System.out.println("Recording audio...");
        int key = 1073948859; //key should be between 2^30 and 2^31 to cover all bits
        short authenticationKey = 10; //authentication key must be a short so receiver can remove it from packet
        float packetNumber = 0;

        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            try{

                byte[] buffer = recorder.getBlock();
                ByteBuffer unwrapEncrypt = ByteBuffer.allocate(520);

                //PACKET NUMBERING
                packetNumber = (packetNumber + 1);
                unwrapEncrypt.putFloat(packetNumber);
                //END PACKET NUMBERING

                unwrapEncrypt.putShort(authenticationKey);
                ByteBuffer plainText = ByteBuffer.wrap(buffer);
                for (int j = 0; j < buffer.length/4; j++)
                {
                    int fourByte = plainText.getInt();
                    fourByte = fourByte ^ key;
                    unwrapEncrypt.putInt(fourByte);
                }
                byte[] encryptedBlock = unwrapEncrypt.array();

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(encryptedBlock, encryptedBlock.length, clientIP, PORT);

                //Send it
                sending_socket.send(packet);

            } catch (IOException e){
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        System.out.println("Packets sent");
        recorder.close();
        //***************************************************
    }
}
