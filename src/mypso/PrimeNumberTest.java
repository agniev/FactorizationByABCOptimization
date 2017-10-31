package mypso;

import java.math.BigInteger;
import java.util.ListIterator;

import static java.math.BigInteger.*;
import static mypso.Allocator.*;

public class PrimeNumberTest {

    public static void primaryTestN (){
        ListIterator i = firstPrimeNumbers.listIterator();
        BigInteger del = (BigInteger) i.next();
        while (i.hasNext()){
            if (Number.mod(del).equals(ZERO) && Number.divide(del).compareTo(ONE) != 0) {
                synchronized (divisors) {
                    divisors.add(del);
                }
                flag.lazySet(true);
                Number = Number.divide(del);
                primaryTestN();
            }
            del = (BigInteger) i.next();
        }
    }

    public static boolean primaryTest (BigInteger yi, int r){
        boolean result = false; //по умолчанию число yi простое
        ListIterator i = firstPrimeNumbers.listIterator();
        BigInteger del = (BigInteger) i.next();
        while (del.compareTo(valueOf(2*r)) <= 0 && result == false){
            if (yi.mod(del).equals(ZERO)) {
                result = true;
                break;
            }
            if (i.hasNext() == false) {
                throw new RuntimeException("out of bounds firstPrimeNumbers");
            }
            del = (BigInteger) i.next();
        }
        return result;
    }

    public static boolean matrixSandaram (final BigInteger yi){
        BigInteger halfY = yi.subtract(ONE).divide(TWO);  //(Y-1)/2;
        BigInteger y = ONE;
        boolean result = false; //по умолчанию число yi простое
        while (TWO.multiply(y.multiply(y.add(ONE))).compareTo(halfY) <= 0
                && result == false && !flag.get()) {
            if (halfY.subtract(y).mod(TWO.multiply(y).add(ONE)).equals(ZERO)){
                result = true;
            }
            y = y.add(ONE);
        }
        return result;
    }

    public static boolean primeNumberTest (BigInteger yi, int r) {
        if (primaryTest(yi, r) == true || matrixSandaram(yi) == true) {
            return true; //составное
        }else{
            return false;//простое
        }
    }

    public static boolean primeProbableTest (BigInteger yi, int r, int certainty) {
        if (primaryTest(yi, r) == true || yi.isProbablePrime(certainty) != true) {
            return true; //составное
        }else{
            return false;//простое
        }
    }

    public static void isSqrtN (){
        if(sqrtN.multiply(sqrtN).compareTo(Number) == 0){
            isSqr += 1;
            Number = sqrtN;
            flag.lazySet(true);
            isSqrtN();
        }
    }
}
