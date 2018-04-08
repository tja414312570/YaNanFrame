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
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
			<div class="page-head">
				<h2>账号管理</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">账号管理</li>
				</ol>
			</div>		
		  <div class="cl-mcont">
			<div class="row">
				<div class="col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3>账号管理</h3>
						</div>
						<div class="content">
							<div class="table-responsive">
								<table class="table table-bordered" id="datatable-icons" >
									<thead>
										<tr>
											<th>用户名</th>
											<th>账号标示</th>
											<th>用户状态</th>
											<th>用户权限</th>
											<th>创建日期</th>
											<th>备注</th>
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
  <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript">
    //Add dataTable Functions
      var functions = $('<div class="btn-group"><button class="btn btn-default btn-xs" type="button">Actions</button><button data-toggle="dropdown" class="btn btn-xs btn-primary dropdown-toggle" type="button"><span class="caret"></span><span class="sr-only">Toggle Dropdown</span></button><ul role="menu" class="dropdown-menu pull-right"><li><a href="#">Edit</a></li><li><a href="#">Copy</a></li><li><a href="#">Details</a></li><li class="divider"></li><li><a href="#">Remove</a></li></ul></div>');
      $("#datatable tbody tr td:last-child").each(function(){
        $(this).html("");
        functions.clone().appendTo(this);
      });
      
      //Add dataTable Icons
      var functions = $('<a class="btn btn-default btn-xs" href="#" data-original-title="Open" data-toggle="tooltip"><i class="fa fa-file"></i></a> <a class="btn btn-primary btn-xs" href="#" data-original-title="Edit" data-toggle="tooltip"><i class="fa fa-pencil"></i></a> <a class="btn btn-danger btn-xs" href="#" data-original-title="Remove" data-toggle="tooltip"><i class="fa fa-times"></i></a>');
      $("#datatable-icons tbody tr td:last-child").each(function(){
        $(this).html("");
        functions.clone().appendTo(this);
      });
      
      $(document).ready(function(){
        //initialize the javascript
        App.init();
        App.dataTables();
      });
       	 //datatable
			    	var amTable;
			    	var amEditor; 
			    	var auditEditor;
			    	$(document).ready(function() {
			    		 auditEditor = new $.fn.dataTable.Editor( {
					     //.   ajax: "ufo_member/addUfoMember.do",
					        ajax: function ( method, url, data, success, error ) {
					        	var dataDes;
					        	for(var str in data.data)
					        		dataDes=data.data[str]
					        		dataDes.action=data.action;
								        $.ajax( {
								            type: "post",
								            url:  "rentAudit.do",
								            data: dataDes,
								            dataType: "json",
								            success: function (json) {
								                success( json);
								            },
								            error: function (xhr, error, thrown) {
								                error( xhr, error, thrown );
								            }
								        } )},
					        table: "#AllMember",
					        idSrc:  'BUID',
					        fields: [{
					                label: "产品名称(ProductName):",
					                name: "ProductName"
					            },{
					                label: "产品键(ProductKey):",
					                name: "ProductKey"
					            },{
					                label: "产品描述(ProductDesc):",
					                name: "stauts",
					                type:"textarea"
					            },{
					                label: "创建者用户ID(CreateUserId):",
					                name: "CreateUserId",
					            }, {
					                label: "分类ID(CatId):",
					                name: "CatId",
					            },{
					                label: "表单源(FromSource):",
					                name: "FromSource",
					            }
					        ],
					         i18n: {
					         	 create: {
					                button: "添加产品信息",
					                title:  "添加/创建产品信息",
					                submit: "提交"
					            },
					            edit: {
					                button: "修改产品信息",
					                title:  "修改产品信息",
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
			    			amTable = $('#datatable-icons').dataTable({
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
						        "sAjaxSource": "getAllAdmin.do",
						        "sAjaxDataProp": "data",
						        "columns": [
						 	 	    { "data": "account" },
						            { "data": "AUID" },
						          	{ "data": "status" },
						            { "data": "roles" },
						            { "data": "createDate"},
						            { "data": "note" },
						        ],
						        "order": [[ 3, "desc" ]],
						        select: {
							            style: 'multi',
							        },
						        buttons: [
						       	    { extend: "create", text:"添加产品",  editor: auditEditor },
						            { extend: "edit", text:"修改",  editor: amEditor },
						            { extend: "remove",text:"删除", editor: amEditor },
						            { extend: "selectRows",text:"选择行"},
						            { extend:"selectColumns",text:"选择列"},
						            { extend:"selectCells",text:"选择单元格"},
						            { extend:"selectNone",text:"反选所有"},
						            { extend:"copyHtml5",text:"复制"},
							        { extend:'excelHtml5',title: '娱乐机器人产品信息'},
							        { extend:'csvHtml5', title: '娱乐机器人产品信息'},
							        { extend:"pdfHtml5", download: 'open'},
							        { extend:"print",text:"打印" , exportOptions: {columns: ':visible'},title:"娱乐机器人产品信息"},
							        { extend: 'print',text: '打印已选择',exportOptions: {modifier: {selected: true}},title:"娱乐机器人产品信息"},
							        { extend:"colvis",text:"表头选择"}
						        ],
						         
					       
					   	 });
					   	 if(localStorage.root=="true"){
					   	 	$(".root").addClass("xactive");
					   	 }
			    		amEditor.on( 'preSubmit', function ( e, o, action ) {
					        if ( action !== 'remove' ) {
					            // ... additional validation rules
					 
					            // If any error was reported, cancel the submission so it can be corrected
					            if ( this.inError() ) {
					                return false;
					            }
					        }
					    } );
						 //   $(".dataTables_length").append("<div class='btn btn-sm btn-default' id='addRow'>添加</div>");
					} );
				    $('.btn-setting').click(function (e) {
				        e.preventDefault();
				        $('#myModal').modal('show');
				    });
    </script>
    <script src="js/behaviour/voice-commands.js"></script>
    <script src="js/bootstrap/dist/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.pie.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.resize.js"></script>
	<script type="text/javascript" src="js/jquery.flot/jquery.flot.labels.js"></script>
  </body>

<!-- Mirrored from condorthemes.com/cleanzone/ by HTTrack Website Copier/3.x [XR&CO'2013], Mon, 31 Mar 2014 14:32:27 GMT -->
</html>
