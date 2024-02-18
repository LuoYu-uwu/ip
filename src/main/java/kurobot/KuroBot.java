package kurobot;

import kurobot.exceptions.InvalidCommandException;
import kurobot.exceptions.InvalidDescriptionException;
import kurobot.exceptions.InvalidTimeException;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class KuroBot {

    //private static Task[] tasks = new Task[100];
    private static ArrayList<Task> tasks = new ArrayList<>();
    private static int taskNum = 0;
    private static boolean isStart;
    private static final int LINE_LEN = 60;
    private static final String LINE =  "-".repeat(LINE_LEN);
    private static Scanner scanner;

    private static final String LOGO =
            " ___   ___    ___    ___ \n"
                    + "|   |/   /   |  |   |  | \n"
                    + "|       /    |  |   |  | \n"
                    + "|   |\\   \\   |_ |___| _| \n"
                    + "|___| \\___\\    |_____|   \n";


    private static void printTasks() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (Task task : tasks){
            System.out.println(tasks.indexOf(task)+1 + "." + task.printTask());
        }
        System.out.println(LINE);
    }

    private static void start() {
        System.out.println(LINE);
        System.out.println("Hello! I'm KuroBot\n" + "What can I do for you?");
        System.out.println(LINE);
        isStart = true;
        scanner = new Scanner(System.in);
    }

    private static void printAddedTask(Task task) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(task.printTask());
        System.out.println("Now you have " + taskNum + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void addTodo(String userInput) throws InvalidDescriptionException {
        //check if description was given
        String[] words = userInput.split(" ",2);
        if (words.length < 2){
            throw new InvalidDescriptionException();
        }

        String taskName = words[1];
        Todo task = new Todo(taskName, false);
        tasks.add(task);
        taskNum++;
        printAddedTask(task);
    }

    private static void addDeadline(String userInput) throws InvalidDescriptionException, InvalidTimeException {
        //check if description was given
        String[] words = userInput.split(" ",2);
        if (words.length < 2){
            throw new InvalidDescriptionException();
        }
        String description = words[1];

        //check if due date was given
        String[] phrases = description.split("/by", 2);
        if (phrases.length < 2){
            throw new InvalidTimeException();
        }
        String taskName = phrases[0];
        String by = phrases[1].strip();

        Deadline task = new Deadline(taskName, by, false);
        tasks.add(task);
        taskNum++;
        printAddedTask(task);
    }

    private static void addEvent(String userInput) throws InvalidDescriptionException, InvalidTimeException {
        //check if description was given
        String[] words = userInput.split(" ",2);
        if (words.length < 2){
            throw new InvalidDescriptionException();
        }
        String description = words[1];

        //check if duration was given
        String[] phrases = description.split("/from",2);
        if (phrases.length < 2){
            throw new InvalidTimeException();
        }
        String taskName = phrases[0];

        //check if both "from" and "to" was given
        String[] period = phrases[1].split("/to",2);
        if(period.length < 2){
            throw new InvalidTimeException();
        }
        String from = period[0].strip();
        String to = period[1].strip();

        Event task = new Event(taskName, from, to, false);
        tasks.add(task);
        taskNum++;
        printAddedTask(task);
    }

    private static void end() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
        System.out.println(LOGO);
        isStart = false;
        scanner.close();
    }

    private static void markTask(String userInput, boolean status) throws InvalidDescriptionException {
        //check if task number was given
        String[] words = userInput.split(" ");
        if (words.length != 2){
            throw new InvalidDescriptionException();
        }

        //get task number
        String taskIndex = words[1];
        int i = Integer.parseInt(taskIndex);
        try {
            if (status) {
                tasks.get(i - 1).mark();
            } else {
                tasks.get(i - 1).unmark();
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println(LINE);
            System.out.println("there's no such task though...");
            System.out.println(LINE);
        }
    }

    private static void deleteTask(String userInput) throws InvalidDescriptionException {
        String[] words = userInput.split(" ");
        if (words.length != 2){
            throw new InvalidDescriptionException();
        }

        String taskIndex = words[1];
        int i = Integer.parseInt(taskIndex);
        try {
            taskNum--;
            System.out.println(LINE);
            System.out.println("Noted. I've removed this task:");
            System.out.println(tasks.get(i - 1).printTask());
            System.out.println("Now you have " + taskNum + " tasks in the list.");
            System.out.println(LINE);
            tasks.remove(tasks.get(i - 1));
        } catch (IndexOutOfBoundsException e){
            System.out.println(LINE);
            System.out.println("there's no such task hmmm");
            System.out.println(LINE);
        }
    }

    private static void manageTasks(String input) throws InvalidCommandException {
        //extract command keyword from input
        String[] words = input.split(" ",2);
        //first phrase given is the command keyword
        String command = words[0];
        switch (command) {
        case "bye":
            end();
            break;
        case "list":
            printTasks();
            break;
        case "mark":
            try {
                markTask(input, true);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("mhmm.. which task have you completed? >.<");
                System.out.println(LINE);
            }
            break;
        case "unmark":
            try {
                markTask(input, false);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("oopsie, what task should I unmark?");
                System.out.println(LINE);
            }
            break;
        case "todo":
            try {
                addTodo(input);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("Hmmm.. what is the task about?");
                System.out.println(LINE);
            }
            break;
        case "deadline":
            try {
                addDeadline(input);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("Heyyy~ don't forget your task");
                System.out.println(LINE);
            } catch (InvalidTimeException e) {
                System.out.println(LINE);
                System.out.println("Did you forget your due date? :p");
                System.out.println(LINE);
            }
            break;
        case "event":
            try {
                addEvent(input);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("aiyoyo, how can you forget the event XD");
                System.out.println(LINE);
            } catch (InvalidTimeException e) {
                System.out.println(LINE);
                System.out.println("uhoh! don't forget the timings!");
                System.out.println(LINE);
            }
            break;
        case "delete":
            try {
                deleteTask(input);
            } catch (InvalidDescriptionException e) {
                System.out.println(LINE);
                System.out.println("what task?");
                System.out.println(LINE);
            }
            break;
        default:
            throw new InvalidCommandException();
        }
        try {
            writeToFile();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    private static void writeToFile() throws IOException {
        FileWriter fw = new FileWriter("src/data/prevData");
        for (Task task : tasks){
            fw.write(tasks.indexOf(task)+1 + "." + task.printTask() + System.lineSeparator());
        }
        fw.close();
    }

    public static void main(String[] args) {

        //display welcome message
        start();

        try {
            readFileContents();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        printTasks();

        while (isStart) {
            String input = scanner.nextLine();
            try {
                manageTasks(input);
            } catch (InvalidCommandException e) {
                System.out.println(LINE);
                System.out.println("Whoops! Please enter a valid command~");
                System.out.println(LINE);
            }
        }
    }

    private static void readFileContents() throws FileNotFoundException {
        try {
            File file = new File("src/data/prevData");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                Scanner scan = new Scanner(file); // create a Scanner using the File as the source
                while (scan.hasNext()) {
                    String content = scan.nextLine();
                    char taskType = content.charAt(3);
                    char markStatus = content.charAt(6);
                    String taskDetails = content.substring(9);
                    switch (Character.toString(taskType)){
                    case "T":
                        addPreviousTodo(taskDetails, markStatus);
                        break;
                    case "D":
                        addPreviousDeadline(taskDetails, markStatus);
                        break;
                    case"E":
                        addPreviousEvent(taskDetails, markStatus);
                        break;
                    default:
                        System.out.println("Something went wrong!");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    private static void addPreviousTodo(String details, char mark) {
        Todo task;
        if (Character.toString(mark).equals("X")) {
            task = new Todo(details, true);
        } else {
            task = new Todo(details, false);
        }
        tasks.add(task);
        taskNum++;
    }

    private static void addPreviousDeadline(String details, char mark) {
        String[] phrases = details.split("\\(");
        String taskName = phrases[0];
        String by = phrases[1];
        String deadline = by.substring(by.indexOf(" ")+1, by.indexOf(")"));
        Deadline task;
        if (Character.toString(mark).equals("X")) {
            task = new Deadline(taskName, deadline, true);
        } else {
            task = new Deadline(taskName, deadline, false);
        }
        tasks.add(task);
        taskNum++;
        printAddedTask(task);
    }

    private static void addPreviousEvent(String details, char mark) {
        String[] phrases = details.split("\\(from: ");
        String taskName = phrases[0];
        String[] periods = phrases[1].split(" to: ");
        String from = periods[0];
        String to = periods[1].substring(0,periods[1].length()-1);
        Event task;
        if (Character.toString(mark).equals("X")) {
            task = new Event(taskName, from, to, true);
        } else {
            task = new Event(taskName, from, to, false);
        }
        tasks.add(task);
        taskNum++;
    }
}
