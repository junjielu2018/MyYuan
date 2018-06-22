package com.yuan.dao;

import com.yuan.model.PeFreeSsInfo;
import com.yuan.model.PeStudent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lujunjie
 * @date 2018/05/28
 */
public interface ReptilianFreeSsInfoDao {

    /**
     * 批量插入
     * @param freeSsInfoList
     */
    void saveFreeSsInfoList(@Param("freeSsInfoList") List<PeFreeSsInfo> freeSsInfoList);
}
