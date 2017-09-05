<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.order.add")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

});

function changeCountry(){
	// 根据国家获取商品分类
	var country = $("#country").val();
	$.ajax({
		url: "${base}/admin/order/getProducts",
		type: "GET",
		data: {countryName: country},
		dataType: "json",
		cache: false,
		success: function(message) {
			console.log(message);
		}
	});
}

</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.order.add")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.order.orderInfo")}" />
		</li>
		<li class="product">
			<input type="button" value="${message("admin.order.productInfo")}" />
		</li>
	</ul>
	<form id="inputForm" action="save" method="post">
		<table id="order" class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("common.country")}:
				</th>
				<td>
					<select id="country" name="countryName" onchange="changeCountry();">
						[@country_list]
							[#list countrys as country]
								<option value="${country.name}">${message("${country.nameLocal}")}</option>
							[/#list]
						[/@country_list]
					</select>
				</td>
			</tr>
		</table>
		<table class="input tabContent product">
			<tr>
				<th>2
				</th>
				<td>
				2
				</td>
			</tr>
		</table>
		<table class="input">
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>