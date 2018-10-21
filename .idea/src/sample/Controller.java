package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    private TreeView<String> tree;

    @FXML
    private TextField priceBox;

    @FXML
    private TextField nameBox;

    @FXML
    private TextField xCoord;

    @FXML
    private TextField lenBox;

    @FXML
    private TextField yCoord;

    @FXML
    private TextField widthBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button removeButton;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button newButton;

    @FXML
    private CheckBox containerCheck;

    @FXML
    private GridPane farmGrid;

    private static final String DEFAULT_NAME = "SkyFarm";
    private static final String FILENAME = "skyfarm.list";
    private static final String ITEM_CONTAINER_CODE = "ic";
    private static final String ITEM_CODE = "i";
    private static final String SEPARATOR = "|";
    private static final String SEPARATOR_REGEX = "\\|";
    private static final String COMMA = ",";
    private boolean fileExists = false;
    private static final Map<String, Integer> INDEX_MAP;
    static
    {
        INDEX_MAP = new HashMap<String, Integer>();
        INDEX_MAP.put("isContainer", 0);
        INDEX_MAP.put("name", 1);
        INDEX_MAP.put("price", 2);
        INDEX_MAP.put("xcoord", 3);
        INDEX_MAP.put("ycoord", 4);
        INDEX_MAP.put("lengthSpan", 5);
        INDEX_MAP.put("widthSpan", 6);
        INDEX_MAP.put("red", 7);
        INDEX_MAP.put("green", 8);
        INDEX_MAP.put("blue", 9);
        INDEX_MAP.put("children", 10);
    }

    public void initialize(URL url, ResourceBundle rb){
        loadTreeItems(DEFAULT_NAME);
    }

    //Reads skyfarm.json file if it exists and returns the root object from the file.
    //If it doesn't exist the it will return a new root item with the name DEFAULT_NAME
    public Items readFile(){
        //Setting default root item
        Items rootItem = new ItemContainer(DEFAULT_NAME,0.0,0,0,0,0, javafx.scene.paint.Color.BLACK);

        try{
            //Setting the file
            File inFile = new File(FILENAME);
            //Checking if the file exists and also making sure it is not empty
            if(inFile.exists() && inFile.length() > 0){
                //Use the stream to retrieve the map object from the file and set the main item map equal to it
                FileReader file = new FileReader(inFile);
                BufferedReader reader = new BufferedReader(file);
                Map<String, String[]> childMap = new HashMap<String, String[]>();

                String line;
                //String output = "";

                while((line = reader.readLine()) != null){
                    //output += line;
                    Items lineItem = parseLine(line, childMap);
                    Main.itemMap.put(lineItem.getName(), lineItem);
                }

                for(Map.Entry<String, String[]> entry : childMap.entrySet()){
                    Main.itemMap.get(entry.getKey()).setChildren(entry.getValue());
                }
                file.close();
                reader.close();

                /**This loop is used to get the root item from the map.
                 You could set root item to Main.itemMap.get(DEFAULT_NAME) but I didn't here
                 just in case we want to be able to change the root item later. This just grabs
                 the first item in the itemMap, assigns it to rootItem, and breaks from the loop
                 so we don't waste time going through all of the entries.
                 */
                for (Map.Entry<String, Items> entry : Main.itemMap.entrySet()) {
                    rootItem = entry.getValue();
                    fileExists = true;
                    break;
                }
            }else{
                //If the file doesn't exist then we create it here
                inFile.createNewFile();
            }
        } catch(Exception e){
            System.out.println("Error in readFile()");
            e.printStackTrace();
        }

        return rootItem;
    }

    private Items parseLine(String line, Map<String, String[]> childMap){
        Items item = null;

        String[] atts = line.split(SEPARATOR_REGEX);
        Color color = Color.color(Double.parseDouble(atts[INDEX_MAP.get("red")]),Double.parseDouble(atts[INDEX_MAP.get("green")]),Double.parseDouble(atts[INDEX_MAP.get("blue")]));
        String[] children = atts[INDEX_MAP.get("children")].split(COMMA);
        if(!children[0].equalsIgnoreCase("null")) {
            childMap.put(atts[INDEX_MAP.get("name")], children);
        }
        if(atts[INDEX_MAP.get("isContainer")].equalsIgnoreCase(ITEM_CONTAINER_CODE)){
            item = new ItemContainer(atts[INDEX_MAP.get("name")],Double.parseDouble(atts[INDEX_MAP.get("price")]),Integer.parseInt(atts[INDEX_MAP.get("xcoord")]),
                    Integer.parseInt(atts[INDEX_MAP.get("ycoord")]), Integer.parseInt(atts[INDEX_MAP.get("lengthSpan")]),Integer.parseInt(atts[INDEX_MAP.get("widthSpan")]),color);
        }else{
            item = new Item(atts[INDEX_MAP.get("name")],Double.parseDouble(atts[INDEX_MAP.get("price")]),Integer.parseInt(atts[INDEX_MAP.get("xcoord")]),
                    Integer.parseInt(atts[INDEX_MAP.get("ycoord")]), Integer.parseInt(atts[INDEX_MAP.get("lengthSpan")]),Integer.parseInt(atts[INDEX_MAP.get("widthSpan")]),color);
        }
        return item;
    }
    /**Recursive method to build the TreeItem
     Param: treeItem - the TreeItem that represents the item
     Param: item - the next node in the tree that needs to be traversed
     */
    public TreeItem<String> buildTree(TreeItem<String> treeItem, Items item){
        //Creating new TreeItem instance for this iteration of the tree traversal
        Main.itemMap.put(item.getName(), item);
        TreeItem<String> newTreeItem = createNewTreeItem(item);
        //Getting the children of this item
        HashMap<String, Items> children = item.getChildren();
        //Checking if the item has children
        if(children != null && children.size() > 0) {
            //Looping through all of the items children
            for (Map.Entry<String, Items> entry : children.entrySet()) {
                //Creating a new TreeItem for the child
                TreeItem<String> childTreeItem = new TreeItem<String>(entry.getKey());
                /**
                 * This is where all of the magic happens
                 * We set this childTreeItem and all of its children
                 * as a child for the newTreeItem through recursion
                 */
                newTreeItem.getChildren().add(buildTree(childTreeItem, entry.getValue()));
            }
        }
        //Return the newTreeItem after having all of its children set
        return newTreeItem;
    }

    //This method draws the boxes on in the gridPane
    public void drawFarm(){
        //Loop through all of the items in Main.itemMap
        for(Map.Entry<String, Items> entry : Main.itemMap.entrySet()){
            //Use the items attributes to draw the item on the screen
            Items item = entry.getValue();
            Rectangle rec = new Rectangle();
            rec.setWidth(item.getWidthSpan());
            rec.setHeight(item.getLengthSpan());
            javafx.scene.paint.Color c = javafx.scene.paint.Color.color(item.getRed(), item.getGreen(), item.getBlue());
            rec.setFill(Color.WHITE);
            rec.setStroke(c);
            farmGrid.setRowIndex(rec, item.getyCoord());
            farmGrid.setColumnIndex(rec, item.getxCoord());
            farmGrid.getChildren().addAll(rec);
        }
    }

    //Adds the item's attributes to the screen
    public void displayItem(Items item){
        //Here we just check if the items attribute is null or empty and set the attribute value in the corresponding textbox
        if(!item.getName().isEmpty() || item.getName() != null) nameBox.setText(item.getName());
        priceBox.setText(String.valueOf(item.getPrice()));
        xCoord.setText(String.valueOf(item.getxCoord()));
        yCoord.setText(String.valueOf(item.getyCoord()));
        widthBox.setText(String.valueOf(item.getWidthSpan()));
        lenBox.setText(String.valueOf(item.getLengthSpan()));
        colorPicker.setValue(javafx.scene.paint.Color.color(item.getRed(), item.getGreen(), item.getBlue()));
        if(item.isItemContainer()){
            containerCheck.setSelected(true);
        }else{
            containerCheck.setSelected(false);
        }
    }

    private TreeItem<String> createNewTreeItem(Items item){
        String image = "resources/";
        if(item.isItemContainer()) {
            image = image + "folder.png";
        }else{
            image = image + "item.png";
        }

        return new TreeItem<String>(item.getName(), new ImageView(new Image(getClass().getResourceAsStream(image))));
    }

    //Writes Main.itemMap to the file
    public void saveMap(){
        try {
            File outFile = new File(FILENAME);
            PrintWriter writer = new PrintWriter(outFile);

            if(!outFile.exists()){
                if(outFile.createNewFile()){
                    System.out.println("Successful create");
                }else{
                    System.out.println("Failed to create");
                }
            }else{
                writer.write("");
                writer.flush();
            }

            for(Map.Entry<String, Items> entry : Main.itemMap.entrySet()){
                Items item = entry.getValue();
                String[] atts = new String[INDEX_MAP.size()];
                StringBuffer record = new StringBuffer();

                if(item.isItemContainer()) {
                    atts[INDEX_MAP.get("isContainer")] = "ic";
                }else{
                    atts[INDEX_MAP.get("isContainer")] = "i";
                }
                atts[INDEX_MAP.get("name")] = item.getName();
                atts[INDEX_MAP.get("price")] = String.valueOf(item.getPrice());
                atts[INDEX_MAP.get("xcoord")] = String.valueOf(item.getxCoord());
                atts[INDEX_MAP.get("ycoord")] = String.valueOf(item.getyCoord());
                atts[INDEX_MAP.get("lengthSpan")] = String.valueOf(item.getLengthSpan());
                atts[INDEX_MAP.get("widthSpan")] = String.valueOf(item.getWidthSpan());
                atts[INDEX_MAP.get("red")] = String.valueOf(item.getRed());
                atts[INDEX_MAP.get("green")] = String.valueOf(item.getGreen());
                atts[INDEX_MAP.get("blue")] = String.valueOf(item.getBlue());
                StringBuilder childBuilder = new StringBuilder();
                if(item.isItemContainer()) {
                    boolean first = true;
                    for (Map.Entry<String, Items> childEntry : item.getChildren().entrySet()) {
                        if(!first){
                            childBuilder.append(",");
                        }else{
                            first = false;
                        }
                        childBuilder.append(childEntry.getKey());
                    }
                    if(childBuilder.toString().isEmpty() || childBuilder.toString().startsWith("null")) {
                        atts[INDEX_MAP.get("children")] = "null";
                    }else{
                        atts[INDEX_MAP.get("children")] = childBuilder.toString();
                    }
                }
                for(String att : atts){
                    record.append(att + SEPARATOR);
                }
                atts[INDEX_MAP.get("children")] = childBuilder.toString();
                writer.append(record.toString() + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e){e.printStackTrace();}
    }

    //Sets tree and can load array of saved tree items
    public void loadTreeItems(String ...rootItems){
        //Assign the root item to whatever returns from the readFile method
        Items rootItem = readFile();
        //Set the root TreeItem using the rootItem
        TreeItem<String> root = new TreeItem<>(rootItem.getName());
        //Set Root in Main.itemap
        Main.itemMap.put(rootItem.getName(), rootItem);
        root.setExpanded(true);
        tree.setShowRoot(false);
        //If the file exists, build the tree based on the file
        if(fileExists) {
            root.getChildren().add(buildTree(root, rootItem));
            drawFarm();
            //If the file doesn't exist, create a new tree
        }else{
            root.getChildren().add(new TreeItem<>(new Item(rootItem.getName(), 0.0, 1, 1, 1, 1, javafx.scene.paint.Color.BLACK).getName()));
        }
        tree.setRoot(root);
        //Adds a listener to the Treeview in order to respond to changes in selection
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                //If the item is not the root item then display its attributes within the textboxes
                if(!selectedItem.getValue().equalsIgnoreCase(DEFAULT_NAME)) {
                    displayItem(Main.itemMap.get(selectedItem.getValue()));
                }
            }
        });
    }

    public Map.Entry<String,Items> getEntrySet(String findingKey) {
        ArrayList<HashMap<String, Items>> toCheck = new ArrayList<>();
        toCheck.add(Main.itemMap);
        while (!toCheck.isEmpty()) {
            HashMap<String, Items> checking = toCheck.remove(0);
            for (Map.Entry<String, Items> entry : checking.entrySet()) {
                if (entry.getKey().equals(findingKey)) return entry;
                if (entry.getValue().isItemContainer()) toCheck.add(entry.getValue().getChildren());
            }
        }
        return null;
    }

    public boolean containsKey(String findingKey){
        ArrayList<HashMap<String, Items>> toCheck = new ArrayList<>();
        toCheck.add(Main.itemMap);
        while(!toCheck.isEmpty()){
            HashMap<String, Items> checking = toCheck.remove(0);
            for(Map.Entry<String,Items> entry:checking.entrySet()){
                if (entry.getKey().equals(findingKey)) return true;
                if (entry.getValue().isItemContainer()) toCheck.add(entry.getValue().getChildren());
            }

        };
        return false;
    }

    //Adds new item to the tree with the selected value
    @FXML
    void addNewItem(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
        Items item = Main.itemMap.get(selectedItem.getValue());

        if(selectedItem == null || nameBox.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please select an object from the tree.");
        }else if(!item.isItemContainer()){
            JOptionPane.showMessageDialog(null, "You cannot add children to an item. Please use an item container to add a new child.");
        }else{
            //Add a bool to check for item-container
            javafx.scene.paint.Color cp = colorPicker.getValue();
            Items newItem = !containerCheck.isSelected() ? new Item(nameBox.getText(), Double.parseDouble(priceBox.getText()), (Integer.parseInt(xCoord.getText())), (Integer.parseInt(yCoord.getText())), Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), cp):
                    new ItemContainer(nameBox.getText(), Double.parseDouble(priceBox.getText()), (Integer.parseInt(xCoord.getText())), (Integer.parseInt(yCoord.getText())), Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), cp);

            item.getChildren().put(newItem.getName(), newItem);
            System.out.println("item's new children= " + Main.itemMap.get(item.getName()).getChildren());
            Main.itemMap.put(newItem.getName(), newItem);
            selectedItem.getChildren().add(createNewTreeItem(newItem));
            selectedItem.setExpanded(true);
            saveMap();
            drawFarm();
        }
    }

    @FXML
    void confirmEdit(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
        if(containerCheck.isSelected()){
            selectedItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/folder.png"))));
            Main.itemMap.put(selectedItem.getValue(), new ItemContainer(nameBox.getText(), Double.parseDouble(priceBox.getText()), Integer.parseInt(xCoord.getText()),
                    Integer.parseInt(yCoord.getText()), Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), colorPicker.getValue()));
        }else{
            if(selectedItem.getChildren() == null || selectedItem.getChildren().isEmpty()) {
                selectedItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/item.png"))));
                Main.itemMap.put(selectedItem.getValue(), new Item(nameBox.getText(), Double.parseDouble(priceBox.getText()), Integer.parseInt(xCoord.getText()),
                        Integer.parseInt(yCoord.getText()), Integer.parseInt(lenBox.getText()), Integer.parseInt(widthBox.getText()), colorPicker.getValue()));
            }else{
                JOptionPane.showMessageDialog(null, "Must remove children before you can change this container into an item.");
            }
        }
        saveMap();
        drawFarm();
    }

    @FXML
    void removeItem(ActionEvent event) {
        TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();

        if(selectedItem == null || selectedItem.getValue() == DEFAULT_NAME){
            JOptionPane.showMessageDialog(null, "Please add a new item to the list!");
        }

        else{
            Items item = Main.itemMap.get(selectedItem.getValue());
            if(item.isItemContainer()) {
                for (Map.Entry<String, Items> entry : item.getChildren().entrySet()) {
                    item.removeChild(entry.getKey());
                    Main.itemMap.remove(entry.getKey());
                }
            }
            Main.itemMap.remove(item.getName());
            Main.itemMap.get(selectedItem.getParent().getValue()).removeChild(selectedItem.getValue());
            selectedItem.getParent().getChildren().remove(selectedItem);
            selectedItem.setExpanded(true);
            saveMap();
            drawFarm();
        }
    }

    //Most certainly not needed anymore, as selection model does this.
    @FXML
    void selectItem(MouseEvent event) {

    }

    @FXML
    void sendName(ActionEvent event) {
        nameBox.getText();
    }

    @FXML
    void sendPrice(ActionEvent event) {
        Integer.parseInt(priceBox.getText());
    }

    @FXML
    void setColor(ActionEvent event) {

    }

    @FXML
    void setLen(ActionEvent event) {

    }

    @FXML
    void setWidth(ActionEvent event) {

    }

    @FXML
    void setXCoord(ActionEvent event) {

    }

    @FXML
    void setYCoord(ActionEvent event) {

    }
}

