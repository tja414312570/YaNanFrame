/**
 * Req数据交互参数
 */
/**
 * Notify 数据参数
 */
var reqMap = {};
var timeoutTimers = {};
var actionTimers = {};
var webSocket;
var bindQueue = {};
var RTDT=RTDT || {};
function guid() {
    function S4() {
       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}
RTDT = function(){
	this.available=false;
	/**
	 * req数据交互请求
	 */
	this.Req = function(params){
		params = $.extend(true, {
			action: null,
			namespace: null,
			data: null,
			success:null,
			error:null
			},params);
		var REQAction = {
			action:params.action,
			type:4280,
			parameterMap:params.data,
			AUID:guid()
		}
		reqMap[REQAction.AUID]=params;
		timeoutTimers[REQAction.AUID] = setTimeout(function(){
			if(RTDT.available)
				RTDT.OnError(REQAction.AUID,{AUID:REQAction.AUID,data:"RTDT requests connection timeout"});
			else
				RTDT.OnError(REQAction.AUID,{AUID:REQAction.AUID,data:"RTDT service is unavailable"});
		},RTDTConf.timeout)
		actionTimers[REQAction.AUID] = setInterval(function(){
			if(RTDT.available){
				clearInterval(actionTimers[REQAction.AUID]);
				webSocket.send(JSON.stringify(REQAction));
			}
		},RTDTConf.refresh);
		
	}
	/**
	 * notify广播机制
	 */
	this.Bind=function(params){
		params = $.extend(true, {
			action:null,
			namespace:null,
			bind:null,
			notify:null,
			error:null,
			data:null
		},params);
		if(bindQueue[params.action]==true)
			return;
		bindQueue[params.action]=true;
		var REQAction = {
			action:params.action,
			type:4281,
			parameterMap:params.data,
			AUID:guid()
		}
		reqMap[REQAction.AUID]=params;
		timeoutTimers[REQAction.AUID] = setTimeout(function(){
			if(RTDT.available)
				RTDT.OnError(REQAction.AUID,{AUID:REQAction.AUID,data:"RTDT requests connection timeout"});
			else
				RTDT.OnError(REQAction.AUID,{AUID:REQAction.AUID,data:"RTDT service is unavailable"});
		},RTDTConf.timeout)
		actionTimers[REQAction.AUID] = setInterval(function(){
			if(RTDT.available){
				clearInterval(actionTimers[REQAction.AUID]);
				webSocket.send(JSON.stringify(REQAction));
			}
		},RTDTConf.refresh);
	}
	this.isAvailable = function(){
		return this.available;
	}
	this.OnResponse=function(data){
		try{//需要捕获异常
			data = eval("("+data.data+")");
			if(data.AUID==null||data.AUID==undefined)
				throw new Error("protocol error");
			clearTimeout(timeoutTimers[data.AUID]);
			//判断为请求还是绑定
			if(data.status==null||data.status==undefined||data.type==null||data.type==undefined)
				throw new Error("protocol error")
			if(data.type==4281){
				if(data.status==4270){
					if(typeof reqMap[data.AUID].bind=="function")
						reqMap[data.AUID].bind.call(this,data.data);
				}else if(data.status==4280){
					if(typeof reqMap[data.AUID].notify=="function")
						reqMap[data.AUID].notify.call(this,data.data);
				}else{
					if(typeof reqMap[data.AUID].error=="function")
						reqMap[data.AUID].error.call(this,data);
				}
			}else if(data.status==4280){
				if(typeof reqMap[data.AUID].success=="function")
					reqMap[data.AUID].success.call(this,data.data);
			}else{
				RTDT.OnError(data.AUID,data)
			}
		}catch(err){
			if(typeof RTDTConf.error == "function"){
				RTDTConf.error.call(this,err)
			}
		}
	}
	this.OnError = function(AUID,err){
		if(typeof reqMap[AUID].error=="function")
				reqMap[AUID].error.call(this,err);
	}
};
(function(){
	/**
	 * 检查socket 配置文件是否存在
	 * @param {Object} event
	 */
	RTDT = new RTDT();
	if(typeof RTDTConf == "undefined")
		throw new Error("RTDT need RTDT Conf")
	webSocket =	new WebSocket(RTDTConf.url);
		webSocket.onerror = function(event) {
			if(reqMap.length>0){
				for(var key in reqMap){
					console.log(key)
				}
			}
			if(typeof RTDTConf.error=="function")
				RTDTConf.error(event);
			else{
				throw new Error("RTDT init failed");
				console.log(event);
			}
		};
		webSocket.onopen = function(event) {
			RTDT.available=true;
		};
		webSocket.onmessage = function(event) {
			RTDT.OnResponse(event)
		};
	
})();

