package cn.od.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import cn.od.bean.User;
import cn.od.bean.UserFile;
import cn.od.dao.UserFileDao;
import cn.od.util.Const;

public class DownloadFilter implements Filter{

	UserFileDao userFileDao = new UserFileDao();
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		User user = (User)((HttpServletRequest)request).getSession().getAttribute(Const.SESSION_USER);
		int id = Integer.parseInt(request.getParameter("id"));
		int userFile_id = Integer.parseInt(request.getParameter("id"));
		UserFile userFile = userFileDao.findUserFileById(id);
		
		//管理员都可以下载 ，      放行
		if (user == null) {
			request.setAttribute("userFile", userFile);
			chain.doFilter(request, response);
		}else {
			if(user.getIsAdmin() == 1)
			{
				request.setAttribute("userFile", userFile);
				chain.doFilter(request, response);
			}else if (userFile.getIsShared() == 1) {
				if (userFile.getFilepassword()!=null) {
					//普通用户有设置文件密码的文件要验证密码才能下载
					String projectName = request.getServletContext().getContextPath().toString()+"/";
					String filePassword = request.getParameter("filePassword").toString();
					if (userFileDao.pswDown(filePassword, userFile_id) == null) {
						request.setAttribute("msgFail","密码错误" );
						request.getRequestDispatcher("/WEB-INF/user/myfollow.jsp").forward(request, response);
						response.setContentType("text/html; charset=utf8");
						response.getWriter().print("密码错误！");
					} else {
						request.setAttribute("userFile", userFile);
						chain.doFilter(request, response);
					}
				}else {
					request.setAttribute("userFile", userFile);
					chain.doFilter(request, response);
				}
			} 
		}
		
		
		
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

}
