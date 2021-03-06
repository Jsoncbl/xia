package cn.zhyonk.wechat.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import cn.zhyonk.annotation.IsLogin; 
import cn.zhyonk.common.utils.DESUtils;
import cn.zhyonk.common.utils.JedisUtils;
import cn.zhyonk.common.utils.ResponseData;
import cn.zhyonk.common.utils.StringUtils;
import cn.zhyonk.controller.BaseController;
import cn.zhyonk.entity.Login;
import cn.zhyonk.entity.MemberCard;
import cn.zhyonk.entity.MemberCardInfo;
import cn.zhyonk.entity.RedisLogin;
import cn.zhyonk.entity.User;
import cn.zhyonk.entity.WechatUser;
import cn.zhyonk.jwt.JWT;
import cn.zhyonk.rpc.api.LocalUserService;
import cn.zhyonk.rpc.api.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/user")
@Api(value="用户管理")
public class UserController extends BaseController{ 
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LocalUserService localUserService;
	
	@IsLogin
	@RequestMapping(value="/login")
	@ApiOperation(value = "用户登录")
	public ResponseData login(HttpServletRequest request) {
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		ResponseData responseData;
		if (!StringUtils.isEmpty(password)) {
			responseData = ResponseData.ok();
			String encryptPassword = DESUtils.getEncryptString(password);
	        Login login = new Login();
	        login.setPhone(phone);
	        login.setPassword(encryptPassword);
	        //先到数据库验证
	        String openid = userService.checkLogin(login);
	        if(null != openid) {
	        	Set<String> roles = userService.loadRoles(openid);
	        	login.setRoles(roles);
	            login.setUid(openid);
	            long refTime = System.currentTimeMillis()+60L*1000L*50L;;
	            //给用户jwt加密生成token
	            String token = JWT.sign(login,refTime);
	            //封装成对象返回给客户端
	            responseData.putDataValue("token", token);
	            responseData.putDataValue("uid", openid);
	            responseData.putDataValue("exp", refTime);
	            RedisLogin rlogin = new RedisLogin();
	            rlogin.setRefTime(refTime);
	            rlogin.setToken(token);
	            rlogin.setUid(openid);
	            JedisUtils.setObject(openid, rlogin, 0);
	        }
	        else{
	            responseData =  ResponseData.customerError();
	        }   
		}else {
			responseData = ResponseData.badRequest();
		}
		
        return responseData;
	}

	@RequestMapping(value="/test",method=RequestMethod.POST)
	@ApiOperation(value = "用户登录")
	public ResponseData test(HttpServletRequest request) {
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("test status", "success");
        return responseData;
	}
	
	@RequestMapping(value="/getUserInfo")
	@ApiOperation(value = "获取用户的信息")
	public ResponseData getUserInfo(HttpServletRequest request,HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		ResponseData responseData = null;
		String token = request.getParameter("token");
		if (token!=null) {
			Login login = JWT.unsign(token, Login.class);
			String openid = login.getUid();
			User user = userService.getUserByOpenId(openid);
			WechatUser wechatInfo = localUserService.selectUserInfoByOpenid(openid);
			responseData = ResponseData.ok();
			responseData.putDataValue("userInfo", user);
			if (wechatInfo!=null) {
				responseData.putDataValue("wechatInfo", wechatInfo);
			}
			return responseData;
		}
		responseData = ResponseData.unauthorized();
        return responseData;
	}
	@RequestMapping(value="/regist",method=RequestMethod.POST)
	@ApiOperation(value = "用户注册")
	public ResponseData regist(HttpServletRequest request) {
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("test status", "success");
        return responseData;
	}
	@RequestMapping(value="/getMemberCard",method=RequestMethod.GET)
	@ApiOperation(value = "获取客户会员卡")
	public ResponseData getMemberCard(HttpServletRequest request) {
		String openid = request.getParameter("openId");
		List<MemberCard> list = userService.selectMemberCardList(openid);
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("memberCardInfo", list);
        return responseData;
	}
	@RequestMapping(value="/getMemberCardByNumber",method=RequestMethod.GET)
	@ApiOperation(value = "通过会员号获取客户会员卡")
	
	public ResponseData getMemberCardByNumber(HttpServletRequest request) {
		String number = request.getParameter("number");
		MemberCardInfo list = userService.getMemberCardByNumber(number);
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("memberCardInfo", list);
        return responseData;
	}
}
