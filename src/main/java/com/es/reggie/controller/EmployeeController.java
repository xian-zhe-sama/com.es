package com.es.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.es.reggie.common.R;
import com.es.reggie.entity.Employee;
import com.es.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.接收密码做md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2根据页面提交用户名 username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3 如果没有查询到则返回登陆失败
        if (emp == null) {
            return R.error("登陆失败");
        }
        //4 密码错误返沪登录失败
        if (!password.equals(emp.getPassword())) {
            return R.error("登陆失败");
        }
        //5.查看用户状态 如果已禁用返回已禁用
        if (emp.getStatus() == 0) {
            return R.error("账户已禁用");
        }
        //6登录成功，将员工的id返回到Session并返回登录结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

}
