
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author: RuM
 */
public class round_robin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {

        String output = "";
        File outputF = new File("outputRR.txt");
        PrintWriter out = new PrintWriter(outputF);
        for (int i = 1; i <= 25; i++) { //testing different Q_T

            MyProcess.setQ_T(i);
            //==============Var==================================
            int counter = 0; //this counter will be used to manage the displaying of the gantt chart  
            String gantt = ""; //this string will store the basic informations about the gantt chart
            int finished = 0; //this counter will be used to keep track of how many processes finished execution
            int clock = 0; //this integer represents the clock for the system, 1 will be added after 1 unit of time is passed
            //===================================================
            //MyProcess[] processes = getInfoFromUser(); //this array will store all the processes 
            MyProcess[] processes = readFromFile(); //this array will store all the processes 
            ArrayDeque<MyProcess> readyQ = new ArrayDeque<>(); //this queue will represent the ready queue in the scheduling algorithm
            ArrayList<MyProcess> temp = new ArrayList<>(); //this is a temporary list to help moving the process to the ready queue
            temp.addAll(Arrays.asList(processes)); //storing all the processes in the temporary list
            Collections.sort(temp); //sorting the processes in the temporary list by there arrival time, 
            //if the arrival time is the same the ID will be used to sort them
            execution(finished, counter, clock, processes, readyQ, temp, gantt); //it will execution all processes
            output += String.format("%2d, %.2f, %.2f\n", i, MyProcess.getAvgT_A_T(), MyProcess.getAvgW_T());
            MyProcess.reaset();
        }

        out.print(output);
        out.close();

    }

    private static void execution(int finished, int counter, int clock, MyProcess[] processes,
            ArrayDeque<MyProcess> readyQ, ArrayList<MyProcess> temp, String gantt) {
        while (finished < processes.length) { //this is the main loop, it will keep execution until all processes finish there time.

            while (!temp.isEmpty() && temp.get(0).getA_T() <= clock) { //this loop will iterate on the temporary list and adds
                readyQ.add(temp.get(0));                               //to readyQ any process that has arrived according to the system clock
                temp.remove(temp.get(0)); // after adding any process to the readyQ we remove it from the list so we don't add it again  
            }

            if (readyQ.isEmpty()) { // if we reach this point and the readyQ is empty then we can't do any thing know
                clock++;            // just increment the system clock by 1
                continue;           // and go to the next iteration
            }

            MyProcess p = readyQ.pop(); // if we reach this point this means there is at least 1 process ready so we take it to start the execution
            // s we save the clock before starting the process use it in the gantt chart
            // t send the process to the cpu, sendToCPU() will return the time that the process has spend in the cpu
            int s = clock, t = p.sendToCPU();
            clock += t;                 // so we add that time to the system clock.

            gantt += p.getID() + " " + s + " " + clock + " "; //adding a new entry to the gantt chart in the form: "ID startTime endTime "

            counter++; //incrementing the counter that is used later to show the Gantt chart in a formatted way
            if (p.getR_T() == 0) {      // if the remaining time for the process is 0
                p.setC_T(clock);            // set it's complation time to the current clock
                p.calculatProcessInfo();    // then calculate it's T.A.T and W.T
                finished++;                 // increment the fininshed counter
            } else {                    // if the current process did not finish its execution
                while (!temp.isEmpty() && temp.get(0).getA_T() <= clock) { // first, find if any other process has arrived
                    readyQ.add(temp.get(0)); // then add it to readyQ
                    temp.remove(temp.get(0));// and remove it from the list
                }
                readyQ.add(p); // second, add the current process back to the ready          
            }
        }
        printTable(processes, clock, gantt, counter); // print the table showing the information related to each process
    }

    /**
     * this method interact with the user to get the needed information about
     * the processes
     *
     * @return an array of object that contains all the processes
     */
    private static MyProcess[] getInfoFromUser() {

        Scanner in = new Scanner(System.in);

        System.out.print("-->Enter the number of processes: ");
        int n = in.nextInt();// get number of processes
        while (n <= 0) { // make sure the number is valid
            System.out.print("-->Number of processes must be bigger than 0.\nTry again: ");
            n = in.nextInt();
        }
        MyProcess.setN(n); // set the number of processes to n
        MyProcess[] p = new MyProcess[n]; // create an array of size n

        System.out.print("-->Enter the quantum time: ");
        int q = in.nextInt(); // get the quantum time
        while (n <= 0) { // make sure the time is valid
            System.out.print("-->Quantum time must be bigger than 0.\nTry again:");
            q = in.nextInt();
        }

        MyProcess.setQ_T(q); // set the quantum time to q

        for (int i = 1; i <= n; i++) { // get the information related to each process

            System.out.print("-->Enter the arrival time for process number (" + (i) + "):");
            int at = in.nextInt(); // get the arrival time
            while (at < 0) { // make sure the time is valid
                System.out.print("-->Arival time must be bigger than or equal to 0.\nTry again:");
                at = in.nextInt();
            }

            System.out.print("-->Enter the burst time for process number (" + (i) + "):");
            int bt = in.nextInt(); // get the burst time
            while (bt <= 0) { // make sure the time is valid
                System.out.print("-->Burst time must be bigger than 0.\n-->Try again:");
                bt = in.nextInt();
            }

            p[i - 1] = new MyProcess(i, at, bt); // create a new process with entered data and store it in the array

        }

        return p; // return the array that has all the process

    }

    /**
     * this method prints the final result in table format
     *
     * @param p
     * @param clock
     * @param gantt
     * @param counter
     */
    private static void printTable(MyProcess[] p, int clock, String gantt, int counter) {

        System.out.println("|---------+-----+-----+-----+-------+-----|"
                + "\n| Process | A.T | B.T | C.T | T.A.T | W.T |"// print the header of the table
                + "\n|---------+-----+-----+-----+-------+-----|");

        for (MyProcess p1 : p) { // print the information for every process
            System.out.println(p1 + "\n|---------+-----+-----+-----+-------+-----|");
        }

        System.out.printf("-->Average T.A.T: %.2f\n", MyProcess.getAvgT_A_T()); // print the avg T.A.T
        System.out.printf("-->Average W.T: %.2f\n", MyProcess.getAvgW_T()); // print the avg W.T
        System.out.printf("-->Throughput: %.2f\n", (float) p.length / clock); // // print the avg Throughput

        //---------------------------------------------------------------------------------------------------
        Scanner input = new Scanner(gantt); // make a String as an input this String contain all info about all process
        int[][] format = new int[counter][3]; //created 2D array first D will be the size of how many process enter the ready queuu
        //and secound D will contatin process ID start and finish time
        for (int i = 0; i < format.length; i++) {
            for (int j = 0; j < format[i].length; j++) {
                format[i][j] = input.nextInt(); // this loop will fill the 2D array by the info store in the Gantt String var 
            }
        }
        System.out.println("-->Displaying Gantt Chart" + "\n");
        String str = "";
        int temp = String.format("|  %-4s ", ("P" + format[0][0])).length(); //this temp will be used to know what is the length of spaces
        String symbol = "";
        for (int i = 0; i < format.length; i++) {
            str += String.format("|  %-4s ", ("P" + (format[i][0] + 1))); // this command will print aa formatted String for a gantt chart
            if (i < format.length - 1 && format[i][2] != format[i + 1][1]) {
                str += String.format("|  %-4s ", ("idle")); //this if statment check if secound process start time is not equal 
                //then the cpu is ideal between the start and finish time of each process 
                //if that the case it will print the idle state 
            }
        }
        str += "|"; // add the final | to the String

        for (int i = 0; i < str.length(); i++) {
            symbol += "-"; //create a line of symbols based on the length of gantt chart
        }
        System.out.println(symbol + "\n" + str + "\n" + symbol);
        String spaces = "";
        for (int i = 0; i < temp - 1; i++) {
            spaces += " "; //spacess based on the length taken by each process format 
        }
        for (int i = 0; i < format.length; i++) {
            System.out.print(format[i][1]
                    + spaces.substring(0, spaces.length() - (format[i][1] + "").length() + 1)); //thiss will print the start time if each proces
            // if the length if the number is greater then one the length of spacess will be reduced
            if (i < format.length - 1 && format[i][2] != format[i + 1][1]) {
                System.out.print(format[i][2]
                        + spaces.substring(0, spaces.length() - (format[i][1] + "").length() + 1)); //if start time of next process not the same as the end process
                // then cpu is idle for suure same print as before
            }
        }

        System.out.println(format[counter - 1][2]); // print last end time

    }

    /**
     * this method read the process info from a file named "processes input.txt"
     * and returns them in an array
     *
     * @return
     * @throws FileNotFoundException
     */
    private static MyProcess[] readFromFile() throws FileNotFoundException {
        File f = new File("processes input.txt");
        Scanner in = new Scanner(f);
        int n = in.nextInt();
        MyProcess.setN(n);
        MyProcess[] p = new MyProcess[n];

        for (int i = 0; i < n; i++) {
            int AT = in.nextInt();
            int BT = in.nextInt();
            p[i] = new MyProcess(i, AT, BT);
        }

        return p;
    }
}
