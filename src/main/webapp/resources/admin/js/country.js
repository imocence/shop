/*
 * 
 * @author gaoxiang
 * 
 * JavaScript - Country
 * Version: 1.0
 */
$().ready( function() {

	var $listForm = $("#listForm");
	var $countryName = $("#countryName");
	var $countryMenu = $("#countryMenu");
	var $countryMenuItem = $("#countryMenu li");
	$countryMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$countryMenuItem.click(function() {
		$countryName.val($(this).attr("val"));
		$listForm.submit();
	});

});