<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.warehouse.edit")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			order: "digits"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.warehouse.edit")}
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="id" value="${warehouse.id}" />
		<table class="input">
			<tr>
				<th>
					${message("common.country")}:
				</th>
				<td>
					<select id="countryId" name="country.id">
						[#list countries as country]
							<option value="${country.id}"[#if country.id == brand.country.id] selected="selected"[/#if]>${country.name}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Warehouse.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${warehouse.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Warehouse.code")}:
				</th>
				<td>
					<input type="text" name="code" class="text" value="${warehouse.code}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Warehouse.address")}:
				</th>
				<td>
					<input type="text" name="address" class="text" value="${warehouse.address}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductTag.memo")}:
				</th>
				<td colspan="4">
					<input type="text" name="memo" class="text" value="${warehouse.memo}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" value="${warehouse.order}" maxlength="9" />
				</td>
			</tr>
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