<%--
  Created by IntelliJ IDEA.
  User: gugu
  Date: 2019/12/31
  Time: 0:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>测试页面2</title>
  <script type="text/javascript" src="./js/analytics.js"></script>
</head>
<body>
测试页面2
<br>
<label>orderid: 123456</label><br>
<label>orderName: 测试订单123456</label><br/>
<label>currencyAmount: 524.01</label><br/>
<label>currencyType: RMB</label><br/>
<label>paymentType: alipay</label><br/>
<button onclick="__AE__.onChargeRequest('123456','测试订单123456',524.01,'RMB','alipay')">触发chargeRequest事件</button><br/>
跳转到:
<a href="demo.jsp">demo</a>
<a href="demo2.jsp">demo2</a>
<a href="demo3.jsp">demo3</a>
<a href="demo4.jsp">demo4</a>
</body>
</html>
