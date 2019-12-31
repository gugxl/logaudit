<%--
  Created by IntelliJ IDEA.
  User: gugu
  Date: 2019/12/31
  Time: 0:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>测试页面4</title>
  <script type="text/javascript" src="./js/analytics.js"></script>

  <script type="text/javascript">
    (function () {
			var _aelog_ = _aelog_ || window._aelog_ || [];
			// 设置_aelog_相关属性
			_aelog_.push(["memberId","zhangsan"]);
			window._aelog_ = _aelog_;
			(function(){
				var aejs = document.createElement('script');
				aejs.type = 'text/javascript';
				aejs.async = true;
				aejs.src = './js/analytics.js';
				var script = document.getElementsByTagName('script')[0];
				script.parentNode.insertBefore(aejs, script);
			})();
		})();
  </script>
</head>
<body>
测试页面4
测试页面4<br/>
在本页面设置memberid为zhangsan<br/>
跳转到:
<a href="demo.jsp">demo</a>
<a href="demo2.jsp">demo2</a>
<a href="demo3.jsp">demo3</a>
<a href="demo4.jsp">demo4</a>
</body>
</html>
