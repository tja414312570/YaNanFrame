/**
 * RTDT 配置文件
 * 调用方法
 * 参数说明
 * url:websocket地址，RTDT
 * parameters:序列化的表单数据
 * success:当正常返回数据
 * error:当错误发生（可能服务器返回错误信息，或者js内部错误）
 * 需要其中依赖jquery的extends函数，需要jquery支持
 * version: v1.0
 * version: v1.1 解决跨域访问的问题
 * protocol；RTDT 1.0
 * by:YaNan 2017-06-22
 */
var RTDTConf ={
	url:"ws://localhost:8080/YaNanFrame/RTDT",//"+window.location.hostname+"
	timeout:3000,
	refresh:500,
}
