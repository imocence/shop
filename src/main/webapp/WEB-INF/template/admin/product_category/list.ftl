<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.productCategory.list")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $countryId = $("#countryId");
	var $typeMenu = $("#typeMenu");
	var $typeMenuItem = $("#typeMenu li");

	var $delete = $("#listTable a.delete");
	
	[@flash_message /]
	
	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "delete",
					type: "POST",
					data: {id: $this.attr("val")},
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$this.closest("tr").remove();
						}
					}
				});
			}
		});
		return false;
	});
	
	
	$typeMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$typeMenuItem.click(function() {
		$countryId.val($(this).attr("val"));
		$listForm.submit();
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.productCategory.list")}
	</div>
	<form id="listForm" action="list" method="get">
	    <input type="hidden" id="countryId" name="countryId" value="${countryId}" />
	 </form>
	<div class="bar">
		<a href="add" class="iconButton">
			<span class="addIcon">&nbsp;</span>${message("admin.common.add")}
		</a>
		<div class="buttonGroup">
			<a href="javascript:;" id="refreshButton" class="iconButton">
				<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
			</a>
			<div id="typeMenu" class="dropdownMenu">
				<a href="javascript:;" class="button">
					${message("Brand.country")}<span class="arrow">&nbsp;</span>
				</a>
				<ul>
					<li[#if countryId == null] class="current"[/#if] val="">${message("Brand.country.all")}</li>
					[#assign currentType = countryId]
					[#list countries as country]
						<li[#if country.id == currentType] class="current"[/#if] val="${country.id}">${country.name}</li>
					[/#list]
				</ul>
			 </div>
		 </div>
	</div>
	<table id="listTable" class="list">
		<tr>
			<th>
				<a href="javascript:;" class="sort" name="name">${message("Brand.country")}</a>
			</th>
			<th>
				<span>${message("ProductCategory.name")}</span>
			</th>
			<th>
				<span>${message("admin.common.order")}</span>
			</th>
			<th>
				<span>${message("admin.common.action")}</span>
			</th>
		</tr>
		[#list productCategoryTree as productCategory]
			<tr>
				<td>
					${productCategory.country.name}
				</td>
				<td>
					<span style="margin-left: ${productCategory.grade * 20}px;[#if productCategory.grade == 0] color: #000000;[/#if]">
						${productCategory.name}
					</span>
				</td>
				<td>
					${productCategory.order}
				</td>
				<td>
					<a href="${base}${productCategory.path}" target="_blank">[${message("admin.common.view")}]</a>
					<a href="edit?id=${productCategory.id}">[${message("admin.common.edit")}]</a>
					<a href="javascript:;" class="delete" val="${productCategory.id}">[${message("admin.common.delete")}]</a>
				</td>
			</tr>
		[/#list]
	</table>
</body>
</html>