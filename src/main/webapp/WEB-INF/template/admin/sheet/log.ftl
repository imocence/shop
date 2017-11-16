<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.stock.log")} </title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/country.js"></script>
<script type="text/javascript">
$().ready(function() {
	var $listForm = $("#listForm");
	var $countryId = $("#countryId");

	var $ids = $("#listTable input[name='ids']");
	var $selectAll = $("#selectAll");
	var $shippingButton = $("#shippingButton");
	var $refundsButton = $("#refundsButton");

	var $statusName = $("#statusName");
	var $statusMenu = $("#statusMenu");
	var $statusMenuItem = $("#statusMenu li");
	
	
	[@flash_message /]
	$statusMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$statusMenuItem.click(function() {
		$statusName.val($(this).attr("val"));
		$listForm.submit();
	});
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$shippingButton.removeClass("disabled");
			$refundsButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$shippingButton.removeClass("disabled");
				$refundsButton.removeClass("disabled");
			} else {
				$shippingButton.addClass("disabled");
				$refundsButton.addClass("disabled");
			}
		}
	});
	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
			if ($enabledIds.filter(":checked").size() > 0) {
				$shippingButton.removeClass("disabled");
				$refundsButton.removeClass("disabled");
			} else {
				$shippingButton.addClass("disabled");
				$refundsButton.addClass("disabled");
			}
		} else {
			$enabledIds.prop("checked", false);
			$shippingButton.addClass("disabled");
			$refundsButton.addClass("disabled");
		}
	});
	// 审核
	$shippingButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.reviewConfirm")}",
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "shippingReview",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							setTimeout(function() {
									location.reload(true);
								}, 3000);
						}
						$shippingButton.addClass("disabled");
						$selectAll.prop("checked", false);
						$checkedIds.prop("checked", false);
					}
				});
			}
		});
		return false;
	});
	// 拒绝
	$refundsButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.sheet.denied")}",
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "returnsReview",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							setTimeout(function() {
									location.reload(true);
								}, 3000);
						}
						$refundsButton.addClass("disabled");
						$selectAll.prop("checked", false);
						$checkedIds.prop("checked", false);
					}
				});
			}
		});
		return false;
	});
});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.stock.log")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="log" method="get">
		<input type="hidden" id="statusName" name="statusName" value="${statusName}" />
		<input type="hidden" id="countryName" name="countryName" value="${countryName}" />
		<div class="bar">
			<div class="buttonGroup">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="add" class="iconButton">
					<span class="addIcon">&nbsp;</span>${message("admin.common.add")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
		   		<div id="countryMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("common.country")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if country.name == null] class="current"[/#if] val="">${message("common.country.all")}</li>
						[@country_list]
							[#list countrys as country]
								<li[#if country.name == countryName] class="current"[/#if] val="${country.name}">${message("${country.nameLocal}")}</li>
							[/#list]
						[/@country_list]
					</ul>
				</div>
				<div id="statusMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("StockLog.type")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li [#if statusName == null] class="current"[/#if] val="">${message("common.country.all")}</li>
						[#list statuss as value]
							<li [#if value == countryName] class="current"[/#if] val="${value}">${message("admin.sheet.${value}")}</li>
						[/#list]
					</ul>
				</div>
				<div id="pageSizeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if page.pageSize == 10] class="current"[/#if] val="10">10</li>
						<li[#if page.pageSize == 20] class="current"[/#if] val="20">20</li>
						<li[#if page.pageSize == 50] class="current"[/#if] val="50">50</li>
						<li[#if page.pageSize == 100] class="current"[/#if] val="100">100</li>
					</ul>
				</div>
			</div>
			<div id="searchPropertyMenu" class="dropdownMenu">
				<div class="search">
					<span class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<ul>
					<li[#if page.searchProperty == "sku.sn"] class="current"[/#if] val="sn">${message("Sku.sn")}</li>
				</ul>
			</div>
			<div class="buttonGroup">
				<input id="shippingButton" type="button" class="button disabled" value="${message("admin.sheet.audited")}" />
				<input id="refundsButton" type="button" class="button disabled" value="${message("admin.sheet.denied")}" />
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="sn">${message("Order.sn")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="country">${message("common.country")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="username">${message("admin.sheet.username")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="auditor">${message("admin.sheet.auditor")}</a>
				</th>

				<th>
					<a href="javascript:;" class="sort" name="type">${message("StockLog.type")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createdDate">${message("admin.common.createdDate")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="modifyDate">${message("admin.common.lastModifiedDate")}</a>
				</th>
				<th>
					<span>${message("admin.common.action")}</span>
				</th>
			</tr>
			[#list page.content as sheetLog]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${sheetLog.id}" />
					</td>
					<td>
						${sheetLog.sn}
					</td>
					<td>
						${sheetLog.country.name}
					</td>
					<td>
						${sheetLog.admin.username}
					</td>
					<td>
						${sheetLog.auditor}
					</td>

					<td>
						${message("admin.sheet." + sheetLog.status)}
					</td>
					<td>
						<span title="${sheetLog.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${sheetLog.createdDate}</span>
					</td>
					<td>
						<span title="${sheetLog.lastModifiedDate?string("yyyy-MM-dd HH:mm:ss")}">${sheetLog.lastModifiedDate}</span>
					</td>
					<td>
						<a href="view?id=${sheetLog.id}">[${message("admin.common.view")}]</a>
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>