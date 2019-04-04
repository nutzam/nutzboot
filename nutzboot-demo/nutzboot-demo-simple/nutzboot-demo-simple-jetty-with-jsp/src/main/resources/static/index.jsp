<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	
	<div>
		<h2>我是JSP文件, EL输出Context Path=${base}</h2>
	</div>
	<div>
		<h2>我是JSP文件, JSTL输出<c:out value="日期是${obj.date}"></c:out></h2>
	</div>
</body>
</html>