package com.reengen.utils.auditreporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// I imported File, BufferedWriter, FileOutputStream, OutputStreamWriter to write report.csv

public class Runner {

    public List<List<String>> users;
    private List<List<String>> files;
    static boolean reportCSV = false; // -c flag boolean
    static boolean topOption = false; // --top flag boolean
    static int topCount = 0; // --top flag count

    public static void main(String[] args) throws IOException {
        // Set Options
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                reportCSV = true;
            } else if (args[i].equals("--top")) {
                try {
                    topOption = true;
                    topCount = Integer.parseInt(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("Erroneous Top Option \n-> " + e);
                    System.exit(1);
                }
            }
        }
        Runner r = new Runner();
        r.loadData(args[0], args[1]);
        r.run();
    }

    public void run() throws IOException {
        // Create report directory and report.csv file
        File directory = new File("report");
        directory.mkdir();
        File report = new File("./report/report.csv");
        FileOutputStream fos = new FileOutputStream(report);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        if (topOption) {
            if (topCount < 0) {
                System.out.println("Invalid top number!");
                System.exit(1);
            } else {
                System.out.println("Top #" + topCount + " Report\n" + "=============");
                // Sort files
                sortFiles(files);
                // Print and write files.
                for (int i = 0; i < topCount; i++) {
                    String ownerUser = findUser(Integer.parseInt(files.get(i).get(3)));
                    String fileName = files.get(i).get(2);
                    String fileSize = files.get(i).get(1);
                    String printLine = "    * " + fileName + " ==> user " + ownerUser + ", " + fileSize + " bytes";
                    String writeLine = fileName + "," + ownerUser + "," + fileSize;
                    System.out.println(printLine);
                    if (reportCSV) {
                        bw.write(writeLine);
                        bw.newLine();
                    }
                }
            }
        } else {
            printHeader();
            for (List<String> userRow : users) {
                long userId = Long.parseLong(userRow.get(0));
                String userName = userRow.get(1);
                printUserHeader(userName);
                for (List<String> fileRow : files) {
                    long size = Long.parseLong(fileRow.get(1));
                    String fileName = fileRow.get(2);
                    int ownerUserId = Integer.parseInt(fileRow.get(3));
                    String ownerUserName = findUser(ownerUserId);
                    String writeLine = ownerUserName + "," + fileName + "," + size;
                    if (ownerUserId == userId) {
                        printFile(fileName, size);
                        if (reportCSV) {
                            bw.write(writeLine);
                            bw.newLine();
                        }
                    }
                }
            }
        }
        bw.close();
    }

    private void loadData(String userFn, String filesFn) throws IOException {
        String line;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(userFn));
            users = new ArrayList<List<String>>();

            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                users.add(Arrays.asList(line.split(",")));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        reader = null;
        try {
            reader = new BufferedReader(new FileReader(filesFn));
            files = new ArrayList<List<String>>();

            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                files.add(Arrays.asList(line.split(",")));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void printHeader() {
        System.out.println("User Files");
        System.out.println("==========");
    }

    private void printUserHeader(String userName) {
        System.out.println("## User: " + userName);
    }

    private void printFile(String fileName, long fileSize) {
        System.out.println("* " + fileName + " ==> " + fileSize + " bytes");
    }

    List<List<String>> sortFiles(List<List<String>> files) {
        List<String> tempRow; // This is a temp list.It is used in swap progress.
        for (int i = 0; i < files.size(); i++) {
            int max = 0;
            int indexOfMax = 0;
            for (int j = i; j < files.size(); j++) { // Start at first item.Find max value and swap with first
                                                     // item.After first swap, start with second item to search max
                                                     // value and so on.
                int fileSize = Integer.parseInt(files.get(j).get(1));
                if (fileSize > max) {
                    max = fileSize;
                    indexOfMax = j; // When find a max value keep its adress in the files list.
                }
            }
            if (Integer.parseInt(files.get(i).get(1)) < max) { // If found max value is greater than the size of i'th
                                                               // element, then swap i'th element with the one has the
                                                               // max value we found.
                tempRow = files.get(i);
                files.set(i, files.get(indexOfMax));
                files.set(indexOfMax, tempRow);
            }
        }
        files = files.subList(0, Math.min(topCount, files.size())); // Cut and update files list with top option number
        return files.subList(0, Math.min(topCount, files.size())); // I return this value to test it in JunitTest.java
    }

    private String findUser(int userId) { // Find and return username with a given number
        String userName = "no_user";
        for (List<String> userRow : users) {
            if (Integer.parseInt(userRow.get(0)) == userId)
                userName = userRow.get(1);
        }
        return userName;
    }

}
