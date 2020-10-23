package com.changgou.user.dao;
import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:传智播客
 * @Description:User的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface UserMapper extends Mapper<User> {

    /**
     * @author 栗子
     * @Description 增加当前用户的积分
     * @Date 12:02 2020/9/18
     * @param username
     * @param points
     * @return void
     **/
    @Update("UPDATE tb_user SET points = points + #{points} WHERE username = #{username}")
    void incr(@Param("username") String username, @Param("points") String points);
}
