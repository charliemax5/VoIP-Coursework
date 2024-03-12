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
import java.util.ArrayList;

import CMPC3M06.AudioPlayer;


public class TextReceiver {

    static DatagramSocket receiving_socket;

    public static ArrayList<ByteBuffer> selectionSort(ArrayList<ByteBuffer> list)
    {
        for (int h = 0; h < list.size(); h++)
        {
            for (int k = h + 1; k < list.size(); k++)
            {
                if (list.get(k).getFloat() < list.get(h).getFloat())
                {
                    ByteBuffer temp = list.get(h);
                    list.set(h, list.get(k));
                    list.set(k, temp);
                }
                list.get(k).position(0);
                list.get(h).position(0);
            }
        }
        return list;
    }

    public static void main (String[] args) throws Exception{

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        AudioPlayer player = new AudioPlayer();

        //***************************************************
        //Open a socket to receive from on port PORT

//        DatagramSocket receiving_socket;
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
        ArrayList<ByteBuffer> orderedPackets = new ArrayList<>();
//        int packetCount = 0; //counting for graphs, ignore
        while (running) {
            while (orderedPackets.size() < 10) {

                try {
                    //Receive a DatagramPacket
                    byte[] buffer = new byte[520];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 520);
                    receiving_socket.setSoTimeout(35);

                    receiving_socket.receive(packet);

                    //get byte block from buffer
                    byte[] audio = buffer;
                    ByteBuffer unwrapDecrypt = ByteBuffer.allocate(buffer.length);

                    ByteBuffer cipherText = ByteBuffer.wrap(audio);
                    cipherText.position(4);

                    //PACKET NUMBERING
//                    float packetNumber = cipherText.getFloat();
//                    System.out.println(packetNumber);
                    //END PACKET NUMBERING

                    if (cipherText.getShort() == 10) {
                        orderedPackets.add(cipherText);
//                        int key = 1073948859;
//                        for (int j = 0; j < (audio.length / 4) - 6; j++) //-6 so increased packet length from packet numbering doesn't interfere
//                        {
//                            int fourByte = cipherText.getInt();
//                            fourByte = fourByte ^ key;
//                            unwrapDecrypt.putInt(fourByte);
//                        }
//                        byte[] decryptedBlock = unwrapDecrypt.array();

                        //play it
//                                            System.out.println("Playing received audio");
//                                            player.playBlock(decryptedBlock);
//                                            packetCount = packetCount + 1;
//                                            System.out.println(packetCount); //packet count should be 313
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println(".");
                } catch (IOException e) {
                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                    e.printStackTrace();
                }
            }

            {

            }
            orderedPackets = selectionSort(orderedPackets);
            for (int i = 0; i < orderedPackets.size(); i++)
            {
                ByteBuffer unwrapDecrypt = ByteBuffer.allocate(520);
                System.out.println("Playing received audio");
                int key = 1073948859;
                orderedPackets.get(i).position(6);
                for (int j = 0; j < (520/4) - 6; j++) //-6 so increased packet length from packet numbering doesn't interfere
                    {
                        int fourByte = orderedPackets.get(i).getInt();
                        fourByte = fourByte ^ key;
                        unwrapDecrypt.putInt(fourByte);
                    }
                byte[] decryptedBlock = unwrapDecrypt.array();
                System.out.println("Playing received audio");
                player.playBlock(decryptedBlock);
            }
            orderedPackets.clear();
        }
        //Close the socket
        receiving_socket.close();
        player.close();
        //***************************************************
    }
}