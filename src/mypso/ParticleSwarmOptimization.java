package mypso;

import java.math.BigInteger;

import static java.lang.StrictMath.round;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;
import static mypso.Allocator.*;
import static mypso.Allocator.countAllRepetitions;
import static mypso.PrimeNumberTest.*;
import static mypso.Bees.*;

public class ParticleSwarmOptimization {

    public static void main(String[] args) throws InterruptedException {
        /* Входные данные */
        /* Число для разложения на простые множители */
        Number = new BigInteger(new BigInteger("14215571194073").multiply(new BigInteger("1")).toString());
//        Number = new BigInteger("921533319151");
        System.out.println("Проверяемое число: "+Number);

        /* Число пчел */
        explorers = 10000;
        fields = 100; // < explorers
        workers = 100;
        certainty = 20;

        /* Начало отсчета времени */
        long time = System.currentTimeMillis();

        /* Объявим необходимые переменные и массивы */
        allocate();
        allocateArrays();
        /* Является ли квадратом */
        isSqrtN();
        /* Проверка на делимость первых простых чисел до 2*rN и деление */
        primaryTestN();
        if (flag.get()) {
            allocate();
        }

        /* Пока число составное раскладываем */
        /* Вероятность ошибки isProbablePrime = 1/2^certainty */
        while (!Number.isProbablePrime(certainty)){
             /* Выпустим пчел-разведчиков */
            allocateBees();
            threadNum.set(0);
            for (int i = 0; i < explorers; i++) {
                exploreBees[i].start();
            }
            for (int i = 0; i < explorers; i++) {
                exploreBees[i].join();
            }
            /* Посчитаем окрестонсть найденных чисел и заполним массив для проверки */
            for (int i = 0; i < fields; i++){
                long r = round(numbersExplored.first().getExploredNumber().bitLength()/BOOK_CONST);
                for (long j = -r; j <= r; j++) {
                    if(j == 0) continue;;
                    BigInteger toAdd = numbersExplored.first().getExploredNumber().add(valueOf(j));
                    numbersToCheck.add(toAdd);
                }
                numbersExplored.remove(numbersExplored.first());
            }
//            System.out.println("numToCheck "+numbersToCheck.size());
//            System.out.println(numbersToCheck.toString());
            /* Выпустим рабочих пчел */
            threadNum.set(0);
            for (int i = 0; i < workers; i++) {
                workBees[i].start();
            }
            for (int i = 0; i < workers; i++) {
                workBees[i].join();
            }

            /* Если состояние изменилось переобъявляем переменные */
            if (flag.get()) {
                countChecked += checkedForLastDivisor.size();
                allocate();
            }
            if(countRepetitions.get() != 0){
                countAllRepetitions.addAndGet(countRepetitions.get());
//                System.out.println("repetitions "+countRepetitions.get());
                countRepetitions.set(0);
            }
        }

        if (Number.compareTo(ONE) > 0){
            divisors.add(Number);
        }
        for (int i = 0; i < isSqr; i++) {
            divisors.addAll(divisors);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Делители:");
        System.out.println(divisors.toString());
        System.out.println("Сколько отрезков перебрали: "+ countChecked);
        System.out.println("Время выполнения: "+ time/1000 + " s " + time%1000 +" ms ");
        System.out.println("Число повторных обращений "+countAllRepetitions);
    }
}
