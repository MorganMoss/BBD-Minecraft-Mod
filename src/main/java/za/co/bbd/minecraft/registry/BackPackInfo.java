package za.co.bbd.minecraft.registry;

public class BackPackInfo {
    private final String name;
    private final int rowWidth;
    private final int numberOfRows;
    private final boolean isFireImmune;

    public BackPackInfo(String name, int rowWidth, int numberOfRows, boolean isFireImmune) {
        this.name = name;
        this.rowWidth = rowWidth;
        this.numberOfRows = numberOfRows;
        this.isFireImmune = isFireImmune;
    }

    public String getName() { return name; }

    public int getRowWidth() { return rowWidth; }

    public int getNumberOfRows() { return numberOfRows; }

    public boolean getIsFireImmune() { return isFireImmune; }


    public static BackPackInfo of(String name, int rowWidth, int numberOfRows, boolean isFireImmune) {
        return new BackPackInfo(name, rowWidth, numberOfRows, isFireImmune);
    }

}
