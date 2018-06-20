package com.yuan.dao;

import com.yuan.model.PeStudent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lujunjie
 * @date 2018/05/28
 */
public interface PeStudentDao {

    /**
     * 列表显示
     * @return
     */
    List<PeStudent> getAllStudentList();

    /**
     * 批量插入
     * @param students
     */
    void insertPeStudents(@Param("students") List<PeStudent> students);

    /**
     * 根据classCode查询
     * @param classCode
     * @return
     */
    List<PeStudent> findListUserByClassCode(String classCode);

}
