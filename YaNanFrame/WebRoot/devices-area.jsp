<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="ch">
<head>
	<jsp:include page="include/header.jsp"></jsp:include>
	<meta charset="utf-8" />
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,400italic,700,800' rel='stylesheet' type='text/css'>
	<link href='http://fonts.googleapis.com/css?family=Raleway:100' rel='stylesheet' type='text/css'>
 	<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300,700' rel='stylesheet' type='text/css'>
    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" type="text/css" href="http://www.jq22.com/jquery/bootstrap-3.3.4.css">
	<link rel="stylesheet" type="text/css" href="http://www.jq22.com/jquery/font-awesome.4.6.0.css">
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <![endif]-->
	<link rel="stylesheet" type="text/css" href="js/jquery.gritter/css/jquery.gritter.css" />
  	<link rel="stylesheet" type="text/css" href="js/jquery.nanoscroller/nanoscroller.css" />
  	<link rel="stylesheet" type="text/css" href="js/jquery.easypiechart/jquery.easy-pie-chart.css" />
	<link rel="stylesheet" type="text/css" href="js/bootstrap.switch/bootstrap-switch.css" />
	<link rel="stylesheet" type="text/css" href="js/bootstrap.datetimepicker/css/bootstrap-datetimepicker.min.css" />
	<link rel="stylesheet" type="text/css" href="js/jquery.select2/select2.css" />
	<link rel="stylesheet" type="text/css" href="js/bootstrap.slider/css/slider.css" />
	<link href="css/jquery.dataTables.min.css" rel="stylesheet"/> 
    <link href="css/buttons.dataTables.min.css" rel="stylesheet"/>
    <link href="css/select.dataTables.min.css" rel="stylesheet" />
    <link href="css/editor.dataTables.min.css"rel="stylesheet" />
    <link rel="stylesheet" href="css/keyTable.dataTables.min.css" />
  <!-- Custom styles for this template -->
  <link href="css/style.css" rel="stylesheet" />
  <style>
  	#tip{
  		color: #F00;
  	}
  	.col-sm-6{
  		padding: 0;
  	}
  	.device-way{
  		border: 1px solid #e2e2e2;
  	}
  	.device-way h3{
  		text-align: center;
  	}
  	.qrcode{
  		text-align: center;
  	}
  	.test-btn{
  		margin: 0 auto;
  		display: block;
  	}
  </style>
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
			<div class="page-head">
				<h2>区域管理</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">设备管理</li>
				  <li class="active">区域管理</li>
				</ol>
			</div>		
			  <div class="cl-mcont" style="min-height: 100%;padding-left: 320px;">
		  			<div class="page-aside app tree" style="position: absolute;left: 13px;">
					      <div class="nano nscroller" >
					        <div class="content">
					          <div class="header">
					            <button class="navbar-toggle" data-target=".treeview" data-toggle="collapse" type="button">
					              <span class="fa fa-chevron-down"></span>
					            </button>          
					            <h2 class="page-title">区域管理</h2>
					            <p class="description">区域管理描述</p>
					          </div>  
					          <ul class="nav nav-list treeview collapse" id="navLeftTreeMap">
					          </ul>
					        </div>
					      </div>
					</div>	
					<div class="row" style="background: #FFF;margin: 0;" >
						<div class="col-sm-6 col-md-6">
							<div class="block-flat" style="margin: 0;">
								<div class="header">
									<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备信息</font></font></h3>
								</div>
								<div class="content overflow-hidden">
									<ul class="list-unstyled">
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备名称 : <a id="deviceName">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备ID : <a id="deviceId">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备状态 : <a id="deviceStatus">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">绑定账户 : <a id="AUID">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备地址 : <a id="deviceAddress">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备区域 : <a id="deviceArea">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备SIM卡号 : <a id="ICCID">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备备注 : <a id="note">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">绑定状态 : <a id="bindStatus">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">绑定时间 : <a id="bindDate">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备单价 : <a id="price">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备描述 : <a id="description">没有数据</a></font></font></li>
									</ul>					
								</div>
							</div>
						</div>
						<div class="col-sm-6 col-md-6">
							<div class="block-flat" style="margin: 0;">
								<div class="header">
									<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">设备各通道情况</font></font></h3>
								</div>
								<div class="content">
									<div class="row">
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 1</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 2</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 3</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 4</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 5</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 6</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 7</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
										<div class="col-sm-4 col-md-4 col-lg-4">
											<div class="device-way">
												<div class="header">							
													<h3>通道 8</h3>
												</div>
												<div class="content">
													<p><b>通道状态：<a class="deviceStatus">未知</a></b></p>
													<p><b>通道二维码：</b></p>
													<div class="qrcode">请先选择设备</div>
													<div><button class="btn test-btn" disabled="disabled">通道测试</button></div>
												</div>
											</div>				
										</div>
									</div>
						        </div>
							</div>
						</div>
					</div>
			  </div> 
		 </div>
	</div>
  <script src="js/jquery-2.1.4.min.js"></script>
  <script type="text/javascript" src="js/jquery.gritter/js/jquery.gritter.js"></script>
  <script type="text/javascript" src="js/jquery.nanoscroller/jquery.nanoscroller.js"></script>
  <script type="text/javascript" src="js/behaviour/general.js"></script>
  <script src="js/jquery.ui/jquery-ui.js" type="text/javascript"></script>
  <script type="text/javascript" src="js/jquery.sparkline/jquery.sparkline.min.js"></script>
  <script type="text/javascript" src="js/jquery.easypiechart/jquery.easy-pie-chart.js"></script>
  <script type="text/javascript" src="js/jquery.nestable/jquery.nestable.js"></script>
  <script type="text/javascript" src="js/bootstrap.switch/bootstrap-switch.min.js"></script>
  <script type="text/javascript" src="js/bootstrap.datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
  <script src="js/jquery.select2/select2.min.js" type="text/javascript"></script>
  <script src="js/skycons/skycons.js" type="text/javascript"></script>
  <script src="js/bootstrap.slider/js/bootstrap-slider.js" type="text/javascript"></script>
  <script type="text/javascript" src="js/jquery.qrcode.js" ></script>
  <script type="text/javascript" src="js/qrcode.js" ></script>
    <script type="text/javascript">
    	var device;
    		function getAreaUser(){
	       	 $.ajax({
		        	type:"get",
		        	url:"getAreaUser.do",
		        	async:true,
		        	dataType:"json",
		        	success:function(data){
		        		for(var index in data){
		        			var userMenu = '<li><label class="tree-toggler nav-header"><i class="fa fa-folder-o"></i>'+data[index].account+'</label><ul class="account nav nav-list tree" id="'+data[index].AUID+'"><li><a href="#"><i class="fa fa-refresh fa-spin"></i>加载中</a></li></ul></li>'
		        			$("#navLeftTreeMap").append(userMenu)
		        		}
		        	},error:function(err){
		        		console.log(err);
		        	}
	        });
      	}
    		function getUserBindDevices(AUID){
	       	 $.ajax({
		        	type:"get",
		        	url:"getUserBindDevices.do?AUID="+AUID,
		        	async:true,
		        	dataType:"json",
		        	success:function(data){
		        		var areaArray={};
		        		$("#"+AUID).html("");
		        		if(data.length==0)
		        			$("#"+AUID).append('<li><a href="#" ><i class="fa fa-circle-o-notch fa-spin"></i> 没有数据</a></li>')
		        		for(var index in data){
		        			if(data[index].deviceArea=="")
		        				data[index].deviceArea="默认区域"
		        			if(!areaArray[data[index].deviceArea]==true){
		        				var deviceMenu = '<li><label class="tree-toggler nav-header"><i class="fa fa-folder-o"></i>'
		        				+data[index].deviceArea
		        				+'</label><ul class="nav nav-list tree" id="'
		        				+AUID+data[index].deviceArea
		        				+'"></ul></li>';
		        				$("#"+AUID).append(deviceMenu);
		        				areaArray[data[index].deviceArea]=true
		        			}
		        			$("#"+AUID+data[index].deviceArea).append('<li><a href="#" class="device" id="'+data[index].deviceName+'"><i class="fa fa-desktop"></i> '+data[index].deviceName+'</a></li>')
		        		}
		        	},error:function(err){
		        		console.log(err);
		        	}
	        });
      	}
    		function getDeviceStatus(statusNum){
    			if(statusNum==4280)return "<a style='color:#92278f'>设备未激活</a>";
	    		if(statusNum==4281)return "<a style='color:#080'>设备在线</a>";
	    		if(statusNum==4282)return "<a style='color:#F00'>设备已离线</a>";
	    		if(statusNum==-1)return "<a style='color:#888'>设备已删除</a>";
	    		return "未知";
    		}
    		function getDeviceBindStatus(statusNum){
    			if(statusNum==4280)return "<a style='color:#080'>设备已绑定</a>";
	    		if(statusNum==4281)return "<a style='color:#080'>设备已解绑</a>";
	    		if(statusNum==4282)return "<a style='color:#F00'>设备已离线</a>";
	    		if(statusNum==-1)return "<a style='color:#888'>设备已删除</a>";
	    		return "未知";
    		}
    		function getDeviceByDeviceName(deviceName){
    			$.ajax({
    				type:"get",
    				url:"queryDeviceByDeviceName.do?deviceName="+deviceName,
    				async:true,
    				dataType:'json',
    				success:function(data){
	    				if(data.length>0){
	    					for(var key in data[0]){
	    						if(key=="deviceStatus"){
	    							$("#"+key).html(getDeviceStatus(data[0][key]));
	    							continue;
	    						}
	    						if(key=="bindStatus"){
	    							$("#"+key).html(getDeviceBindStatus(data[0][key]));
	    							continue;
	    						}
	    						if(data[0][key]!=""&&data[0][key]!=undefined)
	    							$("#"+key).html(data[0][key]);
	    					}
	    					device = data[0];
	    					console.log($(".deviceStatus").html())
	    					$(".deviceStatus").html(getDeviceStatus(data[0].deviceStatus))
	    				//	if(data[0].deviceStatus==4282)
	    						$(".test-btn").removeAttr("disabled");
	    					console.log("http://39.104.96.185/YLROBOT/app/?deviceName="+data[0].deviceName)
	    					$(".qrcode").html("");
	    					$(".qrcode").each(function(index){
	    						console.log("http://39.104.96.185/YLROBOT/app/?deviceName="+data[0].deviceName+"&passageway="+(index+1));
	    						$(this).qrcode({
								width: 120, //宽度
								height:120, //高度
								text: "http://39.104.96.185/YLROBOT/app/?deviceName="+data[0].deviceName+"&passageway="+(index+1)
							});
	    					})
	    				}
    				},error:function(err){
    					console.log(err)
    				}
    			});
    		}
    	  $(document).on("click",".test-btn:not(.btn[disabled])",function(e){
    	  	console.log("deviceTest.do?deviceName="+device.deviceName+"&passageway="+$(this).index());
    	  	$.ajax({
    	  		type:"get",
    	  		url:"deviceTest.do?deviceName="+device.deviceName+"&passageway="+$(this).index(),
    	  		async:true,
    	  		dataType:"json",
    	  		success:function(data){
    	  			alert(JSON.stringify(data));
    	  		},error:function(err){
    	  			console.log(err);
    	  		}
    	  	});
    	  });
      $(document).ready(function(){
        App.init();
        //获取用户列表
          getAreaUser();
	      $(document).on("click","label.tree-toggler",function (e) {
	      	e.stopPropagation();
	      	e.preventDefault();
	        var icon = $(this).children(".fa");
	          if(icon.hasClass("fa-folder-o")){
	            icon.removeClass("fa-folder-o").addClass("fa-folder-open-o");
	          }else{
	            icon.removeClass("fa-folder-open-o").addClass("fa-folder-o");
	          }        
	        $(this).parent().children('ul.tree').toggle(300,function(){
	          $(this).parent().toggleClass("open");
	          $(".tree .nscroller").nanoScroller({ preventPageScrolling: true });
	        });
	       if($(this).parent().children('ul.tree').hasClass("account"))
	       		getUserBindDevices($(this).parent().children('ul.tree').attr("id"));
	      });
	      $(document).on("click",".device",function(e){
	      	getDeviceByDeviceName($(this).attr("id"));
	      });
    });
  </script>

    </script>
    <script src="js/behaviour/voice-commands.js"></script>
    <script src="js/bootstrap/dist/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.pie.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.resize.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.labels.js"></script>
  </body>

</html>
