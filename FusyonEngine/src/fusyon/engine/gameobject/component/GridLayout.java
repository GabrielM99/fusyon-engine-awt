package fusyon.engine.gameobject.component;

import fusyon.engine.gameobject.GameObject;

import java.awt.*;
import java.util.List;

public class GridLayout extends Component{

    private List<GameObject> childrenList;

    public GridLayout() {
        super("GridLayout");
    }

    @Override
    public void start() {
        childrenList = getParent().getChildrenList();
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {

    }
}
