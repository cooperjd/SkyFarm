package sample;

import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    private boolean fileExists = false;

    public void initialize(URL url, ResourceBundle rb){
        loadTreeItems(DEFAULT_NAME);
    }

    //Reads skyfarm.obj file if it exists and returns the root object from the file.
    //If it doesn't exist the it will return a new root item with the name DEFAULT_NAME
    public Items readFile(){
        //Setting default root item
        Items rootItem = new ItemContainer(DEFAULT_NAME,0.0,0,0,0,0, javafx.scene.paint.Color.BLACK);

        try{
            //Setting the file
            File inFile = new File("skyfarm.obj");
            //Checking if the file exists and also making sure it is not empty
            if(inFile.exists() && inFile.length() > 0){
                //Object stream will get the saved item map from the file
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inFile));
                //Use the stream to retrieve the map object from the file and set the main item map equal to it
                Main.itemMap = (HashMap<String, Items>) ois.readObject();
                //Making sure the map is not null.
                if(Main.itemMap != null) {
                    //Setting the fileExists flag to true because the file exists and contains a valid map
                    fileExists = true;

                    /**This loop is used to get the root item from the map.
                       You could set root item to Main.itemMap.get(DEFAULT_NAME) but I didn't here
                       just in case we want to be able to change the root item later. This just grabs
                       the first item in the itemMap, assigns it to rootItem, and breaks from the loop
                       so we don't waste time going through all of the entries.
                     */
                    for (Map.Entry<String, Items> entry : Main.itemMap.entrySet()) {
                        rootItem = entry.getValue();
                        break;
                    }
                }
                //Closing the stream
                ois.close();
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

    /**Recursive method to build the TreeItem
       Param: treeItem - the TreeItem that represents the item
       Param: item - the next node in the tree that needs to be traversed
     */
    public TreeItem<String> buildTree(TreeItem<String> treeItem, Items item){
        //Creating new TreeItem instance for this iteration of the tree traversal
        TreeItem<String> newTreeItem = new TreeItem<String>(treeItem.getValue());
        //Getting the children of this item
        HashMap<String, Items> children = item.getChildren();
        //Checking if the item has children
        if(children != null && children.size() > 0) {
            //Looping through all of the items children
            for (Map.Entry<String, Items> entry : item.getChildren().entrySet()) {
                //Creating a new TreeItem for the child
                TreeItem<String> childTreeItem = new TreeItem<String>(entry.getValue().getName());
                /**
                 * This is where all of the magic happens
                 * We set this childTreeItem and all of its children
                 * as a child for the newTreeItem through recursion
                 */
                newTreeItem.getChildren().add(buildTree(childTreeItem, Main.itemMap.get(childTreeItem.getValue())));
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

    //Writes Main.itemMap to the file
    public void saveMap(){
        try {
            File outFile = new File("skyfarm.obj");
            if(outFile.exists()){
                if(outFile.delete()){
                    System.out.println("Successful Delete");
                }else{
                    System.out.println("Failed to Delete");
                }
            }
            for(Map.Entry<String, Items> entry : Main.itemMap.entrySet()){
                System.out.println(entry.getKey() + " - " + entry.getValue() + "\n");
            }

            System.out.println("----------------------------------------------------------------------------------");
            if(outFile.createNewFile()){
                System.out.println("Successful create");
            }else{
                System.out.println("Failed to create");
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outFile));

            oos.writeObject(Main.itemMap);
            oos.flush();
            oos.close();
        }
        catch (IOException e){e.printStackTrace();}
    }

    //Sets tree and can load array of saved tree items
    public void loadTreeItems(String ...rootItems){
        //Assign the root item to whatever returns from the readFile method
        Items rootItem = readFile();
        //Set the root TreeItem using the rootItem
        TreeItem<String> root = new TreeItem<>(rootItem.getName());
        root.setExpanded(true);
        tree.setShowRoot(false);
        //Put the root item into the itemMap
        Main.itemMap.put(rootItem.getName(), rootItem);
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
            Main.itemMap.put(newItem.getName(), newItem);
            selectedItem.getChildren().add(new TreeItem<>(newItem.getName()));
            selectedItem.setExpanded(true);
            saveMap();
        }
    }

    @FXML
    void confirmEdit(ActionEvent event) {
        try{
            FileReader file = new FileReader(new File("skyfarm.json"));
            BufferedReader reader = new BufferedReader(file);

            String line;
            String output = "";

            while((line = reader.readLine()) != null){
                output += line;
            }
            Main.itemMap = new Gson().fromJson(output,Main.itemMap.getClass());
            Map.Entry<String, Items> mapped;
            if((mapped = getEntrySet(nameBox.getText()))!=null){
                ////FINISH THIS TOMORROW
            }
            System.out.println(Main.itemMap);

        } catch(IOException e){e.printStackTrace();}}

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
                    Main.itemMap.remove(entry.getKey());
                }
            }
            Main.itemMap.remove(item.getName());
            selectedItem.getParent().getChildren().remove(selectedItem);
            selectedItem.setExpanded(true);
            saveMap();
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

