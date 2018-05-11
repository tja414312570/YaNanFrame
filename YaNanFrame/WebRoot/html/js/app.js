//function load(url){
//	$.ajax({
//		type:"get",
//		url:url,
//		async:true,
//		success:function(e){
//			
//		}
//	});
//}
/**
 * 页面变量
 * @param {Object} express
 */
var decodeType="jsp";
function varDecoder(express){
		var start = express.indexOf("${");
		if(start==-1)return express;
		var end = express.indexOf("}");
		if(start!=-1&&end!=-1)
		var exp = express.substring(start+2,end);
		var head = express.substring(0,start);
		var rest = express.substring(end+1);
		var tmp = eval(exp);
		var result = head+tmp+rest;
		return result.indexOf("${")==-1&&result.indexOf("}")==-1?result:varDecoder(result);
   	}
function varDecoderJSP(express){
		var start = express.indexOf("$<");
		if(start==-1)return express;
		var end = express.indexOf(">");
		if(start!=-1&&end!=-1)
		var exp = express.substring(start+2,end);
		var head = express.substring(0,start);
		var rest = express.substring(end+1);
		var tmp = eval(exp);
		var result = head+tmp+rest;
		return result.indexOf("$<<")==-1&&result.indexOf(">>")==-1?result:varDecoder(result);
   	}
$(document).ready(function(e){
	$("[filter]").each(function(){
		_this = $(this);
		console.log(_this.html())
		var cls = $(this).attr("filter").trim().split(",");
		for(var index in cls){
			var field = cls[index];
			if(field!="filter"&&($(this).attr(field)!=undefined||field=="html")){
				var express =field=="html"?$(this).html():$(this).attr(field);
				var result =decodeType=="html"?varDecoder(express):varDecoderJSP(express);
				if(field=="html")
					$(this).html(result);
				else
					$(this).attr(field,result);
			if(_this.attr("filter-success")!=undefined)
				eval(_this.attr("filter-success"));
			}
		}
	});
	$("[bind-url]").each(function(){
		var _this = $(this);
			$.ajax({
				type:"get",
				url:$(this).attr("bind-url"),
				async:true,
				success:function(data){
					if(_this.attr("bind-attr")===undefined){
						if(_this.attr("bind-type")=="text")
							_this.text(data);
						else
							_this.html(data);
					}else{
						var attrs = _this.attr("bind-attr").trim();
						var attrArray = attrs.split(",")
						for(var index in attrArray){
							if(attrArray[index]=="html"||attrArray[index]=="")
								if(_this.attr("bind-type")=="text")
									_this.text(data);
								else
									_this.html(data);
							else
								_this.attr(attrArray[index],data);
						}
					}
					if(_this.attr("bind-success") != undefined)
						eval(_this.attr("bind-success"))
				},error:function(err){
					console.log(err)
				}
			});
	});
});
/**
 * 获取URL调用所有参数
 */
function getInvokeAllParameters(urls){
	var result = {};
	if(urls.indexOf("://")!=-1)
		urls = urls.split("://")[1];
	var str = urls.split("&");
	for(var s in str){
		var kvs = str[s].split("=");
		var key = kvs[0];
		var values = kvs.length==1?null:kvs[1];
		result[key]=values;
	}
	return result;
}