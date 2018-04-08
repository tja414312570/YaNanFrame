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
						<a href="index.jsp"><i class="fa fa-home"></i><span>首页</span></a>
					</li>
					<!--<li>
						<a href="#"><i class="fa  fa-user"></i><span>账号管理</span></a>
						<ul class="sub-menu">
							<li>
								<a href="account-my.jsp">个人信息</a>
							</li>
							<li>
								<a href="account.jsp">账号信息</a>
							</li>
							<li>
								<a href="account-manager.jsp">账号管理</a>
							</li>
							<li>
								<a href="account-devices.jsp">账号下设备管理</a>
							</li>
						</ul>
					</li>-->
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>Demo</span></a>
						<ul class="sub-menu">
							<li>
								<a href="demo-servlet.jsp">查看Servlet</a>
							</li>
							<li>
								<a href="demo-database.jsp">查看数据库</a>
							</li>
							<li>
								<a href="demo-tabels.jsp">查看数据表</a>
							</li>
							<li>
								<a href="demo-RTDT.jsp">RTDT测试</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>开发文档</span></a>
						<ul class="sub-menu">
							<li>
								<a href="tutorial-core-configure.jsp">核心配置</a>
							</li>
							<li>
								<a href="tutorial-framework.jsp">框架结构</a>
							</li>
							<li>
								<a href="tutorial-servlet.jsp">servlet</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#"><i class="fa  fa-user"></i><span>Hibernate</span></a>
						<ul class="sub-menu">
							<li>
								<a href="tutorial-hibernate-noun.jsp">名词解释</a>
							</li>
							<li>
								<a href="tutorial-hibernate.jsp">数据库配置</a>
							</li>
							<li>
								<a href="tutorial-hibernate-use.jsp">数据库的使用</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="consume.jsp"><i class="fa fa-credit-card"></i><span>消费管理</span></a>
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
		$(".cl-vnavigation a[href='"+menu+"']").parent().addClass("active");
		if($(".cl-vnavigation a[href='"+menu+"']").parents(".sub-menu").length!=0){
			$(".cl-vnavigation a[href='"+menu+"']").parents(".sub-menu").css("display","block");
		}
	//加载账号数据
		$.ajax({
			type:"get",
			url:"getMyInfo.do",
			async:true,
			dataType:"json",
			success:function(data){
				if(data.code==4280){
					$(".account").html(data.data.account);
					$(".status").html(data.data.status==4280?"正常":"其它状态");
					$(".roles").html(data.data.roles);
				}
			},
			error:function(err){
				console.log(err);
			}
		});
	};
	</script>