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
							<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">数据库配置</font></font></h3>
						</div>
						<div class="content">
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">配置说明</font></font></h4>
							<p><font style="vertical-align: inherit;">标签说明</font></p>
							<font style="vertical-align: inherit;">
								dataBase标签，数据库实体标签，拥有属性
								<h5 style="color: #F00;">必选属性</h5>
								<ol>
									<li>name:数据库名称</li>
									<li>password:密码</li>
								</ol>
								<h5 style="color: #F00;">可选属性</h5>
								<ol>
									<li>driver:驱动,默认:com.mysql.jdbc.Driver</li>
									<li>host:主机地址，名称或IP,默认：localhost</li>
									<li>port:端口号，默认：3306</li>
									<li>username:用户名，默认：root</li>
									<li>type:数据类型，默认：mysql</li>
									<li>cahrset:字符类型，默认utf8</li>
									<li>collate:字符集合，默认utf8_general_ci</li>
									<li>encoding:编码，默认utf-8</li>
									<li>minNum:最小线程池数量，默认2</li>
									<li>maxNum:最大线程池数量，默认6</li>
									<li>addNum:线程池递增数量，默认2</li>
								</ol>
							</font>
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">1）、基础配置案例</font></font></h4>
							<pre><code class="xml hljs" bind-url="codes/hibernate/base-hibernate.code" bind-type="text" bind-success="hljs.initHighlightingOnLoad();"></code></pre>
							
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">2）、多个数据库</font></font></h4>
							<pre><code class="xml hljs " bind-url="codes/hibernate/multi-hibernate.code" bind-type="text"  bind-success="hljs.initHighlightingOnLoad();"></code></pre>
							
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">3）、数据表映射</font></font></h4>
								在Hibernate中，所有的数据表都映射与java对象，操作数据表就是对java对象的操作，下面是一张学生表
							<pre><code class="hljs java" bind-url="codes/hibernate/base-java-bean.code"  bind-success="hljs.initHighlightingOnLoad();"></code></pre>
							<h4>在Hibernate中，所有数据表的与Java Class的映射都是通过注解（Annotation）实现的，例如上面例子中的@Tab，表明这是一张数据表，DB属性表明这张表存在于YaNan_Demo这个数据库中</h4>
							<h5 style="color: #F00;">数据表注解(@Tab)</h5>
								<ol>
									<li>name:数据表名称，不填写时为Java类名</li>
									<li>isMust:数据表是否在Hibernate初始化时进行初始化</li>
									<li>DB:数据表所对应的数据</li>
									<li>collate:字符集合，默认空</li>
									<li>charset:字符集，默认utf8</li>
								</ol>
								<h5 style="color: #F00;">数据列注解(@Column)</h5>
								<ol>
									<li>ignore:忽略此列，该字段不会被解析</li>
									<li>name:字段名，不填写时为Java字段名</li>
									<li>type:字段类型，不填自动解析，如int对应Integer</li>
									<li>length:字段长度，默认：-1，type为"varchar"等有length属性时有效</li>
									<li>size:大小，默认：-1，type为"int"等数字类型有效</li>
									<li>Not_Null:非空，默认false</li>
									<li>point:是否有小数点，默认false</li>
									<li>Auto_Increment:自增，默认false，数字类型时有效，一般配合主键使用</li>
									<li>Primary_Key:主键，默认false，一个表中只有一个主键</li>
									<li>unique:unique约束，默认false，详细参考sql属性</li>
									<li>Not_Sign:无符号，默认false</li>
									<li>Annotations:字段注释，默认为空，</li>
									<li>collate:参考数据表解释，默认空</li>
									<li>charset:参考数据表解释，默认空</li>
								</ol>
								<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">下面是一张较为完善的数据表映射</font></font></h4>
								<pre><code class="hljs java" bind-url="codes/hibernate/shop-java-bean.code"  bind-success="hljs.initHighlightingOnLoad();"></code></pre>
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
