package com.weibo.service;

import com.weibo.entity.InformationBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationBaseR extends JpaRepository<InformationBase, String> {
}
