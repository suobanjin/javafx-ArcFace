package zzuli.zw.camera;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName InfoMapper
 * @Description TODO
 * @Author wu2wen
 * @Date 2021/7/9 12:10
 * @Version 1.0
 */
public class InfoMapper {
    private TxQueryRunner queryRunner = new TxQueryRunner();

    public int insertInfo(Student student){
        String sql = "insert into user(name,student,grade,feature,image) values(?,?,?,?,?)";
        Object[] objects = new Object[]{student.getName(),student.getStudent(),student.getGrade(),student.getFeature(),student.getImage()};
        try {
            return queryRunner.update(sql, objects);
        }catch (Exception e){
            return -1;
        }
    }

    public List<Student> findAllInfo()  {
        String sql = "select * from user";
        try {
            return queryRunner.query(sql, new BeanListHandler<>(Student.class));
        }catch (Exception e){
            return null;
        }
    }

    public static void main(String[] args) {
        InfoMapper infoMapper = new InfoMapper();
        List<Student> allInfo = infoMapper.findAllInfo();
        System.out.println(allInfo);
    }
}
