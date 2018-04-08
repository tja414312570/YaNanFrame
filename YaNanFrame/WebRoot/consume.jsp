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
			<div class="stats_bar">
				<div class="butpro butstyle" data-step="2" data-intro="<strong>Beautiful Elements</strong> <br/> If you are looking for a different UI, this is for you!.">
					<div class="sub"><h2>产品数量</h2><span id="total_clientes">0</span></div>
					<div class="stat"><span class="spk1"><canvas style="display: inline-block; width: 74px; height: 16px; vertical-align: top;" width="74" height="16"></canvas></span></div>
				</div>
				<div class="butpro butstyle">
					<div class="sub"><h2>设备数量</h2><span>0</span></div>
					<div class="stat"><span class="up"> 0%</span></div>
				</div>
				<div class="butpro butstyle">
					<div class="sub"><h2>访问次数</h2><span>0</span></div>
					<div class="stat"><span class="down"> 0%</span></div>
				</div>	
				<div class="butpro butstyle">
					<div class="sub"><h2>设备在线</h2><span>0</span></div>
					<div class="stat"><span class="equal"> 0%</span></div>
				</div>	
				<div class="butpro butstyle">
					<div class="sub"><h2>设备使用</h2><span>0%</span></div>
					<div class="stat"><span class="spk2"></span></div>
				</div>
				<div class="butpro butstyle">
					<div class="sub"><h2>设备离线</h2><span>184</span></div>
					<div class="stat"><span class="spk3"></span></div>
				</div>	

			</div>

			<div class="row dash-cols">
			
				<div class="col-sm-6 col-md-6">
					<div class="block">
						<div class="header no-border">
							<h2>用户付费统计</h2>
						</div>
						<div class="content blue-chart"  data-step="3" data-intro="<strong>Unique Styled Plugins</strong> <br/> We put love in every detail to give a great user experience!.">
							<div id="site_statistics" style="height:180px;"></div>
						</div>
						<div class="content">
							<div class="stat-data">
								<div class="stat-blue">
									<h2>1,254</h2>
									<span>总付费</span>
								</div>
							</div>
							<div class="stat-data">
								<div class="stat-number">
									<div><h2>83</h2></div>
									<div>本周使用<br /><span>(Daily)</span></div>
								</div>
								<div class="stat-number">
									<div><h2>57</h2></div>
									<div>当天使用<br /><span>(Daily)</span></div>
								</div>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>	
				
				<div class="col-sm-6 col-md-6">
					<div class="block">
						<div class="header no-border">
							<h2>设备状态监控</h2>
						</div>
						<div class="content red-chart">
							<div id="site_statistics2" style="height:152px;"></div>
						</div>
						<div class="content no-padding">
							<table class="red">
								<thead>
									<tr>
										<th>Name</th>
										<th class="right"><span>25%</span>C.P.U</th>
										<th class="right"><span>29%</span>Memory</th>
										<th class="right"><span>16%</span>Disc</th>
									</tr>
								</thead>
								<tbody class="no-border-x">
									<tr>
										<td style="width:40%;"><i class="fa fa-sitemap"></i> Server load</td>
										<td class="text-right">0,2%</td>
										<td class="text-right">13,2 MB</td>
										<td class="text-right">0,1 MB/s</td>
									</tr>
									<tr>
										<td><i class="fa fa-tasks"></i> Apps</td>
										<td class="text-right">0,2%</td>
										<td class="text-right">13,2 MB</td>
										<td class="text-right">0,1 MB/s</td>
									</tr>
									<tr>
										<td><i class="fa fa-signal"></i> Process</td>
										<td class="text-right">0,2%</td>
										<td class="text-right">13,2 MB</td>
										<td class="text-right">0,1 MB/s</td>
									</tr>
									<tr>
										<td><i class="fa fa-bolt"></i> Wamp Server</td>
										<td class="text-right">0,2%</td>
										<td class="text-right">13,2 MB</td>
										<td class="text-right">0,1 MB/s</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			 <div class="row">
		        <div class="col-sm-6 col-md-6">
		          <div class="block-flat">
		              <div class="header">							
		                <h3>系统公告</h3><button class="btn btn-default btn-flat" type="button" id="not-basic"><i class="fa fa-comment"></i> 发布公告</button>
		              </div>
		              <div class="content">
		                  <h4>Examples</h4>
		                  <p>You can choose between use the basic <b>Gritter Notification</b> style or our theme based notification, just have fun with them.</p>
		                  <div class="spacer2 text-center">
		                    <button class="btn btn-default btn-flat" type="button" id="not-basic"><i class="fa fa-comment"></i> Basic</button>
		                    <button class="btn btn-primary btn-flat" type="button" id="not-theme"><i class="fa fa-comment"></i> Theme Style</button>
		                    <button class="btn btn-primary btn-flat" type="button" id="not-sticky"><i class="fa fa-comment"></i> Sticky</button>
		                    <button class="btn btn-primary btn-flat" type="button" id="not-text"><i class="fa fa-comment"></i> Just Text</button>
		                  </div>
		              </div>
		          </div>
		        </div>
		      </div>
		  </div>
		</div> 
		
	</div>

  <script src="http://www.jq22.com/jquery/jquery-1.10.2.js"></script>
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
