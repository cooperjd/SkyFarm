package sample;

import java.awt.*;

public class ItemContainer extends Items {

    public ItemContainer(String name,
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
        return true;
    }
}
