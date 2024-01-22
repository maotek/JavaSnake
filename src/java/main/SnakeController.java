import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.*;
import static javafx.scene.input.KeyCode.*;

public class SnakeController {

    public static class Entry {
        public int x;
        public int y;

        public Entry(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return x == entry.x && y == entry.y;
        }
    }

    public KeyCode dir = D;
    private int score = 7;
    private Stage stage;
    private AnchorPane parent;
    private final Deque<Rectangle> deque = new LinkedList<>();
    private Rectangle apple;
    private final HashMap<Rectangle, Entry> map = new HashMap<>();

    public void init(Parent parent, Scene scene, Stage stage) {
        this.stage = stage;
        this.parent = (AnchorPane) parent;
        scene.setOnKeyPressed(e->{
            switch (e.getCode()) {
                case W -> {
                    if (SnakeController.this.dir.equals(S)) {
                        return;
                    }
                    SnakeController.this.dir = W;
                }
                case S -> {
                    if (SnakeController.this.dir.equals(W)) {
                        return;
                    }
                    SnakeController.this.dir = S;
                }
                case A -> {
                    if (SnakeController.this.dir.equals(D)) {
                        return;
                    }
                    SnakeController.this.dir = A;
                }
                case D -> {
                    if (SnakeController.this.dir.equals(A)) {
                        return;
                    }
                    SnakeController.this.dir = D;
                }
            }
        });
        Rectangle r = new Rectangle();
        r.setHeight(20);
        r.setWidth(20);
        deque.addFirst(r);
        r.setX(0);
        r.setY(0);
        r.setFill(Color.GREEN);
        this.parent.getChildren().add(r);
        map.put(r, new Entry(0, 0));
        for (int i = 0; i < 6; i++) add();
        apple();
        Thread thread = new Thread(() -> {
            while (true) {
                Platform.runLater(this::move);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void add() {
        Rectangle r = new Rectangle();
        r.setHeight(20);
        r.setWidth(20);
        int headX = (int) deque.getFirst().getX();
        int headY = (int) deque.getFirst().getY();
        deque.addFirst(r);
        switch (dir) {
            case W -> {
                r.setX(headX);
                r.setY(headY + 20);
                map.put(r, new Entry(headX, headY + 20));
            }
            case S -> {
                r.setX(headX);
                r.setY(headY - 20);
                map.put(r, new Entry(headX, headY - 20));
            }
            case A -> {
                r.setX(headX + 20);
                r.setY(headY);
                map.put(r, new Entry(headX + 20, headY));
            }
            case D -> {
                r.setX(headX - 20);
                r.setY(headY);
                map.put(r, new Entry(headX - 20, headY));
            }
        }
        r.setFill(Color.GREEN);
        this.parent.getChildren().add(r);
    }

    public void apple() {
        Rectangle apple = new Rectangle();
        apple.setFill(Color.RED);
        Image img = new Image("download.png");
        apple.setFill(new ImagePattern(img));
        apple.setWidth(20);
        apple.setHeight(20);
        this.parent.getChildren().add(apple);
        Random rn = new Random();
        int x = rn.nextInt(20)*40;
        int y = rn.nextInt(20)*40;
        apple.setY(y);
        apple.setX(x);
        this.apple = apple;
    }

    public void move() {
        int headX = (int) deque.getLast().getX();
        int headY = (int) deque.getLast().getY();
        Rectangle toRemove = this.deque.getFirst();
        Entry e = new Entry(0, 0);
        if(dir.equals(S)) {
            this.deque.getFirst().setY((headY + 20)%800);
            this.deque.getFirst().setX(headX);
            e.x = headX;
            e.y = (headY + 20)%800;
        } else if(dir.equals(W)) {
            this.deque.getFirst().setY((headY - 20)<0?800:headY-20);
            this.deque.getFirst().setX(headX);
            e.x = headX;
            e.y = (headY - 20)<0?800:headY-20;
        } else if(dir.equals(A)) {
            this.deque.getFirst().setX((headX - 20)<0?800:headX-20);
            this.deque.getFirst().setY(headY);
            e.x = (headX - 20)<0?800:headX-20;
            e.y = headY;
        } else {
            this.deque.getFirst().setX((headX + 20)%800);
            this.deque.getFirst().setY(headY);
            e.x = (headX + 20)%800;
            e.y = headY;
        }

        if(eatApple(headX, headY)) {
            this.stage.setTitle("MaoSnake - score: " + this.score++);
            this.parent.getChildren().remove(this.apple);
            add();
            apple();
            return;
        }
        if(map.containsValue(e)) {Platform.exit();}
        map.put(toRemove, e);
        this.deque.addLast(this.deque.removeFirst());
    }

    public boolean eatApple(int x, int y) {
        if(apple == null) {return false;}
        return (apple.getY() == y && apple.getX() == x);
    }
}