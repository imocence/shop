[#assign shiro = JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.order.list")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/country.js"></script>
<style type="text/css">
.moreTable th {
	width: 80px;
	line-height: 25px;
	padding: 5px 10px 5px 0px;
	text-align: right;
	font-weight: normal;
	color: #333333;
	background-color: #f8fbff;
}

.moreTable td {
	line-height: 25px;
	padding: 5px;
	color: #666666;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterMenu = $("#filterMenu");
	var $filterMenuItem = $("#filterMenu li");
	var $moreButton = $("#moreButton");
	var $print = $("#listTable select[name='print']");
	var $shippingButton = $("#shippingButton");
	var $refundsButton = $("#refundsButton");
	var $completeButton = $("#completeButton");
	var $ids = $("#listTable input[name='ids']");
	var $contentRow = $("#listTable tr:gt(0)");
	var $listTable = $("#listTable");
	var $selectAll = $("#selectAll");
	
	[@flash_message /]
	
	// 筛选菜单
	$filterMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	// 筛选
	$filterMenuItem.click(function() {
		var $this = $(this);
		var $dest = $("#" + $this.attr("name"));
		if ($this.hasClass("checked")) {
			$dest.val("");
		} else {
			$dest.val($this.attr("val"));
		}
		$listForm.submit();
	});
	
	// 更多选项
	$moreButton.click(function() {
		$.dialog({
			title: "${message("admin.order.moreOption")}",
			content:
				[@compress single_line = true]
					'<table id="moreTable" class="moreTable">
						<tr>
							<th>
								${message("Order.type")}:
							<\/th>
							<td>
								<select name="type">
									<option value="">${message("admin.common.choose")}<\/option>
									[#list types as value]
										<option value="${value}"[#if value == type] selected="selected"[/#if]>${message("Order.Type." + value)}<\/option>
									[/#list]
								<\/select>
							<\/td>
						<\/tr>
						<tr>
							<th>
								${message("Order.status")}:
							<\/th>
							<td>
								<select name="status">
									<option value="">${message("admin.common.choose")}<\/option>
									[#list statuses as value]
										<option value="${value}"[#if value == status] selected="selected"[/#if]>${message("Order.Status." + value)}<\/option>
									[/#list]
								<\/select>
							<\/td>
						<\/tr>
						<tr>
							<th>
								${message("admin.order.memberUsername")}:
							<\/th>
							<td>
								<input type="text" name="memberUsername" class="text" value="${memberUsername}" maxlength="200" \/>
							<\/td>
						<\/tr>
					<\/table>'
				[/@compress]
			,
			width: 470,
			modal: true,
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$("#moreTable :input").each(function() {
					var $this = $(this);
					$("#" + $this.attr("name")).val($this.val());
				});
				$listForm.submit();
			}
		});
	});
	
	// 打印选择
	$print.change(function() {
		var $this = $(this);
		if ($this.val() != "") {
			window.open($this.val());
		}
	});
	
	// 发货推单
	$shippingButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.verifyConfirm")}",
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
	
	// 退单退款
	$refundsButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.verifyConfirm")}",
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
	
	// 完成
	$completeButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.verifyConfirm")}",
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "completeReview",
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
						$completeButton.addClass("disabled");
						$selectAll.prop("checked", false);
						$checkedIds.prop("checked", false);
					}
				});
			}
		});
		return false;
	});
	
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$shippingButton.removeClass("disabled");
			$refundsButton.removeClass("disabled");
			$completeButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$shippingButton.removeClass("disabled");
				$refundsButton.removeClass("disabled");
				$completeButton.removeClass("disabled");
			} else {
				$shippingButton.addClass("disabled");
				$refundsButton.addClass("disabled");
				$completeButton.addClass("disabled");
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
				$completeButton.removeClass("disabled");
				$contentRow.addClass("selected");
			} else {
				$shippingButton.addClass("disabled");
				$refundsButton.addClass("disabled");
				$completeButton.addClass("disabled");
			}
		} else {
			$enabledIds.prop("checked", false);
			$shippingButton.addClass("disabled");
			$refundsButton.addClass("disabled");
			$completeButton.addClass("disabled");
			$contentRow.removeClass("selected");
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.order.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list" method="get">
		<input type="hidden" id="type" name="type" value="${type}" />
		<input type="hidden" id="status" name="status" value="${status}" />
		<input type="hidden" id="memberUsername" name="memberUsername" value="${memberUsername}" />
		<input type="hidden" id="isPendingReceive" name="isPendingReceive" value="${(isPendingReceive?string("true", "false"))!}" />
		<input type="hidden" id="isPendingRefunds" name="isPendingRefunds" value="${(isPendingRefunds?string("true", "false"))!}" />
		<input type="hidden" id="isAllocatedStock" name="isAllocatedStock" value="${(isAllocatedStock?string("true", "false"))!}" />
		<input type="hidden" id="hasExpired" name="hasExpired" value="${(hasExpired?string("true", "false"))!}" />
		<input type="hidden" id="countryName" name="countryName" value="${countryName}" />
		<div class="bar">
			<div class="buttonGroup">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div id="filterMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.order.filter")}<span class="arrow">&nbsp;</span>
					</a>
					<ul class="check">
						<li name="isPendingReceive"[#if isPendingReceive?? && isPendingReceive] class="checked"[/#if] val="true">${message("admin.order.pendingReceive")}</li>
						<li name="isPendingReceive"[#if isPendingReceive?? && !isPendingReceive] class="checked"[/#if] val="false">${message("admin.order.unPendingReceive")}</li>
						<li class="divider">&nbsp;</li>
						<li name="isPendingRefunds"[#if isPendingRefunds?? && isPendingRefunds] class="checked"[/#if] val="true">${message("admin.order.pendingRefunds")}</li>
						<li name="isPendingRefunds"[#if isPendingRefunds?? && !isPendingRefunds] class="checked"[/#if] val="false">${message("admin.order.unPendingRefunds")}</li>
						<li class="divider">&nbsp;</li>
						<li name="isAllocatedStock"[#if isAllocatedStock?? && isAllocatedStock] class="checked"[/#if] val="true">${message("admin.order.allocatedStock")}</li>
						<li name="isAllocatedStock"[#if isAllocatedStock?? && !isAllocatedStock] class="checked"[/#if] val="false">${message("admin.order.unAllocatedStock")}</li>
						<li class="divider">&nbsp;</li>
						<li name="hasExpired"[#if hasExpired?? && hasExpired] class="checked"[/#if] val="true">${message("admin.order.hasExpired")}</li>
						<li name="hasExpired"[#if hasExpired?? && !hasExpired] class="checked"[/#if] val="false">${message("admin.order.unexpired")}</li>
					</ul>
				</div>
				<a href="javascript:;" id="moreButton" class="button">${message("admin.order.moreOption")}</a>
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
					<li[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Order.sn")}</li>
					<li[#if page.searchProperty == "consignee"] class="current"[/#if] val="consignee">${message("Order.consignee")}</li>
					<li[#if page.searchProperty == "areaName"] class="current"[/#if] val="areaName">${message("Order.area")}</li>
					<li[#if page.searchProperty == "address"] class="current"[/#if] val="address">${message("Order.address")}</li>
					<li[#if page.searchProperty == "zipCode"] class="current"[/#if] val="zipCode">${message("Order.zipCode")}</li>
					<li[#if page.searchProperty == "phone"] class="current"[/#if] val="phone">${message("Order.phone")}</li>
				</ul>
			</div>
			<div class="buttonGroup">
				<input id="shippingButton" type="button" class="button disabled" value="${message("admin.order.button.review")}" />
				<input id="refundsButton" type="button" class="button disabled" value="${message("admin.order.button.returns.review")}" />
				<input id="completeButton" type="button" class="button disabled" value="${message("admin.order.button.complate")}" />
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
					<a href="javascript:;" class="sort" name="amount">${message("Order.amount")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member">${message("Order.member")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="consignee">${message("Order.consignee")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="paymentMethodName">${message("Order.paymentMethod")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="shippingMethodName">${message("Order.shippingMethod")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="status">${message("Order.status")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="source">${message("admin.order.source")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createdDate">${message("admin.common.createdDate")}</a>
				</th>
				[@shiro.hasPermission name = "admin:print"]
					<th>
						<span>${message("admin.order.print")}</span>
					</th>
				[/@shiro.hasPermission]
				<th>
					<span>${message("admin.common.action")}</span>
				</th>
			</tr>
			[#list page.content as order]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${order.id}" />
					</td>
					<td>
						${order.sn}
					</td>
					<td>
						${currency(order.amount, true)}
					</td>
					<td>
						${order.member.username}
					</td>
					<td>
						${order.consignee}
					</td>
					<td>
						${order.paymentMethodName}
					</td>
					<td>
						${order.shippingMethodName}
					</td>
					<td>
						${message("Order.Status." + order.status)}
						[#if order.hasExpired()]
							<span class="silver">(${message("admin.order.hasExpired")})</span>
						[/#if]
					</td>
					<td>
						${message("admin.order.source." + order.source)}
					</td>
					<td>
						<span title="${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${order.createdDate}</span>
					</td>
					[@shiro.hasPermission name = "admin:print"]
						<td>
							<select name="print">
								<option value="">${message("admin.common.choose")}</option>
								<option value="../print/order?id=${order.id}">${message("admin.order.orderPrint")}</option>
								<option value="../print/product?id=${order.id}">${message("admin.order.productPrint")}</option>
								<option value="../print/shipping?id=${order.id}">${message("admin.order.shippingPrint")}</option>
								[#if order.isDelivery]
									<option value="../print/delivery?orderId=${order.id}">${message("admin.order.deliveryPrint")}</option>
								[/#if]
							</select>
						</td>
					[/@shiro.hasPermission]
					<td>
						<a href="view?id=${order.id}">[${message("admin.common.view")}]</a>
						[#if !order.hasExpired() && (order.status == "pendingPayment" || order.status == "pendingReview")]
							<a href="edit?id=${order.id}">[${message("admin.common.edit")}]</a>
						[#else]
							<span title="${message("admin.order.editNotAllowed")}">[${message("admin.common.edit")}]</span>
						[/#if]
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