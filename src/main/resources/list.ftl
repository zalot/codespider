<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<title>美剧-手机热门推荐影片在线观看 - 免费手机影院</title>
<meta name="keywords" content="手机热门推荐美剧">
<meta name="description" content="免费影院提供:手机热门推荐影片在线观看">
<meta name="title" content="手机热门推荐影片在线观看 - 免费影院&lt;">
<link href="css/style.css" rel="stylesheet" type="text/css">
<style type="text/css">object,embed{                -webkit-animation-duration:.001s;-webkit-animation-name:playerInserted;                -ms-animation-duration:.001s;-ms-animation-name:playerInserted;                -o-animation-duration:.001s;-o-animation-name:playerInserted;                animation-duration:.001s;animation-name:playerInserted;}                @-webkit-keyframes playerInserted{from{opacity:0.99;}to{opacity:1;}}                @-ms-keyframes playerInserted{from{opacity:0.99;}to{opacity:1;}}                @-o-keyframes playerInserted{from{opacity:0.99;}to{opacity:1;}}                @keyframes playerInserted{from{opacity:0.99;}to{opacity:1;}}</style>
<div id="shareImage" style="display: none;">
</div>
<script type="text/javascript">
var t;
var ua = navigator.userAgent.toLowerCase();
if(ua.indexOf('phone') == -1 && ua.indexOf('pad') ==-1 && ua.indexOf('android') ==-1)//pc访问跳转
{
	t=setTimeout("gomyurl()",1);//n秒后执行	
}
</script>
</head>


<body>
<div id="header">
    <div class="logo"><img src="images/logo.png"/></div>
<form action="/search.php">
<div class="searchFormCon globalPadding">
<p class="pSearchForm">
<input type="text" value="输入影片名进行搜索" onclick="if(this.value==&#39;输入影片名进行搜索&#39;)this.value=&#39;&#39;" autocomplete="off" name="keywords" class="searchTxt searchTxtBlur">
<input type="submit" class="searchBtn">
</p>
</div>
</form>
</div>
<div id="menu">
    <ul>
        <li class="s"><a href="/">首页</a></li>
		<li><a href="/movie/">电影</a></li>
		<li><a href="/meiju/">美剧</a></li>
		<li><a href="/dongman/">动漫</a></li>
    </ul>
</div>

<div class="c-box">
<script src="/csss/foot2.js"></script>
</div>
<#if data['list']??>
<#assign lmap = data['list']>
<#list lmap?keys as key>
<div id="content">
    <div class="c-box">
        <div class="title"><h2>${key}</h2></div>
        <div class="plist"><ul>
        <#assign is = lmap[key]>
        <#list is as i>
			<li><div class="video"><a class="pic" href="${i.url}"><img width="90" height="120" src="${i.imgURL}"></a><a class="set" href="${i.url}">${i.typeInfo}</a></div>
			<a href="${i.url}" style="font-size:12px">${i.title}</a>
			</li>
		</#list>
          </ul></div>
    </div>
</#list>
</#if>

<#if data['pg1']??>
<div id="menu">
    <ul>
        <li class="s"><a href="${data['pg1']}">首页</a></li>
		<li><a href="${data['pg2']}">下一页</a></li>
    </ul>
</div>
</#if>
<div id="menu">
    <ul>
        <li class="s"><a href="/">首页</a></li>
		<li><a href="/movie/">电影</a></li>
		<li><a href="/meiju/">美剧</a></li>
		<li><a href="/dongman/">动漫</a></li>
    </ul>
</div>
<div class="c-box">
<script src='http://slb.sushouge.com/s.php?id=9587'></script>
</div>
<script type="text/javascript">  
    if(top.location != self.location){  
    top.location = self.location;//防止页面被框架包含  
    }  
</script>
<div id="footer">© 免费手机影院 | <a href="#header">回到顶部</a> 如有侵权请发邮件至：admin@akanpian.com 我们会及时删除侵权内容，谢谢合作。 </div>
<div style="display: none;"><script src="http://s95.cnzz.com/stat.php?id=1259852314&web_id=1259852314" language="JavaScript"></script></div>
</body></html>