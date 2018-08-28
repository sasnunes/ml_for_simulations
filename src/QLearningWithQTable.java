import java.util.List;
import java.util.Random;

public class QLearningWithQTable {

    //Hyperparameters for Q-Learning-Algorithm
    private static int TRAINING_CYCLES = 200;
    private static double EPSILON = 1.0;
    private static double EPSILON_DECAY = EPSILON / (TRAINING_CYCLES*4);
    private static double LEARNING_RATE = 0.2;
    private static double DISCOUNT_FACTOR = 0.9;

    private static int simulationPeriods = 3;
    private static HospitalSimulation sim = new HospitalSimulation(simulationPeriods, "");
    private static List<Integer> ACTIONS = sim.actions;
    private static int STATE_COUNT = calcStateCount(ACTIONS.size(), sim.numberOfPeriods);


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        System.out.printf("State count is: %d%n", STATE_COUNT);
        System.out.printf("Building initial Q-Table%n");
        double[][] qtable = buildInitialQTable(STATE_COUNT, ACTIONS.size());
        //printQTable(qtable, true);

        for (int i = 0; i < TRAINING_CYCLES; i++) {
            System.out.printf("%n===== Training-Cycle %d =====%n", i + 1);
            train(qtable);
            //printQTable(qtable, true);
        }

        // show results. when EPSILON is 0, we will always exploit, choosing the most rewarding action on each state.
        EPSILON = 0.0;
        showResults(qtable);
        printQTable(qtable, true);
        System.out.printf("Total Time: %d milliseconds%n", System.currentTimeMillis() - start);
    }

    private static void train(double[][] qtable) {
        sim = new HospitalSimulation(simulationPeriods, "");
        for (int i = 0; i < sim.numberOfPeriods; i++) {
            String currentState = sim.state;
            int chosenAction = chooseAction(qtable, sim.state);
            double reward = sim.runPeriod(chosenAction);
            String newState = sim.state;
            updateQValue(qtable, currentState, chosenAction, reward, newState);
            EPSILON -= EPSILON_DECAY;
        }
    }

    private static void updateQValue(double[][] qtable, String state, int action, double reward, String newState) {
        double currentQValue = qtable[stateToIndex(state)][action];
        HospitalSimulation tmpSim = new HospitalSimulation(2, newState);
        double futureReward = tmpSim.runPeriod(exploit(qtable, tmpSim.state));

        qtable[stateToIndex(state)][action] =
                currentQValue + LEARNING_RATE * (reward + DISCOUNT_FACTOR * futureReward - currentQValue);
    }

    private static int chooseAction(double[][] qtable, String state) {
        Random r = new Random();
        int action = r.nextDouble() >= EPSILON ? exploit(qtable, state) : explore();
        System.out.printf("State: \"%s\", chosen action: %d%n", state, action);
        return action;
    }

    private static int exploit(double[][] qtable, String state) {
        if (qtable[stateToIndex(state)][0] == 0 &&
                qtable[stateToIndex(state)][1] == 0 &&
                qtable[stateToIndex(state)][2] == 0) {
            // in case we are trying to exploit without any rewards known for this state, do explore instead.
            return explore();
        }

        // choose the action with the best reward.
        int bestAction = 0;
        double bestReward = Integer.MIN_VALUE;
        for (int i = 0; i < ACTIONS.size(); i++) {
            double reward = qtable[stateToIndex(state)][i];
            if (reward > bestReward) {
                bestReward = reward;
                bestAction = i;
            }
        }
        return bestAction;
    }

    private static int explore() {
        Random r = new Random();
        return r.nextInt(ACTIONS.size());
    }

    private static double[][] buildInitialQTable(final int numStates, final int numActions) {
        double[][] qt = new double[numStates][numActions];
        for (int i = 0; i < numStates - 1; i++) {
            for (int j = 0; j < numActions; j++) {
                qt[i][j] = 0;
            }
        }
        return qt;
    }

    private static int calcStateCount(int numActions, int periods) {
        int sum = 0;
        for (int i = 0; i <= periods; i++) {
            sum = (int) (sum + Math.pow(numActions, i));
        }
        return sum;
    }

    private static int stateToIndex(String state) {
        int stateLength = state.length();
        int actionCount = ACTIONS.size();
        if (state.length() == 0) {
            return 0;
        }
        int idx = calcStateCount(actionCount, stateLength - 1);
        int j = stateLength - 1;
        for (String x : state.split("")) {
            idx += Integer.valueOf(x) * Math.pow(actionCount, j);
            j--;
        }
        return idx;
    }

    private static void printQTable(double[][] qt, boolean cutLastRows) {
        System.out.printf(" State | Reward through Action0 | Action1 | ...%n");
        int shortenTable = cutLastRows ? ACTIONS.size() : 1;
        for (int i = 0; i < STATE_COUNT / shortenTable; i++) {
            System.out.printf("   %d   |", i);
            for (int j = 0; j < ACTIONS.size(); j++) {
                System.out.printf(" %.1f |", qt[i][j]);
            }
            System.out.println();
        }
    }

    private static void showResults(double[][] qtable) {
        System.out.printf("%n==================%n" +
                "===== RESULT =====%n" +
                "==================%n");
        System.out.printf("Most optimal gameplan should be:%n");
        train(qtable);
    }

}
