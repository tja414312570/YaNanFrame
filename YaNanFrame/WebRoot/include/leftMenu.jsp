<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<meta charset="utf-8" />
<div class="cl-sidebar" data-position="right" data-step="1" data-intro="<strong>Fixed Sidebar</strong> <br/> It adjust to your needs.">
	<div class="cl-toggle"><i class="fa fa-bars"></i></div>
	<div class="cl-navblock">
		<div class="menu-space">
			<div class="content">
				<div class="side-user">
					<div class="avatar"><img src="images/avatar1_50.jpg" alt="Avatar" /></div>
					<div class="info">
						<a href="#" class="account">管理员</a>
						<img src="images/state_online.png" alt="Status" /> <span class="status">在线状态</span>
					</div>
				</div>
				<ul class="cl-vnavigation">
					<li >
						<a href="."><i class="fa fa-home"></i><span>首页</span></a>
					</li>
					<!--<li>
						<a href="#"><i class="fa  fa-user"></i><span>账号管理</span></a>
						<ul class="sub-menu">
							<li>
								<a href="account-my">个人信息</a>
							</li>
							<li>
								<a href="account">账号信息</a>
							</li>
							<li>
								<a href="account-manager">账号管理</a>
							</li>
							<li>
								<a href="account-devices">账号下设备管理</a>
							</li>
						</ul>
					</li>-->
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>Demo</span></a>
						<ul class="sub-menu">
							<li>
								<a href="demo-servlet">查看Servlet</a>
							</li>
							<li>
								<a href="demo-database">查看数据库</a>
							</li>
							<li>
								<a href="demo-tabels">查看数据表</a>
							</li>
							<li>
								<a href="demo-RTDT">RTDT测试</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>开发文档</span></a>
						<ul class="sub-menu">
							<li>
								<a href="tutorial-core-configure">核心配置</a>
							</li>
							<li>
								<a href="tutorial-framework">框架结构</a>
							</li>
							<li>
								<a href="tutorial-servlet">servlet</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>Hibernate</span></a>
						<ul class="sub-menu">
							<li>
								<a href="tutorial-hibernate-noun">名词解释</a>
							</li>
							<li>
								<a href="tutorial-hibernate">数据库配置</a>
							</li>
							<li>
								<a href="tutorial-hibernate-use">数据库的使用</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="consume"><i class="fa fa-credit-card"></i><span>消费管理</span></a>
					</li>
				</ul>
			</div>
		</div>
		<div class="text-right collapse-button" style="padding:7px 9px;">
			<input type="text" class="form-control search" placeholder="Search..." />
			<button id="sidebar-collapse" class="btn btn-default" style=""><i style="color:#fff;" class="fa fa-angle-left"></i></button>
		</div>
	</div>
</div>
<script>
	window.onload=function(e){
	//修改菜单选中项
		$(".cl-vnavigation").find("li").each(function(e){
			$(this).removeClass("active");
		});
		var menu = location.pathname.substr(location.pathname.lastIndexOf("/")+1);
		if(menu.trim()=="")menu=".";
		$(".cl-vnavigation a[href='"+menu+"']").parent().addClass("active");
		if($(".cl-vnavigation a[href='"+menu+"']").parents(".sub-menu").length!=0){
			$(".cl-vnavigation a[href='"+menu+"']").parents(".sub-menu").css("display","block");
		}
	};
	</script>