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
  	.red{
  		border: 1px solid #F00;
  	}
  </style>
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
			<div class="page-head">
				<h2>Demo</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">Demo</li>
				  <li class="active">数据表结构</li>
				</ol>
			</div>		
			  <div class="cl-mcont" style="min-height: 100%;">
					<div class="row" style="background: #FFF;margin: 0;" >
						<div class="col-sm-3 col-md-2" style="padding: 0;">
							<div class="page-aside app tree" >
							      <div class="nano nscroller" >
							        <div class="content" style="position: relative;">
							          <div class="header">
							            <button class="navbar-toggle" data-target=".treeview" data-toggle="collapse" type="button">
							              <span class="fa fa-chevron-down"></span>
							            </button>          
							            <h2 class="page-title">数据库</h2>
							          </div>  
							          <ul class="nav nav-list treeview collapse" id="navLeftTreeMap">
							          </ul>
							        </div>
							      </div>
							</div>	
						</div>
						<div class="col-sm-3 col-md-2" style="padding: 0;">
							<div class="block-flat" style="margin: 0;">
								<div class="header">
									<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">数据库信息</font></font></h3>
								</div>
								<div class="content overflow-hidden">
									<ul class="list-unstyled">
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">名称 : <a id="name">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">类型 <a id="type">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">驱动 : <a id="driver">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">用户名 : <a id="username">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">密码 : <a id="password">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">scheme : <a id="scheme">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">host : <a id="host">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">port : <a id="port">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">字符集 : <a id="cahrset">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">字符集合 : <a id="collate">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">编码 : <a id="encoding">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">启用日志 : <a id="log">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">连接池最大数量 : <a id="maxNum">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">连接池初始数量 : <a id="minNum">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">连接池每次增加数量 : <a id="addNum">没有数据</a></font></font></li>
									</ul>					
								</div>
							</div>
						</div>
						<div class="col-sm-6 col-md-8" style="padding: 0;">
							<div class="block-flat" style="margin: 0;">
								<div class="header">
									<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">数据表信息</font></font></h3>
								</div>
								<div class="content overflow-hidden">
									<ul class="list-unstyled">
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">名称 : <a id="tab-name">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">启动时创建 : <a id="tab-isMust">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">映射类 : <a id="tab-cls">没有数据</a></font></font></li>
										<li><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">字符集 : <a id="tab-charset">没有数据</a></font></font></li>
									</ul>					
									<h4>表结构</h4>
										<table class="display compact table-responsive" cellspacing="0" width="100%" id="datatable-tables" >
											<thead>
												<tr>
													<th>name</th>
													<th>type</th>
													<th>PK</th>
													<th>AF</th>
													<th>AI</th>
													<th>unique</th>
													<th>Length</th>
													<th>Size</th>
													<th>Not_Null</th>
													<th>Not_Sign</th>
													<th>Annotations</th>
												</tr>
											</thead>
										</table>	
								</div>
							</div>
						</div>
					</div>
			  </div> 
		 </div>
	</div>
  <script src="js/jquery-2.1.4.min.js"></script>
  <script>
  	$("#pcont").css("max-width",($(window).width()-$('.menu-space .content').width())+"px")
  	$(window).resize(function(e){
  		$("#pcont").css("max-width",($(window).width()-$('.menu-space .content').width())+"px");
  	});
  </script>
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
  <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
  <script type="text/javascript" src="js/jquery.datatables/bootstrap-adapter/js/datatables.js" ></script>
    <script type="text/javascript">
    	var device;
    	var databases = {};
    	var datatables = {};
    	var DataTablesObject;
    		function getDatabase(){
	       	 $.ajax({
		        	type:"get",
		        	url:"DashBoard/DataBase",
		        	async:true,
		        	dataType:"json",
		        	success:function(data){
		        		var data = data.data;
		        		for(var index in data){
		        			databases[data[index].name]=data[index];
		        				console.log(databases)
		        			var userMenu = '<li><label class="tree-toggler nav-header"><i class="fa fa-folder-o"></i>'+data[index].name+'</label><ul class="database nav nav-list tree" id="'+data[index].name+'"><li><a href="#"><i class="fa fa-refresh fa-spin"></i>加载中</a></li></ul></li>'
		        			$("#navLeftTreeMap").append(userMenu)
		        		}
		        	},error:function(err){
		        		console.log(err);
		        	}
	        });
      	}
    		function getDataTabels(databaseName){
	       	 $.ajax({
		        	type:"get",
		        	url:"DashBoard/DataBase/"+databaseName,
		        	async:true,
		        	dataType:"json",
		        	success:function(data){
		        		$("#"+databaseName).html("");
		        		if(data.code==4280){
		        			data = data.data;
		        			for(var index in data){
		        				$("#"+databaseName).append('<li><a class="tables" id="'+data[index].name+'"><i class="fa fa-table"></i> '+(data[index].name.indexOf(".")>1?data[index].name.split(".")[1]:data[index].name)+'</a></li>');
		        				datatables[data[index].name]=data[index];
		        			}
		        		}else{
		        			$("#"+databaseName).html('<li><a ><i class="fa fa-exclamation-circle"></i> '+data.message+'</a></li>')
		        		}
		        		
//		        		if(data.length==0)
//		        			$("#"+databaseName).append('<li><a href="#" ><i class="fa fa-circle-o-notch fa-spin"></i> 没有数据</a></li>')
//		        		for(var index in data){
//		        			if(data[index].deviceArea=="")
//		        				data[index].deviceArea="默认区域"
//		        			if(!areaArray[data[index].deviceArea]==true){
//		        				var deviceMenu = '<li><label class="tree-toggler nav-header"><i class="fa fa-folder-o"></i>'
//		        				+data[index].deviceArea
//		        				+'</label><ul class="nav nav-list tree" id="'
//		        				+databaseName+data[index].deviceArea
//		        				+'"></ul></li>';
//		        				$("#"+databaseName).append(deviceMenu);
//		        				areaArray[data[index].deviceArea]=true
//		        			}
//		        			$("#"+databaseName+data[index].deviceArea).append('<li><a href="#" class="device" id="'+data[index].deviceName+'"><i class="fa fa-desktop"></i> '+data[index].deviceName+'</a></li>')
//		        		}
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
	    					$(".qrcode").html("");
	    					$(".qrcode").each(function(index){
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
      $(document).ready(function(){
        App.init();
        DataTablesObject = $('#datatable-tables').DataTable( {
		        columns: [
		            { "data": "name" },
		            { "data": "type" },
		            { "data": "Primary_Key" },
		            { "data": "Auto_Fill" },
		            { "data": "Auto_Increment" },
		            { "data": "Unique" },
		            { "data": "Length" },
		            { "data": "Size" },
		            { "data": "Not_Null" },
		            { "data": "Not_Sign" },
		            { "data": "Annotations" },
		        ]
		    } );
        getDatabase();
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
	       if($(this).parent().children('ul.tree').hasClass("database")){
	       		var db = $(this).parent().children('ul.tree').attr("id");
	       		getDataTabels(db);
	       		for(var key in databases[db])
	       			$("#"+key).html(databases[db][key])
	       }
	       		
	      });
	      $(document).on("click",".tables",function(e){
	      	tab = $(this).attr("id");
	      	for(var key in datatables[tab])
	       			$("#tab-"+key).html(datatables[tab][key])
	       	var columns = datatables[tab].columns;
	       	DataTablesObject.clear();
	       	for(var index in columns)
	       		DataTablesObject.row.add(columns[index]).draw();
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
