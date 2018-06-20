package com.yuan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuan.dao.PeStudentDao;
import com.yuan.model.PeStudent;
import com.yuan.service.PeStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author lujunjie
 * @date 2018/05/28
 */
@Service(value = "studentService")
public class PeStudentServiceImpl implements PeStudentService {

    @Autowired
    private PeStudentDao studentDao;
    @Override
    public void insertPeStudents(List<PeStudent> students) {
        studentDao.insertPeStudents(students);
    }

    @Override
    public PageInfo<PeStudent> findAllUser(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        PageHelper.startPage(pageNum, pageSize);
        List<PeStudent> userDomains = studentDao.getAllStudentList();
        PageInfo result = new PageInfo(userDomains);
        return result;
    }

    @Override
    public List<PeStudent> findListUserByClassCode(String classCode) {
        List<PeStudent> studentList = studentDao.findListUserByClassCode(classCode);
        return studentList;
    }
}
