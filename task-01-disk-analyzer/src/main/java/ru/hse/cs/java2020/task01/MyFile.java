package ru.hse.cs.java2020.task01;

public class MyFile implements Comparable<MyFile> {

    private final String name;
    private long size;
    private int itemsNumber;

    public MyFile(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public int getItemsNumber() {
        return itemsNumber;
    }

    public void incrementSizeOnAmount(long amount) {
        size += amount;
    }

    public void incrementItemsNumber() {
        itemsNumber += 1;
    }

    @Override
    public int compareTo(MyFile otherFile) {
        return (int) (otherFile.size - this.size);
    }
}
