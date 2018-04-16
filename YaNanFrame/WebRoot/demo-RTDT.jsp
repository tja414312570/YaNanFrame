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
	<link rel="stylesheet" href="jquery.terminal/css/jquery.terminal-1.12.1.css" />
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
				<h2>Demo</h2>
				<ol class="breadcrumb">
				  <li><a href="index.jsp">主页</a></li>
				  <li class="active">Demo</li>
				  <li class="active">RTDT</li>
				</ol>
			</div>		
			  <div class="cl-mcont" filter="style" style="height: $<$(window).height()-$('.cl-mcont').offset().top>px">
			        <pre contenteditable="true" style="width: 100%;height: 100%;padding: 1em;background: #fefefe;" id="cmd" ></pre>
			  </div> 
		 </div>
	</div>
  <script src="js/jquery-2.1.4.min.js"></script>
  <script type="text/javascript" src="js/jquery.gritter/js/jquery.gritter.js"></script>
  <script type="text/javascript" src="js/jquery.nanoscroller/jquery.nanoscroller.js"></script>
  <script type="text/javascript" src="js/behaviour/general.js"></script>
  <script type="text/javascript" src="js/app.js" ></script>
  <script src="js/jquery.ui/jquery-ui.js" type="text/javascript"></script>
  <script type="text/javascript" src="js/jquery.sparkline/jquery.sparkline.min.js"></script>
  <script type="text/javascript" src="js/jquery.easypiechart/jquery.easy-pie-chart.js"></script>
  <script type="text/javascript" src="js/jquery.nestable/jquery.nestable.js"></script>
  <script type="text/javascript" src="js/bootstrap.switch/bootstrap-switch.min.js"></script>
  <script type="text/javascript" src="js/bootstrap.datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
  <script src="js/jquery.select2/select2.min.js" type="text/javascript"></script>
  <script src="js/skycons/skycons.js" type="text/javascript"></script>
  <script src="js/bootstrap.slider/js/bootstrap-slider.js" type="text/javascript"></script>
  <script src="js/jquery.codemirror/lib/codemirror.js"></script>
  <script src="js/jquery.codemirror/mode/xml/xml.js"></script>
  <script src="js/jquery.codemirror/mode/css/css.js"></script>
  <script src="js/jquery.codemirror/mode/htmlmixed/htmlmixed.js"></script>
  <script src="js/jquery.codemirror/addon/edit/matchbrackets.js"></script>
  <script type="text/javascript" src="jquery.terminal/js/jquery.terminal-1.12.1.min.js" ></script>
  <script type="text/javascript" src="jquery.terminal/js/jquery.mousewheel-min.js" ></script>
  <script type="text/javascript" src="RTDT/conf.js" ></script>
  <script type="text/javascript" src="RTDT/RTDT.js" ></script>
    <script type="text/javascript">
      $(document).ready(function(){
        App.init();
        
        var terminal = $('#cmd').terminal(function(command) {
	        if (command !== '') {
	        	terminal.pause();
	            try {
	                RTDT.Req({
					action:"accept",
					data: {content:command},
					success:function(data){
						terminal.echo(data);
						terminal.resume();
					},
					error:function(err){
						terminal.error("server error");
						terminal.error("reason:"+err.data);
						terminal.resume();
					}
				});
	            } catch(e) {
	                this.error(new String(e));
	            }
	        } else {
	           this.echo('');
	        }
		    }, {
	        greetings: 'Welcome to YaNan Frame',
	        name: 'js_demo',
	        height: 800,
	        prompt: '> '
	    });
	    	terminal.pause();
      	terminal.echo("connecting", {raw: false});
      	var timer = setInterval(function(e){
      		terminal.echo("*")
      		RTDT.Bind({
				action:"MyRAction",
				bind:function(data){
					clearInterval(timer);
					terminal.echo("\nconnecting success\n");
					terminal.resume();
				},
				notify:function(data){
					clearInterval(timer);
					terminal.echo(data);
					terminal.resume();
				},
				error:function(err){
					terminal.error("connecting failed");
					terminal.error("reason:"+err.data);
					clearInterval(timer);
					terminal.resume();
				}
			})
      	},1000);
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
