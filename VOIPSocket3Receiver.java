/**
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


public class VOIPSocket3Receiver {

    static DatagramSocket receiving_socket;

    //I couldn't get the .sort method to work so we have this terrible selection sort algorithm instead

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
        ByteBuffer catcher = ByteBuffer.allocate(520);
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

                    ByteBuffer cipherText = ByteBuffer.wrap(audio);
                    cipherText.position(4); //go past packet number to authentication key

                    if (cipherText.getShort() == 10) {
                        catcher = cipherText;
                        orderedPackets.add(cipherText);
                    }
                } catch (SocketTimeoutException e) {
                    if (orderedPackets.size() > 0)
                    {
                        orderedPackets.add(catcher);
                    }
                    else //don't fill the array with white noise, mainly mitigates problems at the start
                    {
                        System.out.println(".");
                    }
                } catch (IOException e) {
                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                    e.printStackTrace();
                }
            }

            {

            }
            orderedPackets = selectionSort(orderedPackets); //sort by packet number
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
            orderedPackets.clear(); //must clear arraylist at the end so previous packets don't leak into the next load of 10
        }
        //Close the socket
        receiving_socket.close();
        player.close();
        //***************************************************
    }
}
