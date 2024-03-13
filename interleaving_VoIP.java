import java.nio.ByteBuffer;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class interleaving_VoIP {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("Interleaved");
        //sortedArray = unpackedArrays(interDepth, interleavedArray);


    }

    public static byte[][] interleavingArrays4(byte[][] preparedArray){
        //"InterleavedDepth" sets the size of the interleaved "block"
        int interleavedDepth = 4;
        //The squared size is also important for many processes
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;


        int packetNumber;
        //Initializes storage for modulo and division operattions
        int positionModulo;
        int positionDivision;

        //Creates the array block where data will be stored
        byte[][] interleavedArray = new byte[interleavedDepthSquare][];
        for (int i = 0; i < interleavedDepthSquare; i++) {

            //Casts the packet number to int and uses a modulo operation on it to make it fit the array size.
            packetNumber = (int) ByteBuffer.wrap(preparedArray[i]).getFloat() % interleavedDepthSquare;
            //System.out.println(packetNumber);

            positionModulo = packetNumber % interleavedDepth;
            positionDivision = packetNumber / interleavedDepth;

            switch(positionModulo){
                //"Rotates" the array based on the information given in
                //The position something should appear in the cube can be found through this process
                //Find the modulo value to find which "row" it should appear on
                //Then find its division for the coulom
                case 0:
                    interleavedArray[positionDivision] = preparedArray[i];
                    break;
                case 3:
                    interleavedArray[interleavedDepth+positionDivision] = preparedArray[i];
                    break;
                case 2:
                    interleavedArray[(interleavedDepth*2)+positionDivision] = preparedArray[i];
                    break;
                case 1:
                    interleavedArray[(interleavedDepth*3)+positionDivision] = preparedArray[i];
                    break;
            }
        }


        return interleavedArray;
    }


    public static byte[][] interleavingArrays3(byte[][] preparedArray){
        //"InterleavedDepth" sets the size of the interleaved "block"
        int interleavedDepth = 3;
        //The squared size is also important for many processes
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;


        int packetNumber;
        //Initializes storage for modulo and division operattions
        int positionModulo;
        int positionDivision;

        //Creates the array block where data will be stored
        byte[][] interleavedArray = new byte[interleavedDepthSquare][];
        for (int i = 0; i < interleavedDepthSquare; i++) {

            //Casts the packet number to int and uses a modulo operation on it to make it fit the array size.
            packetNumber = (int) ByteBuffer.wrap(preparedArray[i]).getFloat() % interleavedDepthSquare;
            //System.out.println(packetNumber);

            positionModulo = packetNumber % interleavedDepth;
            positionDivision = packetNumber / interleavedDepth;

            switch(positionModulo){
                //"Rotates" the array based on the information given in
                //The position something should appear in the cube can be found through this process
                //Find the modulo value to find which "row" it should appear on
                //Then find its division for the coulom
                case 0:
                    interleavedArray[positionDivision] = preparedArray[i];
                    break;
                case 2:
                    interleavedArray[(interleavedDepth*1)+positionDivision] = preparedArray[i];
                    break;
                case 1:
                    interleavedArray[(interleavedDepth*2)+positionDivision] = preparedArray[i];
                    break;
            }
        }


        return interleavedArray;
    }

    public static byte[][] interleavingArrays2(byte[][] preparedArray){
        //"InterleavedDepth" sets the size of the interleaved "block"
        int interleavedDepth = 2;
        //The squared size is also important for many processes
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;


        int packetNumber;
        //Initializes storage for modulo and division operattions
        int positionModulo;
        int positionDivision;

        //Creates the array block where data will be stored
        byte[][] interleavedArray = new byte[interleavedDepthSquare][];
        for (int i = 0; i < interleavedDepthSquare; i++) {

            //Casts the packet number to int and uses a modulo operation on it to make it fit the array size.
            packetNumber = (int) ByteBuffer.wrap(preparedArray[i]).getFloat() % interleavedDepthSquare;
            //System.out.println(packetNumber);

            positionModulo = packetNumber % interleavedDepth;
            positionDivision = packetNumber / interleavedDepth;

            switch(positionModulo){
                //"Rotates" the array based on the information given in
                //The position something should appear in the cube can be found through this process
                //Find the modulo value to find which "row" it should appear on
                //Then find its division for the coulom
                case 0:
                    interleavedArray[positionDivision] = preparedArray[i];
                    break;
                case 1:
                    interleavedArray[(interleavedDepth*1)+positionDivision] = preparedArray[i];
                    break;
            }
        }


        return interleavedArray;
    }

    public static byte[][] unpackedArrays(int interleavedDepth, byte[][] interleavedArray){
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;
        byte[][] unpackedArray = new byte[interleavedDepthSquare][];
        int unpackingNumber = 0;

        for (int i = 0; i < interleavedDepthSquare; i++) {
            unpackingNumber = ((int) ByteBuffer.wrap(interleavedArray[i]).getFloat()) % interleavedDepthSquare;
            System.out.println(unpackingNumber);
            //Retrieves number from relevant position and matches it to position in unpacking array
            //Gets the packets ordered number in
            unpackedArray[unpackingNumber] = interleavedArray[i];
            //-1 to patch the position of data (1-16), to the position in arrays (0-15)
        }
        System.out.println("Sorted");
        for (int i = 0; i < interleavedDepthSquare; i++) {
            System.out.println(unpackedArray[i]);
        }
        return unpackedArray;
    }
}
//Will look back at this once it is confirmed which number packets start at
//Also need to change if reading in continuous data vs not
//Currently only works in blocks of 16
//Want to also make it worth with a depth of 3/block on 9