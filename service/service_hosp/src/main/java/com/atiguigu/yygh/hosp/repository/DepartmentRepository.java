package com.atiguigu.yygh.hosp.repository;

import com.atiguigu.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {
    //查询科室接口
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
