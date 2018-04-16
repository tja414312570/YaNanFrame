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
  <link rel="stylesheet" href="js/highlightjs/default.css">
<script src="js/highlightjs/highlight.site.pack.js"></script>
</head>

<body>
<jsp:include page="include/topBar.jsp"></jsp:include>
	<div id="cl-wrapper" class="fixed-menu">
		<jsp:include page="include/leftMenu.jsp"></jsp:include>
		<div class="container-fluid" id="pcont">
		  <div class="cl-mcont">
			<div class="row">
				<div class="col-sm-12 col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">对象解释</font></font></h3>
						</div>
						<div class="content">
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">
							</font></font></h4>
							<p><font style="vertical-align: inherit;">DBFactory：数据库工厂，用于初始化，管理数据库</font></p>
							<p><font style="vertical-align: inherit;">DataBase：数据库，每个数据配置映射之后都有一个对应的DataBase</font></p>
							<p><font style="vertical-align: inherit;">ConnectionPools:连接池，用于管理应用的链接</font></p>
							<p><font style="vertical-align: inherit;">DataBaseConfigure：数据库配置</font></p>
							<p><font style="vertical-align: inherit;">DBTab：数据表，每个有@tab的Java文件映射后都会生成一个对应的DBTab的对象，用于存储映射关系，匹配数据库等</font></p>
							<p><font style="vertical-align: inherit;">DBColumn：数据表列，用于存储单个字段的映射关系</font></p>
							<p><font style="vertical-align: inherit;">TabCache：表缓存</font></p>
							<p><font style="vertical-align: inherit;">Class2TabMappingCache：类与表的映射缓存</font></p>
							<p><font style="vertical-align: inherit;">Case：事件，用于数据操作的判断条件</font></p>
							<p><font style="vertical-align: inherit;">Create：数据表的创建对象，用于创建数据表</font></p>
							<p><font style="vertical-align: inherit;">Delete：数据的删除对象，用于删除数据表中的数据</font></p>
							<p><font style="vertical-align: inherit;">Insert：数据表的插入对象，用于向数据表中插入数据</font></p>
							<p><font style="vertical-align: inherit;">Query：数据表的查询对象，用于在数据表中查询数据</font></p>
							<p><font style="vertical-align: inherit;">Update：数据表的更新对象，用于更新数据表中的数据</font></p>
							<p><font style="vertical-align: inherit;">@Column：数据表列的注解，用于Java字段到数据表的Column的映射关系</font></p>
							<p><font style="vertical-align: inherit;">@Tab：数据表的注解，用于Java类到数据表的映射关系</font></p>
							<p><font style="vertical-align: inherit;">Transaction：事物对象，用于hibernate的事物的支持</font></p>
							<p><font style="vertical-align: inherit;">TransactionPools：事物池对象，用于管理事物</font></p>
						</div>
					</div>
				</div>
				<div class="col-sm-12 col-md-12">
					<div class="block-flat">
						<div class="header">							
							<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">数据库层说明</font></font></h3>
						</div>
						<div class="content">
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">
								在Hibernate中，操作数据库就是操作Java Object，Hibernate 组件覆盖了数据库的增(Insert)删(Delete)查(Query)改(Update)，
								调用非常方便简单，因此一般数据库使用，你无须学会Sql语句就能轻松使用，让我们简单看看一下例子
							</font></font>
							</h4>
							<pre><code class="java hljs" bind-url="codes/hibernate/Query/base-query.code" bind-type="text" bind-success="hljs.initHighlightingOnLoad();"></code></pre>
							<pre><code class="java hljs" bind-url="codes/hibernate/Query/base-use.code" bind-type="text" bind-success="hljs.initHighlightingOnLoad();"></code></pre>
						</div>
					</div>
				</div>
			</div>
		  </div>
		</div> 
		
	</div>
  <script src="http://www.jq22.com/jquery/jquery-1.10.2.js"></script>
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
      $(document).ready(function(){
        App.init();
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
