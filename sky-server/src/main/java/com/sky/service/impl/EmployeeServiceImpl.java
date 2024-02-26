package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对

        /* 对密码进行加密
        * getBytes是将字符串转换为字符数组的方法
        *
        * */

        password=DigestUtils.md5DigestAsHex(password.getBytes());


        if (!password.equals(employee.getPassword())) {
           //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
    * 新增员工的方法
    *  */
    public void sava(EmployeeDTO employeeDTO) {
        Employee employee =new Employee();
        /* 这样拷贝的更快 ，原本要一个一个set*/
        BeanUtils.copyProperties(employeeDTO,employee);
        /* 设置其他属性,使用一个常量类提高代码的可维护性 */
        employee.setStatus(StatusConstant.ENABLE);
        /* 使用md5加密 也是常量类 */
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        /* 设置当前创建人和修改人id */
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);

    }
    /* 分页查询 */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        /* select * from employee limit 0,10 这是要是用的sql代码，用来实现分页查询
        *可以使用mybatis的东西来简化分页查询
        * */
        /* 前面这个参数是页码后面参数是长度 */
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        /* 指定写法，该插件要求创建 page */
        Page<Employee> page =employeeMapper.pageQuery(employeePageQueryDTO);

        long total =page.getTotal();
        List<Employee> records=page.getResult();

        /* 该项目约定将数据都封装成result对象，这里封装为PageResult对象是便于存储分页查询数据，然后交给控制层封装为result对象 */
        return new PageResult(total,records);
    }
    /* 启用禁用员工账号 */
    @Override
    public void startOrStop(Integer status, long id) {
        // updata employee set status =? where id =?
        /* 这里打算做动态更新 */

        // Employee employee =new Employee();
        // employee.setStatus(status);
        // employee.setId(id);
        //这是传统方法，还可以使用构建器来进行创建
        Employee employee=Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

}
