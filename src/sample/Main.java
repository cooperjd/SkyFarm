package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class Main extends Application {

    private Parent root;
    static HashMap<String, Items> itemMap = new HashMap<String, Items>();

    private Parent getInstance(){
        try{
            if(root == null){
                root =  FXMLLoader.load(getClass().getResource("sample.fxml"));}
        }catch(IOException e){}
        return root; }

    @Override
    public void start(Stage primaryStage) throws Exception{

        getInstance();
        primaryStage.setTitle("SkyFarm Basic Application - Group H");
        primaryStage.setScene(new Scene(root, 750, 550));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
