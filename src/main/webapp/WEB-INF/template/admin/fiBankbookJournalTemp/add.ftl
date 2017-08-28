<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.index.fiBankbookJournalTempAdd")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $memberSelect = $("#memberSelect");
	var $money = $("#money");
	var $country = $("#country");
	var $usercode = $("#usercode");
	var $name = $("#name");
	[@flash_message /]
	
	// 用户选择
	$memberSelect.autocomplete("member_select", {
		dataType: "json",
		max: 20,
		width: 218,
		scrollHeight: 300,
		extraParams: {country:$country.val()}, 
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.code
				}
			});
		},
		formatItem: function(item) {
			return '<span title="' + escapeHtml(item.name) + '">' + escapeHtml(abbreviate(item.name, 50, "...")) + '<\/span>' + (item.code.length > 0 ? ' <span class="silver">[' + escapeHtml(item.code) + ']<\/span>' : '');
		}
	}).result(function(event, item) {
		$usercode.val(item.code);
		$name.html(escapeHtml(item.name) + (item.code.length > 0 ? ' <span class="silver">[' + escapeHtml(item.code) + ']<\/span>' : '')).closest("tr").show();
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			money: {
				required: true,
				min: 0,
				decimal: {
					integer: 21,
					fraction: ${setting.priceScale}
				}
			}
		},
		submitHandler: function(form) {
			if ($usercode.val() == "") {
				$.message("warn", "${message("admin.fiBankbookJournalTemp.usercodeRequired")}");
				return false;
			}
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});

function countryChange(){
	$("#usercode").val('');
	$("#name").html('').closest("tr").hide();
}
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.index.fiBankbookJournalTempAdd")}
	</div>
	<form id="inputForm" action="save" method="post">
		<input type="hidden" id="usercode" name="usercode" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("common.country")}:
				</th>
				<td>
					<select id="country" name="country" onchange="countryChange();">
						[@country_list]
							[#list countrys as country]
								<option value="${country.name}">${message("${country.nameLocal}")}</option>
							[/#list]
						[/@country_list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournalTemp.memberSelect")}:
				</th>
				<td>
					<input type="text" id="memberSelect" name="memberSelect" class="text" maxlength="200" title="${message("admin.fiBankbookJournalTemp.memberSelectTitle")}" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("common.member")}:
				</th>
				<td id="name">
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournal.type")}:
				</th>
				<td>
					<select name="type">
						[#list types as value]
							<option value="${value}">${message("admin.fiBankbookJournal.type." + value)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournalTemp.dealType")}:
				</th>
				<td>
					<select name="dealType">
						[#list dealTypes as value]
							<option value="${value}">${message("admin.fiBankbookJournal.dealType." + value)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournal.moneyType")}:
				</th>
				<td>
					<select name="moneyType">
						[#list moneyTypes as value]
							<option value="${value}">${message("admin.fiBankbookJournal.moneyType." + value)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.fiBankbookJournalTemp.money")}:
				</th>
				<td>
					<input type="text" id="money" name="money" class="text" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.fiBankbookJournal.notes")}:
				</th>
				<td>
					<input type="textarea" name="notes" class="text" maxlength="200" />
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