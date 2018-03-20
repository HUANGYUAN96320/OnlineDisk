package cn.od.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.od.bean.User;
import cn.od.bean.UserFile;
import cn.od.dao.UserFileDao;
import cn.od.util.Const;

public class LoginFilter implements Filter{

	private UserFileDao userFileDao = new UserFileDao();
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String url = req.getRequestURI();//  /Netdisc/   /Netdisc/index.jsp
		//projectName 直接访问项目时的路径
		String projectName = request.getServletContext().getContextPath().toString()+"/";//项目路径
		//游客访问 获取未加密的文件
		if(url.equals(projectName) || url.equals(projectName+"index.jsp")){
			String filename = request.getParameter("filename");
			List<UserFile> fileList;
			if(filename != null && !filename.equals("")){
				fileList = userFileDao.findSharedFileWithName(filename);
			}else{
				fileList = userFileDao.findSharedFile();
			}
			request.setAttribute("fileList", fileList);
			chain.doFilter(request, response);
			return;
		}
		
		String userPath = request.getServletContext().getContextPath().toString()+"/user";//user单独放行
		if(url.equals(userPath) || parseUrl(url)){
			//是公开url，放行
			chain.doFilter(request, response);
			return;
		}
		User user = (User)req.getSession().getAttribute(Const.SESSION_USER);
		//session中不存在user,则拦截，跳转到登录界面
		if(user==null){
			((HttpServletResponse)response).sendRedirect(projectName+"common/login.jsp");
			return;
		}
		
		chain.doFilter(request, response);
	}

	private boolean parseUrl(String url) {
		for(String open_url : Const.OPEN_URL_LIST){
			//如果URL是公开的URL，返回true.
			if(url.indexOf(open_url)>=0)//检索open_url，判断url是否为开放url
				return true;
		}
		return false;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

}
