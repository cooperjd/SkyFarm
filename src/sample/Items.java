package sample;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Items implements Serializable{
    private String name;
    private double price;
    private int xCoord;
    private int yCoord;
    private int lengthSpan;
    private int widthSpan;
    //For color
    private double red;
    private double green;
    private double blue;

    private HashMap<String, Items> children = new HashMap<>();

    public Items(String name,
                 Double price,
                 Integer xCoord,
                 Integer yCoord,
                 Integer lengthSpan,
                 Integer widthSpan, javafx.scene.paint.Color color)
    {
        this.name = name;
        this.price = price;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.lengthSpan = lengthSpan;
        this.widthSpan = widthSpan;
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
    }

    protected abstract boolean isItemContainer();

    public HashMap<String,Items> getChildren(){
        if(!isItemContainer()) return null;
        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public int getLengthSpan() {
        return lengthSpan;
    }

    public void setLengthSpan(int lengthSpan) {
        this.lengthSpan = lengthSpan;
    }

    public int getWidthSpan() {
        return widthSpan;
    }

    public void setWidthSpan(int widthSpan) {
        this.widthSpan = widthSpan;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public Color getColor(){
        return Color.color(red,green,blue);
    }

    public String getColorAsString(){
        return "[red=" + red + ", green=" + green + ", blue=" + blue + "]";
    }

    public void setChildren(String[] names) {
        if(isItemContainer()){
            for(String name : names){
                children.put(name, Main.itemMap.get(name));
            }
        }
    }

    public void removeChild(String name){
        children.remove(name);
    }
}

