<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>index page</title>
    <style>
        .ui-widget-content{

        }
    </style>
</head>
<body>
<table class="ui-widget-content" border="1" cellspacing="0" cellpadding="0" >
    <tr>
        <th>id</th>
        <th>学生姓名</th>
        <th>班级</th>
        <th>性别</th>
        <th>身份证号</th>
        <th>家长姓名</th>
        <th>联系方式</th>
    </tr>
    <#list studentList.list as item>
    <tr>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.className}</td>
        <td>${item.sex}</td>
        <td>${item.idCard}</td>
        <td>${item.parentName}</td>
        <td>${item.phone}</td>
    </tr>
    </#list>
</table>
<form method="POST" enctype="multipart/form-data" action="/student/batchAddStudent">
    <p>
        文件：<input type="file" name="file" />
    </p>
    <p>
        <input type="submit" value="上传" />
    </p>
</form>
</body>
</html>