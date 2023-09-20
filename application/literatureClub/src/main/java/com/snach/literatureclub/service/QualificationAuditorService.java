package com.snach.literatureclub.service;

import com.snach.literatureclub.dao.QualificationAuditorDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface QualificationAuditorService {
}

@Mapper
@Service
class QualificationAuditorServiceImpl implements QualificationAuditorService{
    @Autowired
    private QualificationAuditorDao qualificationAuditorDao;
}