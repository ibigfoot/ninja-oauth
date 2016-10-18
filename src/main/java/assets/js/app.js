
$(document).ready(function() {
	$("#toolingAPIBtn").click(function() {
		console.log('clicked the tooling api button');
		jQuery.get("/app/tooling", function(result) {
			console.log(JSON.stringify(result));
		})
	});
	$("#sessionDetailHeader").click(function() {
		$("#sessionDetail").toggle();
	});
	$("#custom_domain").change(function() {
		$("#logonButtons").toggle();
		$("#custom_domain_elements").toggle();
		$("#custom_domain_logon").toggle();
	});
	$("#custom_domain_logon").click(function() {
		var customDomainInput = $("#custom_domain_input").val();
		if(!customDomainInput == null || customDomainInput == "" || customDomainInput.startsWith(".")) {
			alert("incorrect custom domain config");
		} else {
			if(customDomainInput.includes("my.salesforce.com")) {
				customDomainInput = customDomainInput.substring(0, customDomainInput.indexOf("my.salesforce.com")-1);
				console.log("Have adjusted custom domain input to ["+customDomainInput+"]");
			}
			window.location.href = "/oauth/custom/?custom="+encodeURIComponent(customDomainInput);
		}
	});
});