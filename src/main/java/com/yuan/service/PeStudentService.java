package com.yuan.service;

import com.github.pagehelper.PageInfo;
import com.yuan.model.PeStudent;

import java.util.List;

/**
 *
 * @author lujunjie
 * @date 2018/05/28
 */
public interface PeStudentService {

    /**
     * 批量保存
     * @param students
     */
    void insertPeStudents(List<PeStudent> students);

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PeStudent> findAllUser(int pageNum, int pageSize);

    /**
     * 根据classCode查询
     * @param classCode
     * @return
     */
    List<PeStudent> findListUserByClassCode(String classCode);
}
