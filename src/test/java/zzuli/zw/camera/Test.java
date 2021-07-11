package zzuli.zw.camera;

import java.util.Arrays;

/**
 * @ClassName Test
 * @Description TODO
 * @Author wu2wen
 * @Date 2021/7/9 16:20
 * @Version 1.0
 */
public class Test {

    @org.junit.Test
    public void test01(){
        FaceUtils faceUtils = new FaceUtils();
        faceUtils.setImageInfo("C:\\Users\\wu2we\\Pictures\\Camera Roll\\WIN_20210320_18_54_19_Pro.jpg");
        boolean live = faceUtils.isLive();
        System.out.println(live);
    }

    @org.junit.Test
    public void test02(){
        FaceUtils faceUtils = new FaceUtils();
        faceUtils.setImageInfo("C:\\Users\\wu2we\\Pictures\\Camera Roll\\WIN_20210320_18_54_19_Pro.jpg");
        byte[] faceFeature = faceUtils.getFaceFeature();
        System.out.println(Arrays.toString(faceFeature));
    }
}
