package mypso;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.StrictMath.round;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static mypso.PrimeNumberTest.matrixSandaram;

public class Allocator {

    public static final BigInteger TWO = valueOf(2);
    /* Делители */
    final static double BOOK_CONST = 1.442695;

    /* Число пчел-разведчиков */
    static int explorers;
    /* Число рабочих пчел */
    static int workers;
    /* Число полей */
    static int fields;
    /* Раскладываемое на простые множители число */
    static BigInteger Number;
    /* Корень от проверяемого числа */
    static BigInteger sqrtN;
    /* Точность (для проверки на простоту)*/
    static int certainty;
    /* rN окрестность N (число бит N, деленное на BOOK_CONST = 1.442695) */
    static int rN;
    /* Окрестность корня из Number */
    static int rSqrtN;
    /* Длина отрезка для перебора */
    static int section;
    /* Устанавливаем флаг = true, когда нужно заново объявить переменные (вначале и после нахождения делителя) */
    static AtomicBoolean flag = new AtomicBoolean(false);
    /* Номер треда */
    static AtomicInteger threadNum = new AtomicInteger(0);
    /* Cколько раз извлекается корень */
    static int isSqr = 0;
    /* Сколько отрезков перебрали */
    static long countChecked = 0;
    static AtomicInteger countRepetitions = new AtomicInteger(0); //считаем повторные обращения
    static AtomicInteger countAllRepetitions = new AtomicInteger(0); //считаем все повторные обращения


    /*Массив простых чисел от 2 до 2*rN */
    static ArrayList<BigInteger> firstPrimeNumbers;
    /* Создадим TreeSet для хранения провернных чисел (x_i) */
    static  Set<BigInteger> checkedForLastDivisor;
    /* Массив пчел */
    static Thread[] workBees;
    static Thread[] exploreBees;
    /* Массив найденных разведчиками чисел */
    static SortedSet<Bees> numbersExplored;
    /* Массив всех чисел для перебора в текущей итерации */
    static SortedSet<BigInteger> numbersToCheck;
    /* Найденные делители заданного числа */
    static ArrayList<BigInteger> divisors = new ArrayList<>();

    /* Объявляется один раз */
    static void allocateArrays(){
        exploreBees = new Thread[explorers];
        workBees = new Thread[workers];
        primeNumberListAllocation();
    }

    /* Переобъявление вначале и далее после нахождения каждого делителя */
    static void allocate(){
        numbersExplored = new TreeSet<>();
        numbersToCheck = new TreeSet<>();
        checkedForLastDivisor = new TreeSet<>();
        for (int i=1; i<= 2*rN; i++) {
            checkedForLastDivisor.add(valueOf(i));
        }
        flag.lazySet(false);
        sqrtN = isqrt(Number);
        rN = (int) round(Number.bitLength()/BOOK_CONST);
        rSqrtN = (int) round(sqrtN.bitLength()/BOOK_CONST);
        section = rSqrtN*2+1;
    }

    /*Создадет массив простых чисел от 2 до 2*rN */
    public static void primeNumberListAllocation(){
        firstPrimeNumbers = new ArrayList<>();
        firstPrimeNumbers.add(TWO);
        firstPrimeNumbers.add(valueOf(3));
        BigInteger primeNumber = valueOf(5);
        while (primeNumber.compareTo(valueOf(2*rN)) <= 0) {
            while (matrixSandaram(primeNumber) == true){
                primeNumber = primeNumber.add(TWO);
            }
            firstPrimeNumbers.add(primeNumber);
            primeNumber = primeNumber.add(TWO);
        }
    }

    /* isqrt is from Cornell Universiry Library https://arxiv.org/src/0908.3030v2/anc */
    static public BigInteger isqrt(final BigInteger n) {
        if ( n.compareTo(ZERO) < 0 )
            throw new ArithmeticException("Negative argument "+ n.toString()) ;
                /* Start with an estimate from a floating point reduction.
                */
        BigInteger x  ;
        final int bl = n.bitLength() ;
        if ( bl > 120)
            x = n.shiftRight(bl/2-1) ;
        else
        {
            final double resul= Math.sqrt(n.doubleValue()) ;
            x = new BigInteger(""+Math.round(resul)) ;
        }

        final BigInteger two = new BigInteger("2") ;
        while ( true)
        {
                        /* check whether the result is accurate, x^2 =n
                        */
            BigInteger x2 = x.pow(2) ;
            BigInteger xplus2 = x.add(ONE).pow(2) ;
            if ( x2.compareTo(n) <= 0 && xplus2.compareTo(n) > 0)
                return x ;
            xplus2 = xplus2.subtract(x.shiftLeft(2)) ;
            if ( xplus2.compareTo(n) <= 0 && x2.compareTo(n) > 0)
                return x.subtract(ONE) ;
                        /* Newton algorithm. This correction is on the
                        * low side caused by the integer divisions. So the value required
                        * may end up by one unit too large by the bare algorithm, and this
                        * is caught above by comparing x^2, (x+-1)^2 with n.
                        */
            xplus2 = x2.subtract(n).divide(x).divide(two) ;
            x = x.subtract(xplus2) ;
        }
    }
}
