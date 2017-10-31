package mypso;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static mypso.Allocator.*;
import static mypso.PrimeNumberTest.primeProbableTest;

public class Bees implements Comparable {

    private BigInteger exploredNumber;
    private int function;

    /* Число разрядов при делении */
    static int SCALE = 6;

    Bees(BigInteger n){
        this.setExploredNumber(n);
        BigDecimal toEdit = new BigDecimal(Number).divide(new BigDecimal(n), SCALE, HALF_UP);
        int toSet = toEdit.subtract(toEdit.setScale(0, RoundingMode.HALF_DOWN)).unscaledValue().intValue();
        this.setFunction(toSet);
    }

    static void allocateBees(){
        /* Создание пчел-разведчиков */
        for (int i = 0; i < explorers; i++) {
            exploreBees[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    /* Генерация произвольного числа от 2*rN+1 до sqrtN*/
                    Bees myBee = new Bees(getRandom());
                    if (Number.mod(myBee.getExploredNumber()).equals(ZERO)) {
                        synchronized (divisors){
                            divisors.add(myBee.getExploredNumber());
                        }
                        flag.lazySet(true);
                        Number = Number.divide(myBee.getExploredNumber());
                    } else{
                        synchronized (numbersExplored){
                            numbersExplored.add(myBee);
                        }
                    }
                }
            });
        }

        /* Создание рабочих пчел */
        for (int i = 0; i < workers; i++) {
            workBees[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    /* Вылет на проверку */
                    BigInteger checkingNum;
                    while(!numbersToCheck.isEmpty() && !flag.get()) {
                        synchronized (numbersToCheck){
                            if(!numbersToCheck.isEmpty()) {
                                checkingNum = numbersToCheck.first();
                                numbersToCheck.remove(checkingNum);
                            } else {
                                break;
                            }
                        }
                        synchronized (checkedForLastDivisor) {
                            if (checkedForLastDivisor.contains(checkingNum)) {
                                countRepetitions.incrementAndGet();
//                                System.out.println("myrep "+ checkingNum);
                                continue;
                            }
                            checkedForLastDivisor.add(checkingNum);
                        }
                        if (!primeProbableTest(checkingNum, rSqrtN, certainty)){
                            if (Number.mod(checkingNum).equals(ZERO)){
                                synchronized (divisors){
                                    divisors.add(checkingNum);
                                }
                                flag.lazySet(true);
                                Number = Number.divide(checkingNum);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    /* Генерация произвольного числа от 2*rN+1 до sqrtN
    * и добавление его в множество проверенных чисел*/
    static BigInteger getRandom(){
        BigInteger randomNumber = new BigInteger(sqrtN.bitLength(), new Random()).mod(sqrtN).add(ONE);
        while (checkedForLastDivisor.contains(randomNumber)
                && valueOf(checkedForLastDivisor.size()+1).compareTo(sqrtN) < 0
                || randomNumber.compareTo(ONE) == 0) {
            randomNumber = new BigInteger(sqrtN.bitLength(), new Random()).mod(sqrtN).add(ONE);
        }
        checkedForLastDivisor.add(randomNumber);
        return randomNumber;
    }

    @Override
    public int compareTo(Object o) {
        Bees obj = (Bees) o;
        return this.getFunction() - obj.getFunction();
    }

    public BigInteger getExploredNumber() {
        return exploredNumber;
    }

    public void setExploredNumber(BigInteger exploredNumber) {
        this.exploredNumber = exploredNumber;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }
}
