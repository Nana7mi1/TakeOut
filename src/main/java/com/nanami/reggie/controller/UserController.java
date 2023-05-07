package com.nanami.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nanami.reggie.common.Result;
import com.nanami.reggie.entity.User;
import com.nanami.reggie.service.UserService;
import com.nanami.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String MyFrom;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(MyFrom);
//            message.setTo(phone);
//            message.setSubject("【外卖】用户登录验证码");
//            message.setText(code);//
//            try {
//                mailSender.send(message);
//                //需要保存一下验证码，后面用来验证
//                session.setAttribute(phone, code);
//                return Result.success("发送成功");
//            } catch (MailException e) {
//                e.printStackTrace();
//                return Result.error("短信发送失败");
//            }
            //验证码缓存到redis
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
//            session.setAttribute(phone, code);
            log.info("code={}", code);
            return Result.success("发送成功");
        }
        return Result.error("短信发送失败");
    }


    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //Object codeSession = session.getAttribute(phone);
        Object codeSession = stringRedisTemplate.opsForValue().get(phone);
        if(codeSession !=null && codeSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            //登录成功，删缓存
            stringRedisTemplate.delete(phone);
            return Result.success(user);
        }
        return Result.error("登录失败");
    }

}
