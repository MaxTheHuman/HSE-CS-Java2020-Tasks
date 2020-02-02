package ru.hse.cs.java2020.task01;

public class TopNList {

    private static String[] paths;
    private static long[] sizes;
    private static int itemsInList;

    public TopNList(int n) {
        itemsInList = n;
        paths = new String[itemsInList];
        sizes = new long[itemsInList];
    }

    public static void insertNewItem(int position, String newItemPath, long newItemSize) {
        int i;
        for (i = itemsInList - 1; i > position; i--) {
            sizes[i] = sizes[i - 1];
            paths[i] = paths[i - 1];
        }
        sizes[position] = newItemSize;
        paths[position] = newItemPath;
    }

    public static void updateTopList(String newItemPath, long newItemSize) {

        int i;
        for (i = 0; i < itemsInList; i++) {
            if (newItemSize > sizes[i]) {
                insertNewItem(i, newItemPath, newItemSize);
                break;
            }
        }
    }

    public static void printList() {
        System.out.println("\n--------- Biggest files ----------");
        if (paths[0] != null) {
            for (int i = 0; i < itemsInList; i++) {
                if (paths[i] != null) {
                    System.out.printf("%2d. ", i + 1);

                    String fileSizeFormat = String.format("%%%dd Kb | ", Main.getLongWidth(sizes[0]));
                    System.out.format(fileSizeFormat, sizes[i]);

                    System.out.printf("%s\n", paths[i]);
                } else {
                    break;
                }
            }
        } else {
            System.out.println("directory is empty");
        }

    }
}

