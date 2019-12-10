package tanks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class FXMLDocumentTanksController implements Initializable {

    GraphicsContext gc;
    Sprite player;
    Sprite bullet;
    Sprite gameoverImg;
    Sprite detonation;
    MediaPlayer blasterSound;
    MediaPlayer kosmossound;
    MediaPlayer detonationsound;
    MediaPlayer losesound;
    double t;
    int i;
    int y;
    int x;
    double t_sound = 10000, t_soundMax = 2;

    @FXML
    private AnchorPane myAnchorPane;
    @FXML
    private TextField countField;
    int count;
    int lifeLevel;
    ArrayList<String> input;
    @FXML
    private AnchorPane myAnchorPane2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Canvas canvas = new Canvas(1200, 800);
        myAnchorPane.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        Image tank = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\1.png").toURI().toString(), 50, 50, true, false);
        Image countEnemyImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\2.png").toURI().toString(), 30, 30, true, false);
        Image gameover = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\gameover.jpg").toURI().toString());
        Image enemy = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\2.png").toURI().toString(), 50, 50, true, false);
        Image bulletImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\blast.png").toURI().toString(), 50, 50, true, false);
        Image space = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\spaceImg1.jpg").toURI().toString());
        Image detonationImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\explosion4.gif").toURI().toString());
        Image lifeImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\heart.png").toURI().toString(), 30, 30, true, false);
        Image winImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Img\\win.png").toURI().toString(), 400, 400, true, false);

        player = new Sprite(550, 750, 50, 50, 1, 0, 2, 1000, tank);
        bullet = new Sprite(2210, 770, 50, 50, 0, -1, 2, 1000, bulletImg);
        gameoverImg = new Sprite(150, 50, 200, 200, 0, 0, 2, 1000, gameover);
        detonation = new Sprite(2000, 750, 50, 50, 1, 0, 2, 1000, detonationImg);
        detonation.t_ImgMax = 0.5;
        Sprite countEnemy = new Sprite(1110, 0, 10, 10, 0, 0, 2, 1000, countEnemyImg);
        Sprite life = new Sprite(1000, 5, 50, 50, 0, 0, 2, 1000, lifeImg);
        Sprite win = new Sprite(450, 300, 200, 200, 0, 0, 2, 1000, winImg);
        Sprite  ship = new Sprite(0, 0, 50, 50, 0.5, 0.5, 1000, 2, enemy); //объект для проверки на пересечение

        Media sound1 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Sound\\blaster.mp3").toURI().toString());
        blasterSound = new MediaPlayer(sound1);
        blasterSound.setCycleCount(1);
        Media sound2 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Sound\\kosmossound.mp3").toURI().toString());
        kosmossound = new MediaPlayer(sound2);
        Media sound3 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Sound\\detonation.mp3").toURI().toString());
        detonationsound = new MediaPlayer(sound3);
        Media sound4 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Sound\\lose.mp3").toURI().toString());
        losesound = new MediaPlayer(sound4);
        Media sound5 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Tanks\\src\\tanks\\Sound\\winsound.mp3").toURI().toString());
        MediaPlayer winsound = new MediaPlayer(sound5);

        final long startNanoTime = System.nanoTime();

        ArrayList<Sprite> enemy_ar = new ArrayList<>(50);
        input = new ArrayList<>(10);

        myAnchorPane2.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();

                // добавляем в случае, если там ещё нет этой клавиши               
                if (!input.contains(code)) {
                    input.add(code);
                }
            }
        });

        myAnchorPane2.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                input.remove(code);
            }
        });

        //инициализация  врагов
        for (x = 0; x < 1250; x += 100) {
            for (y = 0; y < 150; y += 50) {
                enemy_ar.add(new Sprite(0 + Math.random() * x, 0 + Math.random() * y, 50, 50, 0.4, 0.1, 2, 1000, enemy));

//проверим, не пересекается ли спрайт с остальными из массива ar
                /*boolean found = false;

                for (i = 0; i < enemy_ar.size(); i++) {
                    if (new Sprite(x, y, 50, 50, 0.4, 0.1, 2, 1000, enemy).intersects(enemy_ar.get(i))) {
                        found = true;
                        //если не пересекается, то круто, заканчиваем цикл. Иначе - пробуем по циклу все сначала 
                        if (found == false) {
                            break;
                       }
                    }
               }*/
               
               

            }

        }

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                t = (currentNanoTime - startNanoTime) / 1000000000.0;

                kosmossound.play();

                gc.drawImage(space, 0, 0);//фон
                player.render(gc);//отображение игрока
                bullet.render(gc);//отображение лазера
                countEnemy.render(gc);
                life.render(gc);

                //движение лазера
                bullet.move(t);

                //отображение и движение врагов
                for (i = 0; i < enemy_ar.size(); i++) {
                    enemy_ar.get(i).render(gc);
                    enemy_ar.get(i).move(t);

                    //условие пересечения лазера и врага 
                    if (bullet.intersects(enemy_ar.get(i))) {
                        detonation.t_Img = 0;
                        detonationsound.play();
                        detonation.positionX = enemy_ar.get(i).positionX - 30;
                        detonation.positionY = enemy_ar.get(i).positionY;
                        enemy_ar.remove(i);
                        bullet.positionX = 2000;
                        //подсчет очков
                        count = count + 1;
                        countField.setText(Integer.toString(count));
                        //запуск и остановка спрайта - взрыв
                        //detonationsound.play();
                        //detonationsound.stop();

                    }
                    if (detonation.t_Img < detonation.t_ImgMax) {
                        //gc.drawImage(detonationImg, detonation.positionX, detonation.positionY);
                        detonation.render(gc);
                        //enemy_ar.add(new Sprite(detonation.positionX, detonation.positionY, 50, 50, 0.4, 0.1, 2, 1000, detonationImg));
                        detonation.t_Img += 0.01/enemy_ar.size();
                    } else if (detonation.t_Img >= detonation.t_ImgMax && detonation.t_Img <= detonation.t_ImgMax + 1) {
                        detonationsound.stop();
                        detonation.t_Img = 10000;
                        //enemy_ar.remove(new Sprite(detonation.positionX, detonation.positionY, 50, 50, 0.4, 0.1, 2, 1000, detonationImg));
                    }

                    //условие пересечения игрока и врага 
                    if (player.intersects(enemy_ar.get(i))) {
                        stop();
                        gameoverImg.render(gc);
                        losesound.play();
                        kosmossound.stop();
                    }

                    //возврат врагов на экран
                    if (enemy_ar.get(i).positionY > 800) {
                        enemy_ar.get(i).positionY = 0 + Math.random() * 10;
                        enemy_ar.get(i).positionX = 0 + Math.random() * 1200;

                    }
                    //условия достижения врагами границ поля
                    if (enemy_ar.get(i).positionX > 1200) {
                        enemy_ar.get(i).velocityX = -enemy_ar.get(i).velocityX;
                    }
                    if (enemy_ar.get(i).positionX < 0) {
                        enemy_ar.get(i).velocityX = -enemy_ar.get(i).velocityX;
                    }

                }

                //условие набора определенного количества очков
                if (count == 70) {
                    stop();
                    win.render(gc);
                    kosmossound.stop();
                    winsound.play();

                }

                //условие окончания очков жизни
                if (lifeLevel < 0) {
                    stop();
                    gameoverImg.render(gc);
                    losesound.play();
                    kosmossound.stop();
                }
                if (input.contains("A")) {
                    player.positionX -= 3;
                }
                if (input.contains("D")) {
                    player.positionX += 3;
                }
                if (input.contains("W")) {
                    player.positionY -= 3;
                }
                if (input.contains("S")) {
                    player.positionY += 3;
                }
                if (input.contains("ENTER")) {
                    bullet = new Sprite(2210, 770, 50, 50, 0, -1, 2, 1000, bulletImg);
                    bullet.positionX = player.positionX + 10;
                    bullet.positionY = player.positionY - 40;

                    //blasterSound.play();
                    //blasterSound.stop();
                    t_sound = 0;
                    blasterSound.play();

                }

                if (t_sound < t_soundMax) {
                    t_sound += 0.01;
                } else if (t_sound >= t_soundMax && t_sound <= t_soundMax + 1) {
                    blasterSound.stop();
                    t_sound = 10000;
                }

            }
        }.start();
    }

    @FXML
    private void menuItemSave(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

        fileChooser.getExtensionFilters().add(extFilter);

//добыть сцену из основного элемента AnchorPane нашего окна
        Stage stage = (Stage) myAnchorPane.getScene().getWindow();

// для этой сцены stage запустить показ диалогового окна - save либо load
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file, false)) {
                String playerPositionX = Double.toString(player.positionX);
                String playerPositionY = Double.toString(player.positionY);

                String[] game = new String[2];

                game[0] = playerPositionX;
                game[1] = playerPositionY;

                for (i = 0; i < game.length; i++) {
                    writer.write(game[i] + "\r\n");

                    writer.flush();
                }

            } catch (IOException ex) {
                System.out.println("EXCEPTION");
            }
        }
    }

    @FXML
    private void menuItemLoad(ActionEvent event) {

    }

    @FXML
    private void menuItemContinue(ActionEvent event) {

    }

    @FXML
    private void menuItemNew(Event event) {

    }

    class Sprite {

        Image image;
        double positionX;
        double positionY;
        double velocityX;
        double velocityY;
        double width;
        double height;
        double t_ImgMax;
        double t_Img;

        public Sprite(double positionX, double positionY, double width, double height, double velocityX, double velocityY, double t_ImgMax, double t_Img, Image image) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.width = width;
            this.height = height;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.image = image;
            this.t_ImgMax = t_ImgMax;
            this.t_Img = t_Img;
        }

        void move(double time) {
//параметр time берется выше из  
//double t = (currentNanoTime - startNanoTime) / 1000000000.0; 
//скорость умножить на прошедшее время между кадрами = расстояние
            positionX += velocityX * 5;
            positionY += velocityY * 5;
        }

        void render(GraphicsContext gc) {
            gc.drawImage(image, positionX, positionY);
        }

        Rectangle2D getBoundary() {
            return new Rectangle2D(positionX, positionY, width, height);
        }

        boolean intersects(Sprite s) {
            return s.getBoundary().intersects(this.getBoundary());
        }
    }

}
