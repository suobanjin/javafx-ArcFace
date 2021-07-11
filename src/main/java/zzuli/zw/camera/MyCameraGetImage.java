package zzuli.zw.camera;

import com.github.sarxos.webcam.Webcam;
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
import java.io.IOException;
import java.util.List;

public class MyCameraGetImage extends Application {
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

       /* @Override
        public String toString() {
            return "摄像头" + (Integer.parseInt(webCamName.split("Integrated Webcam ")[1]) + 1);
        }*/
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
    }

    private void createCameraControls() {
        Button banCameraGetImage = new Button();
        banCameraGetImage.setOnAction(event -> getImagine());
        banCameraGetImage.setText("拍照采集");
        bottomCameraControlPane.getChildren().add(banCameraGetImage);
    }

    protected void getImagine() {
        Image image = imgWebCamCapturedImage.getImage();
        ImageView imageView = new ImageView(image);
        Label label = new Label("姓      名：");
        TextField textField = new TextField();
        textField.setPromptText("例：张三");
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(label, textField);
        Label label2 = new Label("专业年级：");
        TextField textField2 = new TextField();
        textField2.setPromptText("例：软件工程18-04");
        HBox hBox2 = new HBox(5);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(label2, textField2);
        Label label3 = new Label("学      号：");
        TextField textField3 = new TextField();
        textField3.setPromptText("例：541813460400");
        HBox hBox3 = new HBox(5);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.getChildren().addAll(label3, textField3);
        Button button = new Button("保存");
        Stage stage = new Stage();
        stage.setTitle("信息录入");
        button.setOnAction(event -> {
            String name = textField.getText();
            String student = textField2.getText();
            String grade = textField3.getText();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(student) || StringUtils.isEmpty(grade)){
                alert.setContentText("必选项不能为空!");
                alert.showAndWait();
            }else {
                try {
                    File file = new File(filePath + student + ".png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    FaceUtils faceUtils = new FaceUtils();
                    faceUtils.setImageInfo(filePath + student + ".png");
                    byte[] faceFeature = faceUtils.getFaceFeature();
                    if (faceFeature == null){
                        alert.setContentText("人脸信息采集失败，请确定镜头中已包含人脸！");
                        alert.showAndWait();
                    }else {
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("正在采集");
                        alert.show();
                        Student studentInfo = new Student();
                        studentInfo.setFeature(faceFeature);
                        studentInfo.setGrade(grade);
                        studentInfo.setName(name);
                        studentInfo.setStudent(student);
                        studentInfo.setImage(filePath + student + ".png");
                        InfoMapper infoMapper = new InfoMapper();
                        List<Student> allInfo = infoMapper.findAllInfo();
                        boolean flag = false;
                        for (Student student1 : allInfo) {
                            byte[] feature = student1.getFeature();
                            float compare = faceUtils.compareTo(faceFeature, feature);
                            if (compare >= 0.99) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag){
                            alert.setHeaderText("人脸已经录入过了!");
                            alert.show();
                        }else {
                            int i = infoMapper.insertInfo(studentInfo);
                            alert.hide();
                            if (i <= 0) {
                                alert.setHeaderText("采集失败");
                                alert.showAndWait();
                            } else {
                                alert.setHeaderText("采集成功");
                                alert.showAndWait();
                                stage.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.getChildren().addAll(imageView, hBox,hBox2,hBox3,button);
        stage.setScene(new Scene(vBox));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
