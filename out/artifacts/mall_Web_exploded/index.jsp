<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: shidongxuan
  Date: 18-12-20
  Time: 下午4:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

spring mvc上传文件


<form method="post" action="<c:url value="/manage/product/upload.do"/>" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="上传">
</form>
</body>
</html>
