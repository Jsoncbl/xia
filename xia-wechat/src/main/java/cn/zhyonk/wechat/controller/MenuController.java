package cn.zhyonk.wechat.controller;



import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.zhyonk.annotation.IsLogin;
import cn.zhyonk.common.utils.ResponseData;
import cn.zhyonk.controller.BaseController;
import cn.zhyonk.rpc.api.OauthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value="/menu")
@Api(value="菜单管理")
public class MenuController extends BaseController{

	@Autowired
	private OauthService oauthService;
	
	@IsLogin
	@RequestMapping(value="/getMineMenu")
	@ApiOperation(value = "获取Mine下的菜单")
	public ResponseData getMineMenu(HttpServletRequest request) {
		String openid = (String) SecurityUtils.getSubject().getPrincipal();
		
		String token = request.getParameter("token");
		ResponseData responseData = ResponseData.ok();
        return responseData;
	}
}
