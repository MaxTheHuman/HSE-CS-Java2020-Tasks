package ru.hse.cs.java2020.task01;

import java.io.File;


public class Main {

    private static TopNList topList = new TopNList(10);

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("directory path is not defined");
            return;
        }
        final File directory = new File(args[0]);
        if (directory == null) {
            System.err.println("directory path is incorrect");
            return;
        }
        listDirsInDir(directory);
    }

    static int getIntWidth(int n) {
        int width = 0;
        while (n > 0) {
            n = n / 10;
            width += 1;
        }
        return width;
    }

    static int getLongWidth(long n) {
        int width = 0;
        while (n > 0) {
            n = n / 10;
            width += 1;
        }
        return width;
    }

    /* getDirectoryInfo
     * counts files in directory, total size
     * maintain top 10 biggest files
     */
    static void getDirectoryInfo(final File directory, long[] sizesOfDirectories, int[] filesNumberInDirectories, int index) {
        File[] filesAndDirs = directory.listFiles();
        if (filesAndDirs != null) {
            for (final File currFileOrDir: filesAndDirs) {
                if (currFileOrDir != null) {
                    filesNumberInDirectories[index] += 1;
                    sizesOfDirectories[index] += currFileOrDir.length();
                    if (currFileOrDir.isDirectory()) {
                        getDirectoryInfo(currFileOrDir, sizesOfDirectories, filesNumberInDirectories, index);
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
        if (files != null) {
            for (final File currFile : files) {
                if (currFile != null) {
                    totalSize += currFile.length();
                    topList.updateTopList(currFile.getPath(), currFile.length());
                }
            }
        }
        File[] directories = directory.listFiles(File::isDirectory);
        long[] sizesOfDirectories = new long[directories.length];
        int[] filesNumberInDirectories = new int[directories.length];

        if (directories != null) {
            for (int i = 0; i < directories.length; i++) {
                currDir = directories[i];
                if (currDir != null) {
                    getDirectoryInfo(currDir, sizesOfDirectories, filesNumberInDirectories, i);
                    totalSize += sizesOfDirectories[i];
                    if (sizesOfDirectories[i] > maxDirectorySize) {
                        maxDirectorySize = sizesOfDirectories[i];
                    }
                    if (filesNumberInDirectories[i] > maxItemsNumber) {
                        maxItemsNumber = filesNumberInDirectories[i];
                    }
                    if (currDir.getName().length() > maxDirectoryNameWidth) {
                        maxDirectoryNameWidth = currDir.getName().length();
                    }
                }
            }
            System.out.println("---------- Disk Usage ----------");
            System.out.println("total directory size: " + totalSize + " Kb\n");
            for (int i = 0; i < directories.length; i++) {
                currDir = directories[i];
                if (currDir != null) {

                    String dirNumberFormat = String.format("%%%dd. ", getIntWidth(directories.length));
                    System.out.format(dirNumberFormat, i + 1);

                    String dirNameFormat = String.format("%%%ds | ", maxDirectoryNameWidth);
                    System.out.format(dirNameFormat, currDir.getName());

                    String dirSizeFormat = String.format("%%%dd Kb | ", getLongWidth(maxDirectorySize));
                    System.out.format(dirSizeFormat, sizesOfDirectories[i]);

                    System.out.printf("%8.4f%% | ", (sizesOfDirectories[i] * 100.0 / totalSize));

                    String dirItemsNumberFormat = String.format("%%%dd items\n", getLongWidth(maxDirectorySize));
                    System.out.format(dirItemsNumberFormat, sizesOfDirectories[i]);

//                    String result_string = ((i + 1) + ". " + currDir.getName() + "\t\t|\t\t" +
//                                            sizesOfDirectories[i] + " Kb\t\t|\t\t" +
//                                            sizesOfDirectories[i] * 100.0 / totalSize + " %\t\t|\t" +
//                                            filesNumberInDirectories[i] + " items");
                    // System.out.printf("%s\n", result_string);
//                    System.out.printf("%2s. %30s | %15s Kb | %7.3f %% | %7d items\n",
//                            Integer.toString(i + 1), currDir.getName(), Long.toString(sizesOfDirectories[i]),
//                            ((sizesOfDirectories[i] * 100.0 / totalSize)), filesNumberInDirectories[i]);
                } else {
                    System.err.println("subdirectory is corrupted");  // maybe remove
                }
            }

            topList.printList();

        } else {
            System.err.println("data in directory is corrupted");
        }
    }
}
