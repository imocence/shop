<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.remittanceLog.list")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {
	var $confirmedPass = $("#listTable a.confirmedPass");
	var $confirmedNoPass = $("#listTable a.confirmedNoPass");
	[@flash_message /]

	// 通过
	$confirmedPass.click(function() {
		if (confirm("${message("admin.remittance.dialog.confirmedPass")}")) {
			var $element = $(this);
			var remittanceId = $element.data("remittance-id");
			$.ajax({
				url: "confirmedPass",
				type: "POST",
				data: {remittanceId : remittanceId},
				dataType: "json",
				success: function() {
					setTimeout(function() {
							location.reload(true);
						}, 500);
				}
			});
		}
		return false;
	});
	// 不通过
	$confirmedNoPass.click(function() {
		if (confirm("${message("admin.remittance.dialog.confirmedNoPass")}")) {
			var $element = $(this);
			var remittanceId = $element.data("remittance-id");
			$.ajax({
				url: "confirmedNoPass",
				type: "POST",
				data: {remittanceId : remittanceId},
				dataType: "json",
				success: function() {
					setTimeout(function() {
							location.reload(true);
						}, 500);
				}
			});
		}
		return false;
	});
});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("member.remittanceLog.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list" method="get">
		<div class="bar">
			<div class="buttonGroup">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
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
					<li[#if page.searchProperty == "name"] class="current"[/#if] val="name">${message("member.remittanceLog.name")}</li>
				</ul>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<span>${message("member.remittanceLog.name")}</span>
				</th>
				<th>
					<span>${message("member.remittanceLog.amount")}</span>
				</th>
				<th>
					<span>${message("member.remittanceLog.number")}</span>
				</th>
				<th>
					<span>${message("admin.remittance.creator")}</span>
				</th>
				<th>
					<span>${message("admin.remittance.confirmStatus")}</span>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createdDate">${message("admin.common.createdDate")}</a>
				</th>
				<th>
					<span>${message("admin.common.action")}</span>
				</th>
			</tr>
			[#list page.content as remittanceLog]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${remittanceLog.id}" />
					</td>
					<td>
						<span title="${remittanceLog.name}">${abbreviate(remittanceLog.name, 50, "...")}</span>
					</td>
					<td>
						<span title="${remittanceLog.amount}">${remittanceLog.amount}</span>
					</td>
					<td>
						<span title="${remittanceLog.remittanceNumber}">${abbreviate(remittanceLog.remittanceNumber, 50, "...")}</span>
					</td>
					<td>
						<a href="${base}/admin/member/view?id=${remittanceLog.member.id}">
							${remittanceLog.member.username}
						</a>
					</td>
					<td>
						${message("admin.remittance.confirmStatus." + remittanceLog.confirmStatus)}
					</td>
					<td>
						<span title="${remittanceLog.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${remittanceLog.createdDate}</span>
					</td>
					<td>
						[#if remittanceLog.confirmStatus == 'unconfirmed']
							<a href="javascript:;" class="confirmedPass" data-remittance-id="${remittanceLog.id}">[${message("admin.remittance.confirmStatus.confirmedPass")}]</a>
							<a href="javascript:;" class="confirmedNoPass" data-remittance-id="${remittanceLog.id}">[${message("admin.remittance.confirmStatus.confirmedNoPass")}]</a>
						[#else]
							-
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