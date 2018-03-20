package cn.od.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.od.bean.User;
import cn.od.bean.UserFile;
import cn.od.bean.UserFollow;
import cn.od.dao.UserDao;
import cn.od.dao.UserFileDao;
import cn.od.dao.UserFollowDao;
import cn.od.util.Const;

public class UserServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2146748539648197507L;
	private UserDao userDao = new UserDao();
	private UserFileDao userFileDao = new UserFileDao();
	private UserFollowDao userFollowDao = new UserFollowDao();

	public UserServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		User user = (User) request.getSession().getAttribute(Const.SESSION_USER);
		String url = request.getRequestURI();// 
		//根君action判断当前用户的操作
		String action = request.getParameter("action");
		//跳转到主页，所有共享
		if (action == null || action.equals("") || action.equals("index")) {
			String filename = request.getParameter("filename");
			List<UserFile> fileList = new ArrayList<UserFile>();
			List<User> followUserList = new ArrayList<User>();
			
			if(filename != null && !filename.equals("")){
				fileList = userFileDao.findSharedFileWithName(filename);
			}else{
				fileList = userFileDao.findSharedFile();
			}
			followUserList = userFollowDao.findUserFollow(user.getId());
			for (User followUser: followUserList) {
				for (UserFile userfile : fileList) {
					if (userfile.getOwnerId() == followUser.getId()) {
						userfile.setFollowed(1);
					}
				}
			}
			request.setAttribute("fileList", fileList);
			request.getRequestDispatcher("/WEB-INF/user/index.jsp").forward(request, response);
			
		}else if(action.equals("myshare")){
			//跳转到 我的共享
			List<UserFile> fileList = userFileDao.findMySharedFile(user.getId());
			request.setAttribute("fileList", fileList);
			request.getRequestDispatcher("/WEB-INF/user/myshare.jsp").forward(request, response);
			
		}else if(action.equals("mydisk")){
			//跳转到 我的网盘
			List<UserFile> fileList = userFileDao.findFileListByOwnerId(user.getId());
			for (UserFile userFile : fileList) {
				System.out.println(userFile.getFilepassword());
				System.out.println(userFile.getIsShared());
			}
			request.setAttribute("fileList", fileList);
			request.getRequestDispatcher("/WEB-INF/user/mydisk.jsp").forward(request, response);
			
		}else if(action.equals("share")){
			//根据file_ID分享文件
			int id = 0;
			String sidString = request.getParameter("id").toString();
			if(sidString != null)
			id = Integer.valueOf(sidString);
			UserFile userFile = userFileDao.findUserFileById(id);
			//判断文件的所有者,文件主人才能分享
			if(user.getId() == userFile.getOwnerId()){
				userFile.setIsShared(1);
				userFileDao.update(userFile);
			}
			response.sendRedirect("user?action=mydisk");
			
		}else if(action.equals("pswdshare")){
			//设置密码分享
			String  filepassword = request.getParameter("filePassword").toString();
			int pswdshare_id = Integer.parseInt(request.getParameter("pswdshare_id"));
			int flag = 0;
			
			UserFile userFile = userFileDao.findUserFileById(pswdshare_id);
			//判断文件的所有者,文件主人才能设置密码分享
			if(user.getId() == userFile.getOwnerId()){
				userFile.setIsShared(1);
				userFile.setFilepassword(filepassword);
				flag = userFileDao.setFilePassword(filepassword, pswdshare_id);
			}
			response.sendRedirect("user?action=mydisk");
			
		}else if(action.equals("cancelShare")){
			//根据file_ID取消分享文件
			int id = Integer.parseInt(request.getParameter("id"));
			UserFile userFile = userFileDao.findUserFileById(id);
			//判断文件的所有者,文件主人才能分享
			if(user.getId() == userFile.getOwnerId()){
				userFile.setIsShared(0);
				userFileDao.update(userFile);
			}
			response.sendRedirect("user?action=myshare");
			
		}else if(action.equals("myfollow")){
			
			String projectName = request.getServletContext().getContextPath().toString()+"/";
			User user1 = (User)request.getSession().getAttribute(Const.SESSION_USER);
			List<User> userlist = new ArrayList<User>();
			if(user1 != null){
				userlist = userFollowDao.findUserFollow(user1.getId());
				request.setAttribute("userlist", userlist);
			}else {
				request.setAttribute("msgFail", "你还没登录！！");
				request.getRequestDispatcher(projectName+"common/login.jsp").forward(request, response);
			}
			//显示关注用户和动态
			request.getRequestDispatcher("/WEB-INF/user/myfollow.jsp").forward(request, response);
		}else if(action.equals("addfollow")){
			//根据User_ID添加关注
			int befollow_id = Integer.parseInt(request.getParameter("id"));
			int follower_id = user.getId();
			int flag = 0;
			//UserFollow userFollow = userFollowDao.findUserFollowById(user.getId(), befollow_id);
			UserFollow userFollow = new UserFollow();
			userFollow = userFollowDao.findUserFollowById(follower_id, befollow_id);
			if (userFollow.getfollower_id() != 0){
				flag = userFollowDao.update(follower_id, befollow_id, 1); 
			}else {
				flag = userFollowDao.addUserFollow(follower_id, befollow_id, 1);
			}
			
			response.sendRedirect("user?action=index");
		}else if(action.equals("unfollow")){
			//根据User_ID取消关注
			int befollow_id = Integer.parseInt(request.getParameter("id"));
			//UserFollow userFollow = userFollowDao.findUserFollowById(user.getId(), befollow_id);
			int flag = userFollowDao.update(user.getId(), befollow_id, 0);
			System.out.println(flag);
			/*if (flag > 0){
				request.setAttribute("msgSuccess", "取消关注成功");
				request.getRequestDispatcher("/WEB-INF/user/myfollow.jsp").forward(request, response);
			} else {
				request.setAttribute("msgFail", "取消失败");
				request.getRequestDispatcher("/WEB-INF/user/myfollow.jsp").forward(request, response);
			}*/
			response.sendRedirect("user?action=myfollow");
		}else if(action.equals("dynamic")){
			//根据User_ID取消关注
			List<User> user_followList = new ArrayList<User>();
			List<UserFile> follow_fileList = new ArrayList<UserFile>();
			List<Integer> idlist = new ArrayList<Integer>();
			user_followList = userFollowDao.findUserFollow(user.getId());
			for (User user_follow : user_followList) {
				idlist.add(user_follow.getId());
			}
			follow_fileList = userFileDao.findSharedUserFileById(idlist);
			request.setAttribute("fileList", follow_fileList);
			request.getRequestDispatcher("/WEB-INF/user/dynamic.jsp").forward(request, response);
		}else if(action.equals("delete")){
			//将字符串数组转为int 数组
			String[] vs = request.getParameterValues("ids");
			int[] ids = strArr2intArr(vs);
			userFileDao.deleteByIds(ids);
			response.sendRedirect("user?action=mydisk");
		}else if(action.equals("edit")){
			request.getRequestDispatcher("/WEB-INF/user/edit.jsp").forward(request, response);
		}else if(action.equals("editSubmit")){
			
			String ori_psw = request.getParameter("password");
			String new_psw = request.getParameter("password1");
			
			if(ori_psw.equals(user.getPassword())){
				user.setPassword(new_psw);
				userDao.update(user);
				request.setAttribute("msgSuccess", "密码修改成功！");
				request.getRequestDispatcher("/WEB-INF/user/edit.jsp").forward(request, response);
			}else{
				request.setAttribute("msgFail", "原密码错误！！修改失败！！");
				request.getRequestDispatcher("/WEB-INF/user/edit.jsp").forward(request, response);
			}
		}
			
	}

	
	public int[] strArr2intArr(String[] arr){
		if(arr == null || arr.length == 0)
			return null;
		int[] intArr = new int[arr.length];
		for(int i = 0; i<arr.length;i++){
			intArr[i] = Integer.parseInt(arr[i]);
		}
		return intArr;
	}
}
