package sample.ChannnelLayer;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/23/16.
 */
public class CRCCoder {

    public static byte[] encode(byte[] data){
        byte[] result = new byte[data.length*2];
        byte currentByte, halfByte1, halfByte2;

        for (int i = 0; i < data.length; ++i) {
            currentByte = data[i];

            halfByte1 = (byte) ( (currentByte & 0b00001111)); //b1b1b1b10000

            byte c3 = (byte) (getBit(0, halfByte1));//<<2;
            byte c5 = (byte) (getBit(1, halfByte1));//<<4;
            byte c6 = (byte) (getBit(2, halfByte1));//<<5;
            byte c7 = (byte) (getBit(3, halfByte1));//<<6;

            byte c1 = (byte) ( c3^c5^c7 );
            byte c2 = (byte) ( c3^c6^c7 );
            byte c4 = (byte) ( c5^c6^c7);

            result[2 * i] = (byte)( (c7 << 6) | c6<<5 | c5<<4 | c4 << 3 | c3<<2 | c2<<1 | c1);

            halfByte2 = (byte) ( (currentByte & 0b11110000) >> 4); // b2b2b2b20000

            c3 = (byte) (getBit(0, halfByte2));//<<2;
            c5 = (byte) (getBit(1, halfByte2));//<<4;
            c6 = (byte) (getBit(2, halfByte2));//<<5;
            c7 = (byte) (getBit(3, halfByte2));//<<6;

            c1 = (byte) ( c3^c5^c7 );
            c2 = (byte) ( c3^c6^c7 );
            c4 = (byte) ( c5^c6^c7);


            result[2 * i + 1] = (byte)( (c7 << 6) | c6<<5 | c5<<4 | c4 << 3 | c3<<2 | c2<<1 | c1);


        }

        return result;
    }
    public static   byte[] decode(byte[] data) throws Exception {
        byte[] result = new byte[data.length / 2];
        byte currentByte, halfByte1, halfByte2, residue;

        for (int i = 0; i < data.length - 1; i += 2) {
            currentByte = 0;
            halfByte1 = data[i];
            halfByte2 = data[i + 1];

            byte h1 = (byte) (getBit(0, halfByte1) ^ getBit(2, halfByte1) ^ getBit(4, halfByte1) ^ getBit(6, halfByte1));
            byte h2 = (byte) (getBit(1, halfByte1) ^ getBit(2, halfByte1) ^ getBit(5, halfByte1) ^ getBit(6, halfByte1));
            byte h3 = (byte) (getBit(3, halfByte1) ^ getBit(4, halfByte1) ^ getBit(5, halfByte1) ^ getBit(6, halfByte1));

            h1 = (byte)(h1<<0);
            h2 = (byte)(h2<<1);
            h3 = (byte)(h3<<2);

            byte sin = (byte) (h1 | h2 | h3);

            if (sin != 0){
                throw new Exception("FRAME IS CORRUPT");
            }

            byte res = (byte) ((getBit(6, halfByte1) << 3) | (getBit(5, halfByte1) << 2)
                    | (getBit(4, halfByte1) << 1) | (getBit(2, halfByte1) << 0));

            currentByte = (byte)(currentByte | res );



            h1 = (byte) (getBit(0, halfByte2) ^ getBit(2, halfByte2) ^ getBit(4, halfByte2) ^ getBit(6, halfByte2));
            h2 = (byte) (getBit(1, halfByte2) ^ getBit(2, halfByte2) ^ getBit(5, halfByte2) ^ getBit(6, halfByte2));
            h3 = (byte) (getBit(3, halfByte2) ^ getBit(4, halfByte2) ^ getBit(5, halfByte2) ^ getBit(6, halfByte2));

            h1 = (byte)(h1<<0);
            h2 = (byte)(h2<<1);
            h3 = (byte)(h3<<2);

            sin = (byte) (h1 | h2 | h3);

            if (sin != 0){
                throw new Exception("FRAME IS CORRUPT");
            }


            res = (byte) ((getBit(6, halfByte2) << 3) | (getBit(5, halfByte2) << 2)
                    | (getBit(4, halfByte2) << 1) | (getBit(2, halfByte2) << 0));

            currentByte = (byte)(currentByte |  (res << 4) );

            result[i / 2] = currentByte;
        }

        return result;
    }

    private static byte getBit(int position, byte b)
    {
        return  (byte) ((b >> position) & 1);
    }

//    public static byte[] encode(byte[] data) {
//        byte[] result = new byte[data.length * 2];
//        byte currentByte, halfByte1, halfByte2, residue;
//
//        for (int i = 0; i < data.length; ++i) {
//            currentByte = data[i];
//
//            halfByte1 = (byte)((currentByte & 0b00001111) << 3);
//            residue = divide(halfByte1);
//            result[2 * i] = (byte)(halfByte1 | residue);
//
//            halfByte2 = (byte)((currentByte & 0b11110000) >> (4 - 3));
//            residue = divide(halfByte2);
//            result[2 * i + 1] = (byte)(halfByte2 | residue);
//        }
//
//        return result;
//    }

//    public static byte[] decode(byte[] data) throws Exception {
//        byte[] result = new byte[data.length / 2];
//        byte currentByte, halfByte1, halfByte2, residue;
//
//        for (int i = 0; i < data.length - 1; i += 2) {
//            currentByte = 0;
//            halfByte1 = data[i];
//            halfByte2 = data[i + 1];
//
//            residue = divide(halfByte1);
//            if (residue != 0){
//                throw new Exception("FRAME IS CORRUPT");
//            }
//
//            currentByte = (byte)(currentByte | (halfByte1 & 0b1111000) >> 3);
//
//            residue = divide(halfByte2);
//            if (residue != 0){
//                throw new Exception("FRAME IS CORRUPT");
//            }
//
//            currentByte = (byte)(currentByte | ((halfByte2 & 0b1111000) << 1));
//
//            result[i / 2] = currentByte;
//        }
//
//        return result;
//    }

//    private static byte getShiftNumbers(byte number, byte maxShift) {
//        byte count = 0;
//        for (byte b = 0b1000000; b != 0; b = (byte)(b >> 1)) {
//            if ((number & b) > 0)
//                return count > maxShift ? maxShift : count;
//            ++count;
//        }
//        return 0;
//    }


//    private static byte divide(byte dividend) {
//        final byte divider = 0b1011000;     // Порождающий полином X^3 + X + 1 со сдвигом << 3
//        byte maxShift = 3;                  // Максимальное количество сдвигов, чтобы не испортить порождающий полином
//        byte residue = dividend;            // Остаток от деления dividend на порождающий полином
//
//        while((residue) > 0b111) {
//            residue = (byte)(residue ^ (divider >> getShiftNumbers(residue, maxShift)));
//        }
//        return residue;
//    }

    public static void main(String[] args) {
        byte data[] = "Hello World!\n\tTabTab".getBytes();
        byte[] encoded = encode(data);
        System.out.println("Encoded");
        System.out.println(new String(encoded));

        byte[] decoded = new byte[0];

        try {
            decoded = decode(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Decoded");
        System.out.println(new String(decoded));
    }
}
