package com.weibo.service;

import com.weibo.entity.InformationBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InformationBaseSImpl implements InformationBaseS {

    @Autowired
    private InformationBaseR r;

    @Override
    public void save(InformationBase base) {
        r.save(base);
    }
}
