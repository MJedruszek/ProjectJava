package snek;

import java.util.Vector;

public class PlayerSnake implements Snake{
    private Direction prev_dir;
    private boolean is_ded;
    private Vector<SnakePart> snek = new Vector<SnakePart>();

    public PlayerSnake(int x1, int x2, int y1, int y2){
        prev_dir = Direction.RIGHT; //w prawo
        is_ded = false;
        //board inicjalizuje sneka, przekazuje mu dwa obok siebie miejsca na planszy (x1>x2, y1=y2)
        snek.add(new SnakePart(x1, y1));
        snek.add(new SnakePart(x2, y2));
    }

    @Override
    public void movePart(int pos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'movePart'");
    }

    @Override
    public boolean checkIfHit(int x, int y) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkIfHit'");
    }


    @Override
    public boolean move(Direction dir, Direction prev_dir) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }


    @Override
    public void update(Direction dir) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public SnakePart getSnakePart(int i) {
        return snek.get(i);
    }

    @Override
    public int getSnakeSize() {
        return snek.size();
    }
    
}
