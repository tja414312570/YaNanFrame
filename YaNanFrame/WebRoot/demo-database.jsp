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
  </style>
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
			<div class="page-head">
				<h2>Demo展示</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">Demo</li>
				  <li class="active">获取数据库信息</li>
				</ol>
			</div>		
		  <div class="cl-mcont">
			<div class="row">
				<div class="col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3>获取数据库信息</h3>
						</div>
						<div class="content">
							<p>
								<h4><b id="tip">接口：getDataBase.do</b></h4>
							</p>
							<div class="table-responsive">
								<table class="display table table-bordered bootstrap-datatable datatable nowrap" cellspacing="0" width="100%" id="datatable-icons" >
									<thead>
										<tr>
											<th>name</th>
											<th>driver</th>
											<th>type</th>
											<th>scheme</th>
											<th>host</th>
											<th>port</th>
											<th>username</th>
											<th>password</th>
											<th>cahrset</th>
											<th>collate</th>
											<th>encoding</th>
											<th>log</th>
											<th>minNum</th>
											<th>maxNum</th>
											<th>addNum</th>
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
	<script src="js/dataTables.buttons.min.js"></script>
	<script type="text/javascript" src="js/dataTables.select.js" ></script>
	<script type="text/javascript" src="js/dataTables.autoFill.min.js" ></script>
	<script type="text/javascript" src="js/dataTables.editor.min.js" ></script>
	<script type="text/javascript" src="js/dataTables.responsive.js" ></script>
	<script type="text/javascript" src="js/jszip.min.js" ></script>
	<script type="text/javascript" src="js/pdfmake.min.js" ></script>
	<script type="text/javascript" src="js/vfs_fonts.js" ></script>
	<script type="text/javascript" src="js/buttons.html5.min.js" ></script>
	<script type="text/javascript" src="js/buttons.print.min.js" ></script>
	<script type="text/javascript" src="js/buttons.colVis.min.js" ></script>
    <script type="text/javascript">
      $(document).ready(function(){
        App.init();
      });
    	$(document).ready(function() {
    			amTable = $('#datatable-icons').DataTable({
    				dom: "Bflrtip",
    				autoFill: true,
    				bAutoWidth:true,
    				responsive: true,
    				stateSave: true,
			        "sPaginationType": "bootstrap",
			        "oLanguage": {
			        select: {
			                rows: {
			                    _: "你已经选中了 %d 条数据",
			                    0: ""
			                }
			            },
			            "sLengthMenu": "显示 _MENU_ 条每页",
			            "zeroRecords": "没有任何数据 ",
			           // "sInfo": "当前 第 _PAGE_ 页，共 _PAGES_ 页",
			           
			            "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据", 
			            "sFirst": "首页",  
			            "sProcessing": "<img src='images/ajax-loader.gif' />",
			            "sSearch":"搜索"  
			        },
			        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
			        "ajax": "DashBoard/DataBase",
			        "sAjaxDataProp": "data",
			        "columns": [
			            { "data": "name" },
			            { "data": "driver" },
			          	{ "data": "type" },
			            { "data": "scheme" },
			            { "data": "host" },
			            { "data": "port"},
			            { "data": "username"},
			            { "data": "password" },
			            { "data": "cahrset" },
			            { "data": "collate" },
			            { "data": "encoding" },
			            { "data": "log" },
			            { "data": "minNum" },
			            { "data": "maxNum" },
			            { "data": "addNum" }
			        ],
			        "order": [[ 0, "desc" ]],
			        select: {
				            style: 'multi',
				        },
			        buttons: [
			            { extend: "selectRows",text:"选择行"},
			            { extend:"selectColumns",text:"选择列"},
			            { extend:"selectCells",text:"选择单元格"},
			            { extend:"selectNone",text:"反选所有"},
			            { extend:"copyHtml5",text:"复制"},
				        { extend:'excelHtml5',title: '娱乐机器人设备信息'},
				        { extend:'csvHtml5', title: '娱乐机器人设备信息'},
				        { extend:"pdfHtml5", download: 'open'},
				        { extend:"print",text:"打印" , exportOptions: {columns: ':visible'},title:"娱乐机器人设备信息"},
				        { extend: 'print',text: '打印已选择',exportOptions: {modifier: {selected: true}},title:"娱乐机器人设备信息"},
				        { extend:"colvis",text:"表头选择"}
			        ],
		   	 });
		   	 })
    </script>
    <script src="js/behaviour/voice-commands.js"></script>
    <script src="js/bootstrap/dist/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.pie.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.resize.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.labels.js"></script>
  </body>

</html>
