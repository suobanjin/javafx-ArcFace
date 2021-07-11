package zzuli.zw.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MyCameraConfig extends Application {
    /**
     * 拍照存储的文件路径
     */
    String filePath = "C:/image/";

    private static class WebCamInfo {
        private String webCamName;
        private int webCamIndex;

        public String getWebCamName() {
            return webCamName;
        }

        public void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }

        public int getWebCamIndex() {
            return webCamIndex;
        }

        public void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }
    }

    private FlowPane bottomCameraControlPane;
    private FlowPane topPane;
    private ImageView imgWebCamCapturedImage;
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private BorderPane webCamPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("摄像");
        BorderPane root = new BorderPane();
        topPane = new FlowPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setHgap(20);
        topPane.setOrientation(Orientation.HORIZONTAL);
        topPane.setPrefHeight(40);
        root.setTop(topPane);
        webCamPane = new BorderPane();
        webCamPane.setStyle("-fx-background-color: #ccc;");
        imgWebCamCapturedImage = new ImageView();
        webCamPane.setCenter(imgWebCamCapturedImage);
        root.setCenter(webCamPane);
        createTopPanel();
        bottomCameraControlPane = new FlowPane();
        bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
        bottomCameraControlPane.setAlignment(Pos.CENTER);
        bottomCameraControlPane.setHgap(20);
        bottomCameraControlPane.setVgap(10);
        bottomCameraControlPane.setPrefHeight(40);
        bottomCameraControlPane.setDisable(true);
        createCameraControls();
        root.setBottom(bottomCameraControlPane);
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(700);
        primaryStage.setWidth(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
        Platform.runLater(this::setImageViewSize);
    }

    protected void setImageViewSize() {
        double height = webCamPane.getHeight();
        double width = webCamPane.getWidth();
        imgWebCamCapturedImage.setFitHeight(height);
        imgWebCamCapturedImage.setFitWidth(width);
        imgWebCamCapturedImage.prefHeight(height);
        imgWebCamCapturedImage.prefWidth(width);
        imgWebCamCapturedImage.setPreserveRatio(true);
    }

    private void createTopPanel() {
        int webCamCounter = 0;
        Label lbInfoLabel = new Label("选择摄像头：");
        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        topPane.getChildren().add(lbInfoLabel);
        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }

        ComboBox<WebCamInfo> cameraOptions = new ComboBox<>();
        cameraOptions.setItems(options);
        String cameraListPromptText = "选择摄像头：";
        cameraOptions.setPromptText(cameraListPromptText);
        cameraOptions.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) -> {
            if (arg2 != null) {
                initializeWebCam(arg2.getWebCamIndex());
            }
        });
        topPane.getChildren().add(cameraOptions);
    }

    protected void initializeWebCam(final int webCamIndex) {
        Task<Void> webCamTask = new Task<Void>() {
            @Override
            protected Void call() {
                webCam = Webcam.getWebcams().get(webCamIndex);
                webCam.setViewSize(WebcamResolution.VGA.getSize());
                webCam.open();
                startWebCamStream();
                return null;
            }
        };
        Thread webCamThread = new Thread(webCamTask);
        webCamThread.setDaemon(true);
        webCamThread.start();
        bottomCameraControlPane.setDisable(false);
    }

    protected void startWebCamStream() {
        stopCamera = false;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                while (!stopCamera) {
                    try {
                        if ((grabbedImage = webCam.getImage()) != null) {
                            Platform.runLater(() -> {
                                Image mainImage = SwingFXUtils.toFXImage(grabbedImage, null);

                                imageProperty.set(mainImage);
                            });
                            grabbedImage.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
                Platform.runLater(()->{
                    getImagine();
                    timer.cancel();
                });
            }
        }, 2000);

    }

    private void createCameraControls() {
    }

    protected void getImagine() {
        Image image = imgWebCamCapturedImage.getImage();
        ImageView imageView = new ImageView();
        Label label = new Label("姓      名：");
        TextField textField = new TextField();
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(label, textField);
        Label label2 = new Label("专业年级：");
        TextField textField2 = new TextField();
        HBox hBox2 = new HBox(5);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(label2, textField2);
        Label label3 = new Label("学      号：");
        TextField textField3 = new TextField();
        Label label4 = new Label("相   似  度：");
        TextField textField4 = new TextField();
        HBox hBox3 = new HBox(5);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.getChildren().addAll(label3, textField3);
        HBox hBox4 = new HBox(5);
        hBox4.setAlignment(Pos.CENTER);
        hBox4.getChildren().addAll(label4, textField4);
        Stage stage = new Stage();
        stage.setOnHiding(event -> {
           Timer timer = new Timer();
           timer.schedule(new TimerTask() {
               @Override
               public void run() {
                   System.out.println("hello");
                   Platform.runLater(()->{
                       getImagine();
                       timer.cancel();
                   });
               }
           }, 2000);
        });
        Alert alert = new Alert(Alert.AlertType.WARNING);
        VBox vBox = new VBox(10);
        try {
            File file = new File(filePath + "temp" + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            FaceUtils faceUtils = new FaceUtils();
            faceUtils.setImageInfo(filePath + "temp"+ ".png");
            byte[] faceFeature = faceUtils.getFaceFeature();
            if (faceFeature == null) {
                /*alert.setContentText("请确定镜头中已包含人脸！");
                alert.showAndWait();*/
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("hello");
                        Platform.runLater(()->{
                            getImagine();
                            timer.cancel();
                        });
                    }
                }, 2000);
            } else {
                Student studentInfo = new Student();
                studentInfo.setFeature(faceFeature);
                InfoMapper infoMapper = new InfoMapper();
                List<Student> allInfo = infoMapper.findAllInfo();
                Map<Float,Student> map = new TreeMap<>((o1, o2) -> (int)(o2-o1));
                for (Student student1 : allInfo) {
                    byte[] feature = student1.getFeature();
                    float compare = faceUtils.compareTo(faceFeature, feature);
                    map.put(compare, student1);
                }
                Set<Float> keySet = map.keySet();
                for (Float aFloat : keySet) {
                    Student student = map.get(aFloat);
                    textField.setText(student.getName());
                    textField2.setText(student.getGrade());
                    textField3.setText(student.getStudent());
                    textField4.setText(aFloat.toString());
                    InputStream inputStream = new FileInputStream(student.getImage());
                    imageView.setImage(new Image(inputStream));
                    inputStream.close();
                    break;
                }
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(10, 10, 10, 10));
                vBox.getChildren().addAll(imageView, hBox, hBox2, hBox3,hBox4);
                stage.setScene(new Scene(vBox));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
