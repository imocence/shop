<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.order.add")} </title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<style type="text/css">
      .noneinput{border: none;}
</style>
<script type="text/javascript">
$().ready(function() {
	var $reviewForm = $("#reviewForm");
	var $refundForm = $("#refundForm");
	var $shippingButton = $("#shippingButton");
	var $refundButton = $("#refundButton");
	var $back = $("#back");
	[#if flashMessage?has_content]
		$.alert("${flashMessage}");
	[/#if]
	[#if sheet.status == "pendingReview"]
		// 审核
		$shippingButton.click(function() {
			var $this = $(this);
			$.dialog({
				type: "warn",
				content: "${message("admin.dialog.reviewConfirm")}",
				ok: "${message("admin.dialog.ok")}",
				cancel: "${message("admin.dialog.cancel")}",
				onOk: function() {
					$.ajax({
						url: "review",
						type: "POST",
						data: {id:"${sheet.id}",status:"audited"},
						dataType: "json",
						cache: false,
						success: function(message) {
							$.message(message);
							if (message.type == "success") {
								$shippingButton.addClass("disabled");
								$refundButton.addClass("disabled");
								setTimeout(function() {
										location.reload(true);
										$back.click();
									}, 3000);
							}
						}
					});
				}
			});
		});
		// 拒绝
		$refundButton.click(function() {
			var $this = $(this);
			$.dialog({
				type: "warn",
				content: "${message("admin.sheet.denied")}",
				ok: "${message("admin.dialog.ok")}",
				cancel: "${message("admin.dialog.cancel")}",
				onOk: function() {
					$.ajax({
						url: "review",
						type: "POST",
						data: {id:"${sheet.id}",status:"denied"},
						dataType: "json",
						cache: false,
						success: function(message) {
							$.message(message);
							if (message.type == "success") {
								$shippingButton.addClass("disabled");
								$refundButton.addClass("disabled");
								setTimeout(function() {
										location.reload(true);
										$back.click();
									}, 3000);
							}
						}
					});
				}
			});
		});
	[/#if]
});

</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.sheet.view")}
	</div>
	<table class="input tabContent">
		<tr>
			<th>
				<span class="requiredField">*</span>${message("common.country")}:
			</th>
			<td colspan="3">
				<input type="hidden" id="country" name="countryName" value="${country.id}"></input>
				<input type="text" readonly  unselectable="on" value="${message("${country.nameLocal}")}" ></input>
			</td>
			<td>
				&nbsp;
			</td>
			<td colspan="3">
				<input id="shippingButton" type="button" class="button" [#if sheet.status != "pendingReview"] disabled="disabled" [/#if] value="${message("admin.sheet.audited")}" />
				<input id="refundButton" type="button" class="button" [#if sheet.status != "pendingReview"] disabled="disabled" [/#if] value="${message("admin.sheet.denied")}" />				
				<input id="back" type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
			</td>
		</tr>
	</table>
	<table class="list tabContent">
		<tr>
			<th>
				${message("admin.order.product.sn")}
			</th>
			<th>
				${message("admin.order.product.name")}
			</th>

			<th>
				${message("admin.order.product.quantity")}
			</th>
			<th>
				${message("admin.order.product.stock")}
			</th>
		</tr>
		<input type="hidden" name="id" value="${sheet.id}"></input>
		[#list sheet.sheetItems as sheetItem]
			<tr class="product" data="${sheetItem.id}">
				<td>
					${sheetItem.sn}
				</td>
				<td>
					${sheetItem.name}
				</td>
				
				<td>
					${sheetItem.quantity}
				</td>
				<td>
					${sheetItem.sku.stock}
				</td>
			</tr>
		[/#list]
	</table>	
</body>
</html>