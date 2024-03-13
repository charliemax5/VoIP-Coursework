/*
 * VOIPSocket1Receiver.java
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

        try{
            receiving_socket = new DatagramSocket(PORT);
        } catch (SocketException e){
            System.err.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;

        while (running){

            try{
                //Receive a DatagramPacket
                byte[] buffer = new byte[514];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                receiving_socket.setSoTimeout(33);

                receiving_socket.receive(packet);

                //get byte block from buffer
                byte[] audio = buffer;
                ByteBuffer unwrapDecrypt = ByteBuffer.allocate(buffer.length);

                ByteBuffer cipherText = ByteBuffer.wrap(audio);
                if (cipherText.getShort() == 10) //authentication key check
                {
                    int key = 1073948859;
                    for (int j = 0; j < audio.length/4; j++)
                    {
                        int fourByte = cipherText.getInt();
                        fourByte = fourByte ^ key;
                        unwrapDecrypt.putInt(fourByte);
                    }
                    byte[] decryptedBlock = unwrapDecrypt.array();

                    //play it
                    System.out.println("Playing received audio");
                    player.playBlock(decryptedBlock);
                }
            }
            catch (SocketTimeoutException e)
            {
                System.out.println(".");
            }
            catch (IOException e){
                System.err.println("ERROR: IO Error occured.");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        player.close();
        //***************************************************
    }
}
