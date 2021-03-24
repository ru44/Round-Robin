
import java.util.ArrayList;

public class MyProcess implements Comparable<MyProcess> {

    private static int Q_T; // the quantum time for the round-robin algorithm
    private static int N; // the number of processes
    private static ArrayList<MyProcess> allProcess = new ArrayList<>(); // this list is used to keep track of all the instances of this class

    // getters and setters
    public static int getN() {
        return N;
    }

    public static void setN(int N) {
        MyProcess.N = N;
    }

    public static int getQ_T() {
        return Q_T;
    }

    public static void setQ_T(int aQ_T) {
        Q_T = aQ_T;
    }

    /**
     * this method calculate the average T.A.T for all the processes
     *
     * @return avg T.A.T
     */
    public static double getAvgT_A_T() {
        double sum = 0;
        for (MyProcess p : allProcess) {
            sum += p.getT_A_T();
        }
        return sum / allProcess.size();
    }

    /**
     * this method calculate the average W.T for all the processes
     *
     * @return avg W.T
     */
    public static double getAvgW_T() {
        double sum = 0;
        for (MyProcess p : allProcess) {
            sum += p.getW_T();
        }
        return sum / allProcess.size();
    }

    static void reaset() {
//        Q_T = 0;
//        N = 0;
        allProcess = new ArrayList<>();
    }

    private int ID; // ID of the process
    private int B_T; // burst time
    private int R_T; // remaining time
    private int A_T; // arrival tim
    private int C_T; // complation time
    private int T_A_T; // turn around time
    private int W_T; // wait time

    /**
     * create a new process with the giving parameters then adds the newly
     * created process to local list
     *
     * @param ID
     * @param A_T
     * @param B_T
     */
    public MyProcess(int ID, int A_T, int B_T) {
        this.ID = ID;
        this.A_T = A_T;

        this.B_T = B_T;
        this.R_T = B_T;
        allProcess.add(this);
    }

    /**
     * this method simulate sending a process to the CPU
     *
     * @return the time spent by the process in the CPU
     */
    public int sendToCPU() {
        if (R_T >= Q_T) {
            R_T = R_T - Q_T;
            return Q_T;
        } else {
            int t = R_T;
            R_T = 0;
            return t;
        }
    }

    /**
     * this method is called ones the process finishes its execution to
     * calculate its T.A.T and W.T
     */
    public void calculatProcessInfo() {
        T_A_T = C_T - A_T;
        W_T = T_A_T - B_T;
    }

    /**
     * get the information related to the process and format them to be printed
     * in the table
     *
     * @return a string with all the information related to the process
     */
    @Override
    public String toString() {
        return String.format("|  P(%2d)  | %03d | %03d | %03d |  %03d  | %03d |", (getID()+1), getA_T(), getB_T(), getC_T(), getT_A_T(), getW_T());
    }

    /**
     * this method compares the processes based on there arrival time and ID and
     * its used to sort them
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(MyProcess o) {
        if (this.A_T < o.A_T) {
            return -1;
        } else if (this.A_T == o.A_T && this.ID < o.ID) {
            return -1;
        } else {
            return 1;
        }
    }

    // getters and setters
    public int getID() {
        return ID;
    }

    public int getB_T() {
        return B_T;
    }

    public void setB_T(int B_T) {
        this.B_T = B_T;
    }

    public int getR_T() {
        return R_T;
    }

    public int getA_T() {
        return A_T;
    }

    public int getC_T() {
        return C_T;
    }

    public void setC_T(int C_T) {
        this.C_T = C_T;
    }

    public int getT_A_T() {
        return T_A_T;
    }

    public void setT_A_T(int T_A_T) {
        this.T_A_T = T_A_T;
    }

    public int getW_T() {
        return W_T;
    }

    public void setW_T(int W_T) {
        this.W_T = W_T;
    }

}
