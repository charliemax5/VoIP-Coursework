/*
 * TextReceiverSocket2.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioPlayer;


public class TextReceiverSocket4 {

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

        byte[] repetitionBuffer = new byte[514];//dummy array for receiver based compensation

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
                if (cipherText.getShort() == 10)
                {
                    int key = 1073948859;
                    for (int j = 0; j < audio.length/4; j++)
                    {
                        int fourByte = cipherText.getInt();
                        fourByte = fourByte ^ key;
                        unwrapDecrypt.putInt(fourByte);
                    }
                    byte[] decryptedBlock = unwrapDecrypt.array();
                    //muffle corrupted audio
                    for (int i = 0; i < decryptedBlock.length; i++)
                    {
                        if (decryptedBlock[i] > 25 || decryptedBlock[i] < -25) //25 seems to be a sweet spot whereby it catches most of the corrupted clicking without losing too much of someone's real voice
                        {
//                            System.out.println(i+" is corrupt");
                            if (i != 0 ) {
                                decryptedBlock[i] = decryptedBlock[i - 1];
                            }
                            else {
                                if (i != 512)
                                decryptedBlock[i] = decryptedBlock[i + 1];
                                else
                                decryptedBlock[i] = 0;
                            }
                        }
                    }

                    //play it
                    System.out.println("Playing received audio");
                    player.playBlock(decryptedBlock);
                    repetitionBuffer = decryptedBlock;
                }
            }
            catch (SocketTimeoutException e)
            {
                player.playBlock(repetitionBuffer);//on timeout play previously received packet
//                System.out.println(".");
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
