import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HospitalSimulation {

    public int numberOfPeriods;
    public String state;

    private int doctorSalary = 75;
    private int doctorEarningsLowest = 120;
    private int doctorEarningsHighest = 125;
    private int doctorCount;
    private int balance;

    public List<Integer> actions = Arrays.asList(0, 1, 2);

    public HospitalSimulation() {
        new HospitalSimulation(4, "");
    }

    public HospitalSimulation(int numberOfPeriods, String state) {
        this.state = state;
        this.numberOfPeriods = numberOfPeriods;
        this.doctorCount = 5;
        this.balance = 1000;
    }

    public void runGeneration(List<Integer> generation) {
        for (Integer action : generation) {
            runPeriod(action);
        }
    }

    public int runPeriod(Integer action) {
        state += action;
        switch (action) {
            case 0:
                //do nothing
                break;
            case 1:
                hireDoctor();
                break;
            case 2:
                dismissDoctor();
                break;
            default:
                //do nothing
        }
        Random r = new Random();
        int old_balance = balance;
        balance = balance
                - doctorCount * doctorSalary
                + doctorCount * (r.nextInt(doctorEarningsHighest - doctorEarningsLowest) + doctorEarningsLowest);
        //return reward as difference in balance values (only used for QLearning)
        return balance - old_balance;
    }

    public int getFitness() {
        return balance;
    }

    private void hireDoctor() {
        doctorCount++;
    }

    private void dismissDoctor() {
        if (doctorCount > 0) {
            doctorCount--;
        } else {
            //no doctor left to be dismissed. do nothing.
        }
    }

}
