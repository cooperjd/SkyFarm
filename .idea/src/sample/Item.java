package sample;

import java.awt.*;

public class Item extends Items {

    public Item(String name,
                 Double price,
                 Integer xCoord,
                 Integer yCoord,
                 Integer lengthSpan,
                 Integer widthSpan,
                 javafx.scene.paint.Color color){
        super(name,price,xCoord,yCoord,lengthSpan,widthSpan,color);
    }

    @Override
    protected boolean isItemContainer() {
        return false;
    }
}
