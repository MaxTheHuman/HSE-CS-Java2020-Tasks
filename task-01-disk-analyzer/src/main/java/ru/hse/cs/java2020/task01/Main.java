package ru.hse.cs.java2020.task01;

import java.io.File;
import java.util.Arrays;
import java.time.Instant;


public class Main {
    static final int TOPFILESNUMBER = 10;
    private static TopNList topList = new TopNList(TOPFILESNUMBER);

    public static void main(String[] args) {

        long startTime = Instant.now().toEpochMilli();

        if (args.length <= 0) {
            System.err.println("directory path is not defined");
            return;
        }

        final File directory = new File(args[0]);

        listDirsInDir(directory);

        long endTime = Instant.now().toEpochMilli();

        long timeElapsed = endTime - startTime;

        System.out.println("Time elapsed: " + timeElapsed + " ms");
    }

    static void printDirInfo(MyFile[] dirs, int i, long maxDirectorySize,
                                     int maxItemsNumber, int maxDirectoryNameWidth, long totalSize) {
        final double fNUMBEROFPERCENTS = 100.0;

        String dirNumberFormat = String.format("%%%dd. ", Integer.toString(dirs.length).length());
        System.out.format(dirNumberFormat, i + 1);

        String dirNameFormat = String.format("%%%ds | ", maxDirectoryNameWidth);
        System.out.format(dirNameFormat, dirs[i].getName());

        String dirSizeFormat = String.format("%%%dd Kb | ", Long.toString(maxDirectorySize).length());
        System.out.format(dirSizeFormat, dirs[i].getSize());

        System.out.printf("%8.4f%% | ", (dirs[i].getSize() * fNUMBEROFPERCENTS / totalSize));

        String dirItemsNumberFormat = String.format("%%%dd items\n", Long.toString(maxItemsNumber).length());
        System.out.format(dirItemsNumberFormat, dirs[i].getItemsNumber());
    }

    static void getDirectoryInfo(final File directory, MyFile[] directories, int index) {
        File[] filesAndDirs = directory.listFiles();
        if (filesAndDirs != null) {
            for (final File currFileOrDir: filesAndDirs) {
                if (currFileOrDir != null) {
                    directories[index].incrementItemsNumber();
                    directories[index].incrementSizeOnAmount(currFileOrDir.length());
                    if (currFileOrDir.isDirectory()) {
                        getDirectoryInfo(currFileOrDir, directories, index);
                    } else {
                        topList.updateTopList(currFileOrDir.getPath(), currFileOrDir.length());
                    }
                } else {
                    System.err.println("can't get information of file or directory");
                }
            }
        } else {
            System.err.println("can't scan directory " + directory.getPath());
        }
    }

    public static void listDirsInDir(final File directory) {

        long totalSize = 0;
        int maxDirectoryNameWidth = 0;
        long maxDirectorySize = 0;
        int maxItemsNumber = 0;
        File currDir;

        File[] files = directory.listFiles(File::isFile);

        MyFile[] myFiles = {};
        if (files != null) {
            myFiles = new MyFile[files.length];
        }

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                myFiles[i] = new MyFile(files[i].getName());
                myFiles[i].incrementSizeOnAmount(files[i].length());
                totalSize += files[i].length();

                if (myFiles[i].getSize() > maxDirectorySize) {
                    maxDirectorySize = myFiles[i].getSize();
                }
                if (myFiles[i].getName().length() > maxDirectoryNameWidth) {
                    maxDirectoryNameWidth = myFiles[i].getName().length();
                }

                topList.updateTopList(files[i].getPath(), files[i].length());
            }
        }

        File[] directories = directory.listFiles(File::isDirectory);

        if (directories != null) {

            MyFile[] dirs = new MyFile[directories.length];
            for (int i = 0; i < directories.length; i++) {
                dirs[i] = new MyFile(directories[i].getName());
            }

            for (int i = 0; i < directories.length; i++) {
                currDir = directories[i];
                if (currDir != null) {
                    getDirectoryInfo(currDir, dirs, i);
                    totalSize += dirs[i].getSize();

                    if (dirs[i].getSize() > maxDirectorySize) {
                        maxDirectorySize = dirs[i].getSize();
                    }
                    if (dirs[i].getItemsNumber() > maxItemsNumber) {
                        maxItemsNumber = dirs[i].getItemsNumber();
                    }
                    if (dirs[i].getName().length() > maxDirectoryNameWidth) {
                        maxDirectoryNameWidth = dirs[i].getName().length();
                    }
                }
            }

            MyFile[] dirAndFiles = Arrays.copyOf(myFiles, myFiles.length + dirs.length);
            System.arraycopy(dirs, 0, dirAndFiles, myFiles.length, dirs.length);
            Arrays.sort(dirAndFiles);

            System.out.println("---------- Disk Usage ----------");
            System.out.println("total directory size: " + totalSize + " Kb\n");

            for (int i = 0; i < dirAndFiles.length; i++) {
                printDirInfo(dirAndFiles, i, maxDirectorySize, maxItemsNumber, maxDirectoryNameWidth, totalSize);
            }

            topList.printList();

        } else {
            System.err.println("invalid directory");
        }
    }
}
