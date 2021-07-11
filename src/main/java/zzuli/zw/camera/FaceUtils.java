package zzuli.zw.camera;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * @ClassName FaceUtils
 * @Description TODO
 * @Author wu2wen
 * @Date 2021/7/9 15:00
 * @Version 1.0
 */
public class FaceUtils {
    private  FaceEngine faceEngine;
    private ImageInfo imageInfo;
    private FunctionConfiguration functionConfiguration;
    private static final String configFile = "C:\\Users\\wu2we\\Desktop\\ArcSoft_ArcFace_Java_Windows_x64_V3.0\\libs\\WIN64";
    public FaceUtils(){
        faceEngine = new FaceEngine(configFile);
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        faceEngine.init(engineConfiguration);
    }
    public FaceUtils(String imageUrl){
        imageInfo = getRGBData(new File(imageUrl));
        faceEngine = new FaceEngine(configFile);
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        faceEngine.init(engineConfiguration);
    }

    public FaceEngine getFaceEngine() {
        return faceEngine;
    }

    public void setImageInfo(String imageUrl) {
        this.imageInfo = getRGBData(new File(imageUrl));
    }
    public float compareTo(byte[] byte1,byte[] byte2){
        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(byte1);
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(byte2);
        FaceSimilar faceSimilar = new FaceSimilar();
        int errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        if (errorCode != ResponseCode.MOK)return -1;
        System.out.println("相似度：" + faceSimilar.getScore());
        return faceSimilar.getScore();
    }
    /**
     * @Date: 2021/7/9 16:43
     * @Author 索半斤
     * @Description: 获取人脸信息
     * @MethodName:
     */
    public  List<FaceInfo> getFaceInfo(){
        if (imageInfo == null)return null;
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        if (errorCode != ResponseCode.MOK)return null;
        if (faceInfoList.size() == 0)return null;
        return faceInfoList;
    }
    /**
     * @Date: 2021/7/9 16:43
     * @Author 索半斤
     * @Description: 检测是否为活体
     * @MethodName:
     */
    public  boolean isLive(){
        if (imageInfo == null)return false;
        faceEngine.setLivenessParam(0.5f, 0.7f);
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        List<FaceInfo> faceInfo = getFaceInfo();
        int errorCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, configuration);
        if (errorCode != ResponseCode.MOK)return false;
        //活体检测
        List<LivenessInfo> livenessInfoList = new ArrayList<>();
        errorCode = faceEngine.getLiveness(livenessInfoList);
        if (errorCode != ResponseCode.MOK)return false;
        if (livenessInfoList.size() == 0)return false;
        return livenessInfoList.get(0).getLiveness() == 1;
    }
    /**
     * @Date: 2021/7/9 15:46
     * @Author 索半斤
     * @Description: 获取人脸特征值
     * @MethodName:
     */
    public byte[] getFaceFeature(){
        if (imageInfo == null)return null;
        if (!isLive())return null;
        List<FaceInfo> faceInfoList = getFaceInfo();
        if (faceInfoList == null || faceInfoList.size() == 0)return null;
        //特征提取
        FaceFeature faceFeature = new FaceFeature();
        int errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        if (errorCode != ResponseCode.MOK)return null;
        return faceFeature.getFeatureData();
    }

    public void unInit(){
        faceEngine.unInit();
    }
}
