<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.memberRank.edit")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $amount = $("#amount");
	var $isSpecial = $("#isSpecial");
	
	[@flash_message /]
	
	// 是否特殊
	$isSpecial.click(function() {
		if ($(this).prop("checked")) {
			$amount.val("").prop("disabled", true);
		} else {
			$amount.prop("disabled", false);
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			firstSingle: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: 3
				}
			},
			nextSingle: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: 3
				}
			}
			//amount: {
				//required: true,
				//min: 0,
				//decimal: {
					//integer: 12,
					//fraction: ${setting.priceScale}
				//},
				//remote: {
					//url: "check_amount",
					//cache: false
				//}
			//}
		}
		//messages: {
			//amount: {
				//remote: "${message("common.validate.exist")}"
			//}
		//}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.memberRank.edit")}
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="id" value="${memberRank.id}" />
		<table class="input">
			<tr>
				<th>
					${message("Brand.country")}:
				</th>
				<td>
					<select id="countryId" name="country.id">
						[#list countries as country]
							<option value="${country.id}"[#if country.id == memberRank.country.id] selected="selected"[/#if]>${country.name}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("MemberRank.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${memberRank.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("MemberRank.type")}:
				</th>
				<td>
					<select name="type">
						[#list types as type]
							<option value="${type}" [#if memberRank.type == type] selected="selected"[/#if]>${message("MemberRank.type." + type)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			
			<tr class="hidden">
				<th>
					<span class="requiredField">*</span>${message("MemberRank.scale")}:
				</th>
				<td>
					<input type="text" name="scale" class="text" value="${memberRank.scale}" maxlength="7" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					<span class="requiredField">*</span>${message("MemberRank.amount")}:
				</th>
				<td>
					<input type="text" id="amount" name="amount" value="${memberRank.amount}" class="text" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("MemberRank.firstSingle")}:
				</th>
				<td>
					<input type="text" id="firstSingle" name="firstSingle" value="${memberRank.firstSingle}" class="text" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("MemberRank.nextSingle")}:
				</th>
				<td>
					<input type="text" id="nextSingle" name="nextSingle" value="${memberRank.nextSingle}" class="text" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.setting")}:
				</th>
				<td>
					<label>
						<input type="checkbox" name="isDefault" value="true"[#if memberRank.isDefault] checked="checked" disabled="disabled"[/#if] />${message("MemberRank.isDefault")}
						<input type="hidden" name="_isDefault" value="false" />
					</label>
					<label title="${message("admin.memberRank.isSpecialTitle")}">
						<input type="checkbox" id="isSpecial" name="isSpecial" value="true"[#if memberRank.isSpecial] checked="checked"[/#if] />${message("MemberRank.isSpecial")}
						<input type="hidden" name="_isSpecial" value="false" />
					</label>
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