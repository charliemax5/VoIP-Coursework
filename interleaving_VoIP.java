// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class interleaving_VoIP {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        int interDepth = 4;
        int interleavedArray[];
        int sortedArray[];
        int prepArray[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
        int prepArray2[] = {1,2,3,4,5,6,7,8,9};
        interleavedArray = interleavingArrays(interDepth, prepArray);
        System.out.println("Interleaved");
        sortedArray = unpackedArrays(interDepth, interleavedArray);

    }

    public static int[] interleavingArrays(int interleavedDepth, int preparedArray[]){
        //"InterleavedDepth" sets the size of the interleaved "block"
        //Testing with regular ints before using datagram packets

        //The squared size is also important for many processes
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;




        //Initializes storage for modulo and division operattions
        int positionModulo = 0;
        int positionDivision = 0;

        //Creates the block where data will be stored
        int interleavedArray[] = new int[interleavedDepthSquare];
        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
        for (int i = 0; i < interleavedDepthSquare; i++) {

            positionModulo = preparedArray[i] % interleavedDepth;
            positionDivision = preparedArray[i] / interleavedDepth;

            switch(positionModulo){
                //Can't actually work with multiple numbers yet since switch case would need to be expanded
                case 0:
                    interleavedArray[-1+positionDivision] = preparedArray[i];
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
            // Press Shift+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.

        }

        for (int i = 0; i < interleavedDepthSquare; i++) {
            System.out.println(interleavedArray[i]);
        }
        return interleavedArray;
    }

    public static int[] unpackedArrays(int interleavedDepth, int interleavedArray[]){
        int interleavedDepthSquare = interleavedDepth * interleavedDepth;
        int unpackedArray[] = new int[interleavedDepthSquare];
        int unpackingNumber = 0;

        for (int i = 0; i < interleavedDepthSquare; i++) {
            unpackingNumber = interleavedArray[i];
            //Retrieves number from relevant position and matches it to position in unpacking array
            System.out.println(unpackingNumber);
            //Numerical testing
            //Gets the packets ordered number in
            unpackedArray[unpackingNumber-1] = interleavedArray[i];
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