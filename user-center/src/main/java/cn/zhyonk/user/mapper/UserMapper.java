package cn.zhyonk.user.mapper;


import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.zhyonk.entity.IndexUserCardInfo;
import cn.zhyonk.entity.Permission;
import cn.zhyonk.entity.User;

/**
 *
 * User 表数据库控制层接口
 *
 */
public interface UserMapper extends BaseMapper<User> {
	
	String checkLogin(@Param(value = "phone") String phone,@Param(value = "password") String password);
	
	User checkUser(@Param(value = "openid") String openid);

	User getUserByOpenId(@Param(value = "openid") String openid);

	List<String> loadRoles(@Param(value = "openid") String openid);

	List<Permission> loadPermissions(@Param(value = "openid") String openid);

	Set<IndexUserCardInfo> getWorkUserList();
	

}
