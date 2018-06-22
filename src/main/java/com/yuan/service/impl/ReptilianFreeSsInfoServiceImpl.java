package com.yuan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuan.dao.PeStudentDao;
import com.yuan.dao.ReptilianFreeSsInfoDao;
import com.yuan.model.PeFreeSsInfo;
import com.yuan.model.PeStudent;
import com.yuan.service.PeStudentService;
import com.yuan.service.ReptilianFreeSsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lujunjie
 * @date 2018/05/28
 */
@Service(value = "freeSsInfoService")
public class ReptilianFreeSsInfoServiceImpl implements ReptilianFreeSsInfoService {

    @Autowired
    private ReptilianFreeSsInfoDao freeSsInfoDao;

    @Override
    public List<PeFreeSsInfo> getFreeSsInfoList(List<String> infoList) {
        List<PeFreeSsInfo> freeSsInfoList = new ArrayList<>();
        for (String info : infoList) {
            try {
                String[] infos = info.split(" ");
                if (info != null && infos.length >0){
                    Object obj = PeFreeSsInfo.class.newInstance();
                    Field[] fields = PeFreeSsInfo.class.getDeclaredFields();
                    for (int i = 0; i < infos.length; i++) {
                        fields[i+1].setAccessible(true);
                        fields[i+1].set(obj, infos[i]);
                    }
                    freeSsInfoList.add((PeFreeSsInfo) obj);
                }
            }catch (Exception e){

            }
        }
        return freeSsInfoList;
    }

    @Override
    public void saveFreeSsInfoList(List<PeFreeSsInfo> freeSsInfoList) {
        freeSsInfoDao.saveFreeSsInfoList(freeSsInfoList);
    }
}
