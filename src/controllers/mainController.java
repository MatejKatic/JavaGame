/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.Gamedata;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import javafx.scene.control.Label;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.w3c.dom.Node;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javax.xml.parsers.ParserConfigurationException;
import multicast.ServerThread;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import synchronization.GamedataManager;
import tasks.ScoreTask;
import threads.CricleThread;
import threads.TimelineThread;
import utils.ReflectionUtils;

/**
 *
 * @author GraphX
 */
public class mainController implements Initializable {

    @FXML
    private Label lblScore;

    @FXML
    private Label lblScoreAdd;

    @FXML
    private AnchorPane scene;

    @FXML
    private Circle circle;

    @FXML
    private Rectangle paddle;

    @FXML
    private Rectangle bottomZone;

    @FXML
    private Button startButton;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnPrint;

    @FXML
    private Label lblMain;

    @FXML
    private Button btnSerial;

    public mainController() {
    }

    Rectangle rectangle = new Rectangle(30, 30);
    private double width = 550;
    private double height = 200;
    private double numberOfCreatedperWidth = width;
    private double numberOfCreatedperHeight = height;
    private int spaceCheck = 1;
    private ServerThread serverThread;
    final private List<Rectangle> bricks = new ArrayList<>();
    private final ObservableList<Gamedata> gamedata = FXCollections.observableArrayList();
    private GamedataManager gamedataManager;
    private static final String FILENAME = "breakout.xml";
    private double deltaX = -1;
    private double deltaY = -3;
    private int count = 0;
    private int score = 0;
    Timeline timelineXML = new Timeline();

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            checkCollisionPaddle(paddle);
            try {
                serverThread.trigger(new Gamedata(
                        bricks.size(),
                        score,
                        paddle.getLayoutX(),
                        circle.getLayoutX(),
                        circle.getLayoutY()));
            } catch (Exception e) {
                Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, e);
            }

            if (bricks.removeIf(brick -> checkCollisionBrick(brick))) {
                score += 10;
            }

            if (!bricks.isEmpty()) {
                bricks.removeIf(brick -> checkCollisionBrick(brick));
            } else {
                timeline.stop();
                lblScore.setText("You won the game! Your score is: " + score);
                lblScore.setLayoutX(169);
                lblScore.setLayoutY(114);
                startButton.setPrefWidth(300);
                startButton.setLayoutX(160);
                startButton.setVisible(true);
                lblScoreAdd.setVisible(false);
                startButton.setText("Play Again!");
            }

            Gamedata save = new Gamedata(bricks.size(), score, paddle.getLayoutX(), circle.getLayoutX(), circle.getLayoutY());
            gamedata.add(save);
            checkCollisionScene(scene);
        }
    }));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initServerThread();
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void beforeGame() {
        lblScore.setLayoutX(14);
        lblScore.setLayoutY(14);
        lblScoreAdd.setVisible(true);
        startButton.setVisible(false);
        btnPrint.setVisible(false);
        btnLoad.setVisible(false);
        lblMain.setVisible(false);
        score = 0;
        lblScore.setText("Score: ");
    }

    @FXML
    void startGameButtonAction(ActionEvent event) {
        beforeGame();
        create();

        startGame();
        StartCircleThread();
        StartTimelineThread();

    }

    public void StartCircleThread() {
        new CricleThread(circle, bottomZone, this, bricks).start();
    }

    public void StartTimelineThread() {
        new TimelineThread(this, circle, bottomZone, bricks).start();
    }

    public void startGame() {
        startTaskThread();
        timeline.play();
    }

    public void BrickCreate() {
        gamedataManager = new GamedataManager(width, height, this);
        gamedataManager.start();
    }

    public void createBricksSync(Queue<Rectangle> load) {
        for (double i = numberOfCreatedperHeight; i > 0; i = i - 50) {
            for (double j = numberOfCreatedperWidth; j > 0; j = j - 55) {
                load.forEach(p -> scene.getChildren().add(rectangle));
            }
        }
    }

    public void startTaskThread() {
        Task<Void> scoreTask = new ScoreTask(bricks, circle, score, bottomZone);
        lblScoreAdd.textProperty().bind(scoreTask.messageProperty());
        Thread timeoutThread = new Thread(scoreTask);
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }

    public void checkCollisionScene(javafx.scene.Node node) {
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = circle.getLayoutX() >= (bounds.getMaxX() - circle.getRadius());
        boolean leftBorder = circle.getLayoutX() <= (bounds.getMinX() + circle.getRadius());
        boolean bottomBorder = circle.getLayoutY() >= (bounds.getMaxY() - circle.getRadius());
        boolean topBorder = circle.getLayoutY() <= (bounds.getMinY() + circle.getRadius());

        if (rightBorder || leftBorder) {
            deltaX *= -1;
        }
        if (bottomBorder || topBorder) {
            deltaY *= -1;
        }
    }

    public void CircleAction() {
        circle.setLayoutX(circle.getLayoutX() + deltaX);
        circle.setLayoutY(circle.getLayoutY() + deltaY);
    }

    public boolean checkCollisionBrick(Rectangle brick) {

        if (circle.getBoundsInParent().intersects(brick.getBoundsInParent())) {
            boolean rightBorder = circle.getLayoutX() >= ((brick.getX() + brick.getWidth()) - circle.getRadius());
            boolean leftBorder = circle.getLayoutX() <= (brick.getX() + circle.getRadius());
            boolean bottomBorder = circle.getLayoutY() >= ((brick.getY() + brick.getHeight()) - circle.getRadius());
            boolean topBorder = circle.getLayoutY() <= (brick.getY() + circle.getRadius());

            if (rightBorder || leftBorder) {
                deltaX *= -1;
            }
            if (bottomBorder || topBorder) {
                deltaY *= -1;
            }

            scene.getChildren().remove(brick);

            return true;
        }
        return false;
    }

    public void create() {
        for (double i = height; i > 0; i = i - 80) {
            for (double j = width; j > 0; j = j - 55) {
                if (spaceCheck % 2 == 0) {
                    Rectangle rectangle = new Rectangle(j, i, 30, 30);
                    rectangle.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                    scene.getChildren().add(rectangle);
                    bricks.add(rectangle);

                }
                spaceCheck++;
            }
        }
    }

    public void movePaddle() {

        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int x = (int) b.getX();

        Bounds bounds = scene.localToScreen(scene.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = x;
        double paddleWidth = paddle.getWidth();

        if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
        } else if (xPos < sceneXPos + (paddleWidth / 2)) {
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(scene.getWidth() - paddleWidth);
        }
    }

    public void checkCollisionPaddle(Rectangle paddle) {

        if (circle.getBoundsInParent().intersects(paddle.getBoundsInParent())) {

            boolean rightBorder = circle.getLayoutX() >= ((paddle.getLayoutX() + paddle.getWidth()) - circle.getRadius());
            boolean leftBorder = circle.getLayoutX() <= (paddle.getLayoutX() + circle.getRadius());
            boolean bottomBorder = circle.getLayoutY() >= ((paddle.getLayoutY() + paddle.getHeight()) - circle.getRadius());
            boolean topBorder = circle.getLayoutY() <= (paddle.getLayoutY() + circle.getRadius());

            if (rightBorder || leftBorder) {
                deltaX *= -1;
            }
            if (bottomBorder || topBorder) {
                deltaY *= -1;
            }
        }
    }

    public void checkCollisionBottomZone() {

        timeline.stop();
        bricks.forEach(brick -> scene.getChildren().remove(brick));
        bricks.clear();
        lblScore.setText("Game over! Your score is: " + score);
        lblScore.setLayoutX(199);
        lblScore.setLayoutY(154);
        startButton.setVisible(true);
        lblScoreAdd.setVisible(false);
        startButton.setText("Retry!");

        deltaX = -1;
        deltaY = -3;

        circle.setLayoutX(300);
        circle.setLayoutY(300);

    }

    public void onBtnSerial(ActionEvent actionEvent) {

        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element root = document.createElement("Gamedata");
            document.appendChild(root);

            gamedata.forEach(d -> document.getDocumentElement().appendChild(createDataElement(d, document)));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(FILENAME));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    private Node createDataElement(Gamedata d, Document document) {
        Element driver = document.createElement("data");
        driver.appendChild(createElement(document, "bricknumber", String.valueOf(d.getNumberOfBricks())));
        driver.appendChild(createElement(document, "score", String.valueOf(d.getScore())));
        driver.appendChild(createElement(document, "paddlexposition", String.valueOf(d.getPaddleX())));
        driver.appendChild(createElement(document, "circlexposition", String.valueOf(d.getCircleX())));
        driver.appendChild(createElement(document, "circleyposition", String.valueOf(d.getCircleY())));
        return driver;
    }

    private Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private void initServerThread() {
        serverThread = new ServerThread();
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void OnbtnLoad(ActionEvent actionEvent) throws SAXException, IOException, ParserConfigurationException, InterruptedException {

        boolean isLoadedOnce = true;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(FILENAME);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("data");

        lblScore.setLayoutX(14);
        lblScore.setLayoutY(14);
        startButton.setVisible(false);
        btnPrint.setVisible(false);
        btnSerial.setVisible(false);
        btnLoad.setVisible(false);
        lblMain.setVisible(false);

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = (Node) nList.item(i);

            Thread.sleep(3);
            timelineXML.play();
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) nNode;

                Node node1 = elem.getElementsByTagName("bricknumber").item(0);
                int BrickNumber = Integer.parseInt(node1.getTextContent());

                Node node2 = elem.getElementsByTagName("score").item(0);
                double Score = Double.parseDouble(node2.getTextContent());

                Node node3 = elem.getElementsByTagName("paddlexposition").item(0);
                double PaddleX = Double.parseDouble(node3.getTextContent());

                Node node4 = elem.getElementsByTagName("circlexposition").item(0);
                double CircleX = Double.parseDouble(node4.getTextContent());

                Node node5 = elem.getElementsByTagName("circleyposition").item(0);
                double CircleY = Double.parseDouble(node5.getTextContent());

                timelineXML = new Timeline(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        bricks.removeIf(brick -> checkCollisionBrick(brick));
                        lblScore.setText("Score: " + Score);
                        paddle.setLayoutX(PaddleX);
                        circle.setLayoutX(CircleX);
                        circle.setLayoutY(CircleY);
                    }
                }));

                while (isLoadedOnce) {
                    for (double k = height; k > 0; k = k - 80) {
                        for (double j = width; j > 0; j = j - 55) {
                            if (spaceCheck % 2 == 0) {
                                if (count <= BrickNumber * 2) {
                                    Rectangle rectangle = new Rectangle(j, k, 30, 30);
                                    rectangle.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                                    scene.getChildren().add(rectangle);
                                    bricks.add(rectangle);
                                }
                            }
                            count++;
                            spaceCheck++;

                        }

                    }
                    isLoadedOnce = false;
                }

            }

        }

    }

    public static void showInfoMessage(String title, String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void generirajDokumentaciju() {

        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("<html>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("<head>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("<title>Dokumentacija</title>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("</head>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("<body>");
        htmlBuilder.append(System.lineSeparator());

        ReflectionUtils.findAllPackagesAndClasses("src/", htmlBuilder);

        htmlBuilder.append("<p></p>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("</body>");
        htmlBuilder.append(System.lineSeparator());
        htmlBuilder.append("</html>");
        htmlBuilder.append(System.lineSeparator());

        try (FileWriter htmlWriter = new FileWriter("dokumentacija.html")) {
            htmlWriter.write(htmlBuilder.toString());

            showInfoMessage("Uspješno spremanje dokumentacije",
                    "Informacija!",
                    "Datoteka \"dokumentacija.html\""
                    + " je uspješno generirana!", Alert.AlertType.INFORMATION);

        } catch (IOException ex) {
            Logger.getLogger(mainController.class.getName()).log(
                    Level.SEVERE, null, ex);
            showInfoMessage("Neuspješno spremanje dokumentacije",
                    "Informacija!",
                    "Dogodila se greška!", Alert.AlertType.ERROR);
        }
    }

    public void OnbtnPrint(ActionEvent actionEvent) {
        generirajDokumentaciju();
    }

}
