package com.yuan.service;

import com.github.pagehelper.PageInfo;
import com.yuan.model.PeFreeSsInfo;
import com.yuan.model.PeStudent;

import java.util.List;

/**
 *
 * @author lujunjie
 * @date 2018/05/28
 */
public interface ReptilianFreeSsInfoService {
    /**
     * getFreeSsInfoList
     * @param infoList
     * @return
     */
    List<PeFreeSsInfo> getFreeSsInfoList(List<String> infoList);

    /**
     * saveFreeSsInfoList
     * @param freeSsInfoList
     */
    void saveFreeSsInfoList(List<PeFreeSsInfo> freeSsInfoList);
}
