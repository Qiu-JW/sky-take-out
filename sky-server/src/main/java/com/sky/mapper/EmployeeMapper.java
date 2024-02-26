package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    @Insert("INSERT INTO employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int insert(Employee employee);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    /* 根据主键来动态修改语句 */
    void update(Employee employee);
}