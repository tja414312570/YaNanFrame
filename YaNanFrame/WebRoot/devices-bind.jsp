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
				<h2>设备绑定</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">硬件管理</li>
				  <li class="active">设备绑定</li>
				</ol>
			</div>		
		  <div class="cl-mcont">
			<div class="row">
				<div class="col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3>设备绑定</h3>
						</div>
						<div class="content">
							<p>
								<h4><b id="tip"></b>上次同步时间，<a id="refresh">立即同步<i class="fa fa-refresh"></i></a></h4>
							</p>
							<div class="table-responsive">
								<table class="display table table-bordered bootstrap-datatable datatable nowrap" cellspacing="0" width="100%" id="datatable-icons" >
									<thead>
										<tr>
											<th>设备名称</th>
											<th>绑定状态</th>
											<th>设备地址</th>
											<th>设备区域</th>
											<th>绑定时间</th>
											<th>设备单价</th>
											<th>设备描述</th>
											<th>ICCID</th>
											<th>设备备注</th>
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
    	var amTable;
    	var auditEditor;
      $(document).ready(function(){
        App.init();
        App.dataTables();
      });
    	$(document).ready(function() {
    		 auditEditor = new $.fn.dataTable.Editor( {
		        ajax: function ( method, url, data, success, error ) {
		        	var dataDes;
		        	for(var str in data.data)
		        		dataDes=data.data[str]
		        		dataDes.action=data.action;
					        $.ajax( {
					            type: "post",
					            url:  "updateBindDevices.do",
					            data: dataDes,
					            dataType: "json",
					            success: function (json) {
					                success( json);
					            },
					            error: function (xhr, error, thrown) {
					                error( xhr, error, thrown );
					            }
					        } )},
		        table: "#datatable-icons",
		        idSrc:  "deviceId",
		        fields: [{
		                label:"设备标示(deviceName): ",
		                name: "deviceName"
		            },{
		                label:"设备单价(devicePrice)[限制0.01-100.00]: ",
		                name: "price"
		            },{
		                label:"设备地址(DeviceAddress): ",
		                name: "deviceAddress",
		            },{
		                label:"设备区域(DeviceArea): ",
		                name: "deviceArea",
		            },{
		                label:"设备ICCD(DeviceICCID): ",
		                name: "ICCID",
		            },{
		                label:"设备描述(DeviceDescription): ",
		                name: "description",
		            },{
		                label:"设备备注(DeviceNote): ",
		                name: "note",
		                type:"textarea"
		            }
		        ],
		         i18n: {
		         	 create: {
		                button: "绑定新的设备",
		                title:  "绑定新的设备",
		                submit: "提交"
		            },
		            edit: {
		                button: "修改设备信息",
		                title:  "修改设备信息",
		                submit: "提交更改"
		            },
		            remove: {
		                button: "删除",
		                title:  "删除公司信息",
		                submit: "确认",
		                confirm: {
		                    _: "你确定要删除选择的 %d 条吗?",
       						 1: "你确定要删除选择的1条数据吗?"
		                }
		               },
		            error: {
		                system: "出现错误"
		            },
		            datetime: {
		                previous: 'previous',
		                next:     'next',
		                months:   [ 'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre' ],
		                weekdays: [ 'Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam' ]
		            }
		          }
		    } );
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
			        "ajax": "queryBindDevices.do",
			        "sAjaxDataProp": "data",
			        "columns": [
			            { "data": "deviceName" },
			            { "data": "bindStatus" },
			          	{ "data": "deviceAddress" },
			            { "data": "deviceArea" },
			            { "data": "bindDate" },
			            { "data": "price"},
			            { "data": "description"},
			            { "data": "ICCID"},
			            { "data": "note" },
			        ],
			        "order": [[ 0, "desc" ]],
			        columnDefs:[{
				    	"render":function(data,type,row){
				    		if(data==4280)return "<a style='color:#92278f'>设备已绑定</a>";
				    		if(data==4281)return "<a style='color:#080'>设备在线</a>";
				    		if(data==4282)return "<a style='color:#F00'>设备已离线</a>";
				    		if(data==-1)return "<a style='color:#888'>设备已删除</a>";
				    		return "未知";
				    	},
				    	"targets":1,
				    }],
			        select: {
				            style: 'multi',
				        },
			        buttons: [
			       	    { extend: "create", text:"添加设备",  editor: auditEditor },
			            { extend: "edit", text:"修改",  editor: auditEditor },
			            { extend: "remove",text:"删除", editor: auditEditor },
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
