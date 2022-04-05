<html>
<head>
	<!-- <meta name='layout' content='authenticated-chart' /> -->
	<meta name='layout' content='consoherozh-app' />
</head>

<body onload="onLoadDeviceChart();">
	<g:applyLayout name="applicationHeader">
		<g:form name="navigation-chart-form" action="deviceChart" class="aui">
			<h3>
				${ command.device.label }
			</h3>
		
			<div class="aui-group aui-group-split">
				<div class="aui-item">
					<g:hiddenField name="device.id" value="${ command.device.id }"/>
					<g:render template="/chart/chartToolbar"/>
				</div>
				<div class="aui-item">
					
				</div>
			</div>	
		</g:form>
	</g:applyLayout>


	<g:applyLayout name="applicationContent">
		<g:if test="${ command.deviceImpl.viewChart() }">
			<g:render template="${ command.deviceImpl.viewChart() }"/>	
		</g:if>
		<g:else>
			<g:render template="deviceChart"/>
		</g:else>
	</g:applyLayout>
	
</body>
</html>