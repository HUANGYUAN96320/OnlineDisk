package cn.od.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.regexp.internal.recompile;

import cn.od.bean.User;
import cn.od.bean.UserFollow;

public class UserFollowDao {
	
	private DBInfo db = DBInfo.getInstance();
	
	public List<User> findUserFollow(int id) {
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<User> userList = new ArrayList<User>();
		User user2;
		try{
			ps = conn.prepareStatement("SELECT * FROM user WHERE id in(SELECT befollow_id FROM user_follow WHERE follower_id = ? and isFollow = 1)");
			ps.setInt(1,id);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				user2 = new User();
				user2.setUsername(rs.getString("username"));//避免用1234，以防数据库修改
				user2.setId(rs.getInt("id"));	
				userList.add(user2);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userList;
	}
	
	public int addUserFollow(int follower_id,int befollow_id,int isFollow){
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		int flag = 0;
		try{
			ps = conn.prepareStatement("insert into user_follow values(?,?,?)");
			
			ps.setInt(1,follower_id);
			ps.setInt(2,befollow_id);
			ps.setInt(3,isFollow);
			
			flag = ps.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null) 
					ps.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	public int update(int follower_id,int befollow_id,int isfollow){
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		int flag = 0;
		try {
			ps = conn.prepareStatement("update user_follow set isFollow = ? where follower_id = ? and befollow_id = ?");
			ps.setInt(1,isfollow );
			ps.setInt(2, follower_id);
			ps.setInt(3, befollow_id);
			flag = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	public UserFollow findUserFollowById(int follower_id,int befollow_id){
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserFollow userFollow = new UserFollow();
		try {
			ps = conn.prepareStatement("SELECT * from user_follow WHERE follower_id = ? AND befollow_id = ?");
			ps.setInt(1, follower_id);
			ps.setInt(2, befollow_id);
			rs = ps.executeQuery();
			while(rs.next()){
				userFollow.setfollower_id(rs.getInt("follower_id"));
				userFollow.setbefollow_id(rs.getInt("befollow_id"));
				userFollow.setIsFollow(rs.getInt("isFollow"));
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return userFollow;
	}
}
