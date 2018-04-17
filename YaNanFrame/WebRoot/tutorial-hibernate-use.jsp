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
							<h3><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">数据库的使用</font></font></h3>
						</div>
						<div class="content">
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">
								在Hibernate中，操作数据库就是操作Java Object，Hibernate 组件覆盖了数据库的增删查改，调用起来也更简单，
								操作对象位于包com.YaNan.frame.hiberante.database下
							</font></font></h4>
							<b><font style="vertical-align: inherit;">查询表（Query）</font></b>
							<h5 style="color: #F00;">构造方法</h5>
							<ol>
								<li>Query(DBTab dbTab):DBTab 为表的映射对象</li>
								<li>Query(Object obj, String... strings):传入一个对象，可选参数为要查询的字符，不填时查询所有字段</li>
								<li>Query(Object obj, boolean starReplace):传入一个对象，boolean为是否代取代解析后名称是否去掉命名空间，默认false</li>
								<li>Query(Class<?> cls,String... strings):传入一个要查询的类，可选参数为要查询的字符，不填时查询所有字段</li>
								<li>Query(Class<?> queryCls, Class<?> saveCls,String...strings):传入一个要查询的类，查询后保存的类，可选参数为要查询的字符，不填时查询所有字段</li>
								<li>Query(Class<?> queryCls, Class<?> saveCls, boolean trans, String...strings):同上，boolean为是否去掉命名空间</li>
							</ol>
							<h5 style="color: #F00;">方法</h5>
							<ol>
								<li>public void addOrder(String...strings ):查询后要ASC排序的字段</li>
								<li>public void addOrderByDesc(String...strings ):查询后要DESC排序的字段</li>
								<li>public void setlimit(int num):限制查询的数量</li>
								<li>public void setLimit(int pos,int num):限制查询的位置和数量</li>
								<li>public void addField(String... strings):添加要查询的字段</li>
								<li>public void addField(Field... strings):添加要查询的字段</li>
								<li>public void addCondition(Field field, String condition):添加查询的条件</li>
								<li>public void addCondition(String field, Object condition):添加查询的条件</li>
								<li>public void addConditionField(String... field):添加要查询的条件，构造参数中有Object时有效</li>
								<li>public void addConditionCommand(String... condition):添加条件的表达式（如 a=12)</li>
								<li>public String create():创建当前条件的Query对象的Sql语句，返回sql语句</li>
								<li>public Map<Field, DBColumn> getFieldMap():获取查询的列，返回字段和DBColumn的集合</li>
								<li>public void removeField(String... string):移除要查询的列</li>
								
								<li>public void setGroup(String group) :设置组查询/li>
								<li>public void setSubQuery(Query subQuery):设置子查询</li>
								<li>public void setUnionQuery(Query unionQurery):设置联合查询</li>
								<li>public void setUnionAllQuery(Query unionQurery):设置联合查询</li>
								<li>public void setJoinLeft(Class<?> cls, String... conditions):设置左链表查询</li>
								<li>public void setJoinLeft(Class<?> cls,boolean trans, String... conditions):设置左链表查询，boolean为是否取代命名空间</li>
								<li>public void setInnnerJoin(Class<?> cls, String... conditions):设置内联表表查询</li>
								<li>public void setInnnerJoin(Class<?> cls,boolean trans, String... conditions) :设置做链表查询</li>
								<li>public void setJoinLeft(Class<?> cls, String... conditions):设置做链表查询</li>
							</ol>
							<h4><font style="vertical-align: inherit;"><font style="vertical-align: inherit;">1）、最基础的使用，查看生成的sql语句</font></font></h4>
							<pre><code class="xml hljs" bind-url="codes/hibernate/Query/base-query.code" bind-type="text" bind-success="hljs.initHighlightingOnLoad();"></code></pre>
							<p>控制台输出</p>
							<pre><code class="xml hljs" bind-url="codes/hibernate/Query/base-query-out.code" bind-type="text" bind-success="hljs.initHighlightingOnLoad();"></code></pre>
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
