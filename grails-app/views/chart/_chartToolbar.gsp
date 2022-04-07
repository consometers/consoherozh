<%@ page import="smarthome.automation.ChartViewEnum" %>

<g:hiddenField name="viewMode" value="${ command.viewMode }"/>
<g:hiddenField name="navigation" value=""/>
<g:hiddenField name="buildIdle" value="false"/>
<g:if test="${ command.hasProperty('compareDevices') }">
	<g:each var="compareDevice" in="${ command.compareDevices }" status="status">
		<g:hiddenField name="compareDevices[${status}].id" value="${ compareDevice.id }"/>
	</g:each>
</g:if>

<div class="aui-toolbar2">
    <div class="aui-toolbar2-inner">
        <div class="aui-toolbar2-primary">
        	<div class="aui-buttons">
        		<button id="navigation-chart-prev-button" class="aui-button aui-button aui-button-primary"><span class="aui-icon aui-icon-small aui-iconfont-arrows-left">Précédent</span></button>
        		<button id="navigation-chart-next-button" class="aui-button aui-button aui-button-primary"><span class="aui-icon aui-icon-small aui-iconfont-arrows-right">Suivant</span></button>
        	</div>
        	
        	<div class="aui-buttons">
        		<g:field type="date" name="dateChart" style="font-family: inherit; padding: 4px 5px; height:2.14285714em; margin-top:10px;" class="aui-date-picker" value="${ app.formatPicker(date: command.dateChart) }" required="true"/>	
        	</div>
        	
        	<div class="aui-buttons">
        		<button id="navigation-chart-day-button" class="aui-button ${ command.viewMode == ChartViewEnum.day ? '' : 'aui-button-primary' }">jour</button>
        		<button id="navigation-chart-month-button" class="aui-button ${ command.viewMode == ChartViewEnum.month ? '' : 'aui-button-primary' }">mois</button>
        		<button id="navigation-chart-year-button" class="aui-button ${ command.viewMode == ChartViewEnum.year ? '' : 'aui-button-primary' }">année</button>
            </div>
			<input type="checkbox" id="navigation-chart-build-idle-checkbox" checked="${ command.buildIdle }"/>
        </div>
    </div><!-- .aui-toolbar-inner -->
</div>
