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
	<link rel="stylesheet" type="text/css" href="js/intro.js/introjs.css" />
  <!-- Custom styles for this template -->
  <link href="css/style.css" rel="stylesheet" />
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
		  <div class="cl-mcont">
			<div class="row">
				<div class="col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">核心配置</font></font></h3>
						</div>
						<div class="content">
							<h3>核心配置</h3>
						</div>
					</div>				
				</div>
			</div>
		  </div>
		</div> 
		
	</div>

  <script type="text/javascript" src="js/jquery-2.1.4.min.js" ></script>
  <script type="text/javascript" src="js/app.js" ></script>
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
  <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript">
//  	var
//      History = window.History, // Note: We are using a capital H instead of a lower h
//      State = History.getState(),
//      $log = $('#log');
//	    //bind to State Change
//	    History.Adapter.bind(window, 'statechange', function () { // Note: We are using statechange instead of popstate
//	        var State = History.getState(); // Note: We are using History.getState() instead of event.state
//	        $.ajax({
//	            url: State.url,
//	            success: function (msg) {
//	                $('#pcont').html($(msg).find('#pcont').html());
//	                $('#loading').remove();
//	                $('#pcont').fadeIn();
//	                var newTitle = $(msg).filter('title').text();
//	                $('title').text(newTitle);
//	                docReady();
//	            }
//	        });
//	    });
//	    $('a.ajax-link').click(function (e) {
//	       // if (e.which != 1 || !$('#is-ajax').prop('checked') || $(this).parent().hasClass('active')) return;
//	        e.preventDefault();
//	        $('.sidebar-nav').removeClass('active');
//	        $('.navbar-toggle').removeClass('active');
//	        $('#loading').remove();
//	        $('#pcont').fadeOut().parent().append('<div id="loading" class="center">加载中...<div class="center"></div></div>');
//	        var $clink = $(this);
//	        History.pushState(null, null, $clink.attr('href'));
//	        $('ul.main-menu li.active').removeClass('active');
//	        $clink.parent('li').addClass('active');
//	    });
      $(document).ready(function(){
        //initialize the javascript
        App.init();
        App.dashBoard();        
       //   introJs().setOption('showBullets', false).start();
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
