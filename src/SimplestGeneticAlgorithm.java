import java.util.*;

/**
 * This class implements an algorithm based on genetic-algroithms.
 * Generations mutate randomly and follow no concrete pattern.
 * Comparing the generation with the "fittest" results and their respective random mutation it determines the best
 * strategy for the given number of training cycles.
 * This algorithm resembles the most simple and unperformant solution and serves to compare performance and results
 * to more clever approaches.
 */
public class SimplestGeneticAlgorithm {

    private static HospitalSimulation sim = new HospitalSimulation(3, "");
    private static int TRAINING_CYCLES = 10;
    private static int bestFitness = Integer.MIN_VALUE;
    private static List<Integer> bestGeneration = null;
    private static List<Integer> newGeneration;
    private static int correctionSteps = 0;
    private static int foundAt = 0;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < TRAINING_CYCLES; i++) {
            train(i);

            if (i == TRAINING_CYCLES - 1 || true) {
                printInfo(newGeneration, bestGeneration, bestFitness, i + 1);
            }
        }
        System.out.printf("Best Generation found after %d Iterations%nTotal time: %d milliseconds%n",
                foundAt, System.currentTimeMillis() - start);

    }

    private static void train(int i) {
        sim = new HospitalSimulation(3, "");
        if (bestGeneration == null) {
            newGeneration = getRandomStrategy(sim.numberOfPeriods);
        } else {
            newGeneration = randomMutate(bestGeneration);
        }
        sim.runGeneration(newGeneration);
        if (sim.getFitness() > bestFitness) {
            correctionSteps++;
            System.out.printf("Found better generation: %d%n", correctionSteps);
            bestFitness = sim.getFitness();
            bestGeneration = newGeneration;
            foundAt = i + 1;
        }
    }

    private static List<Integer> getRandomStrategy(int generationLength) {
        List<Integer> randomGeneration = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < generationLength; i++) {
            randomGeneration.add(sim.actions.get(rand.nextInt(sim.actions.size())));
        }
        return randomGeneration;
    }

    private static List<Integer> randomMutate(List<Integer> oldGeneration) {
        List<Integer> mutatedGeneration = new ArrayList<>(oldGeneration);
        Random rand = new Random();
        int randIndex = rand.nextInt(mutatedGeneration.size());
        List<Integer> newPossibilities = new ArrayList<>(sim.actions);
        newPossibilities.remove(mutatedGeneration.get(randIndex));
        mutatedGeneration.set(randIndex, newPossibilities.get(rand.nextInt(newPossibilities.size())));
        return mutatedGeneration;
    }


    private static void printInfo(List<Integer> newGeneration, List<Integer> bestGeneration, int bestFitness, int index) {
        StringJoiner sj_new = new StringJoiner("-");
        StringJoiner sj_best = new StringJoiner("-");
        for (Integer x : newGeneration) {
            sj_new.add(x.toString());
        }
        for (Integer x : bestGeneration) {
            sj_best.add(x.toString());
        }
        System.out.printf("Iteration: %d,\tnew: %s,\tbest: %s%n", index, sj_new.toString(), sj_best.toString());
        System.out.printf("Fitness:\t\tnew: %d,\tbest: %d%n===%n", sim.getFitness(), bestFitness);
    }
}
