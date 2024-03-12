/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioPlayer;


public class VOIPSocket1Receiver {

    static DatagramSocket receiving_socket;

    public static void main (String[] args) throws Exception{

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        AudioPlayer player = new AudioPlayer();

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try{
            receiving_socket = new DatagramSocket(PORT);
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;
//        int packetCount = 0; counting for graphs, ignore

        while (running){

            try{
                //Receive a DatagramPacket
                int interdep = 3; //Interleaving depth
                int interSqu = interdep * interdep; //Size of the interleaved square variable (4)

                byte[] buffer = new byte[520];
                byte[][] interleavProccesing = new byte[interSqu][];
                byte[] audioP;
                byte[] audioQ;
                byte[][] interleavOrdered;


                for(int k = 0; k < interSqu; k++) {
                    buffer = new byte[520];

                    DatagramPacket packet = new DatagramPacket(buffer, 0, 520);
                    receiving_socket.setSoTimeout(100);

                    receiving_socket.receive(packet);

                    //get byte block from buffer
                    System.out.println((int) ByteBuffer.wrap(buffer).getFloat());
                    interleavProccesing[k] = buffer;
                }
                System.out.println("Exit loop");

                for (int x = 0; x < interSqu; x++){
                    System.out.println("Check");
                    System.out.println((int) ByteBuffer.wrap(interleavProccesing[x]).getFloat());
                }
                interleavOrdered = interleaving_VoIP.unpackedArrays(interdep,interleavProccesing);



                for(int q = 0; q < interSqu; q++) {

                    audioQ = interleavOrdered[q];
                    ByteBuffer unwrapDecrypt = ByteBuffer.allocate(buffer.length);

                    ByteBuffer cipherText = ByteBuffer.wrap(audioQ);

                    //PACKET NUMBERING
                    float packetNumber = cipherText.getFloat();
                    //System.out.println(packetNumber);
                    //END PACKET NUMBERING

                    if (cipherText.getShort() == 10) {
                        int key = 1073948859;
                        for (int j = 0; j < (audioQ.length / 4) - 6; j++) //-6 so increased packet length from packet numbering doesn't interfere
                        {
                            int fourByte = cipherText.getInt();
                            fourByte = fourByte ^ key;
                            unwrapDecrypt.putInt(fourByte);
                        }
                        byte[] decryptedBlock = unwrapDecrypt.array();

                        //play it
                        System.out.println("Playing received audio");
                        player.playBlock(decryptedBlock);
//                    packetCount = packetCount + 1;
//                    System.out.println(packetCount); //packet count should be 313
                    }
                }
            }
            catch (SocketTimeoutException e)
            {
                System.out.println(".");
            }
            catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        player.close();
        //***************************************************
    }
}