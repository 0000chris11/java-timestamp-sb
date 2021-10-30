package com.cofii.ts.first;

import com.cofii.ts.other.CSS;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VFRowTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(6, 6, 6, 6));

        for(int a = 0;a < 3; a++){
            VFRow row = new VFRow("Screw it");
            if(a == 0){
                row.getHbProperty().getChildren().add(new Label("A property"));
               
            }
            row.getHbBtns().setStyle(CSS.NODE_BORDER_ERROR);
            
            HBox hbox = new HBox();
            hbox.setPadding(new Insets(6, 6, 6, 6));
            hbox.setSpacing(4.0);

            hbox.getChildren().addAll(row.getLb(), row.getVbCenter(), row.getHbBtns());
            vBox.getChildren().add(hbox);
        }
        
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
}
