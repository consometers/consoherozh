<html>
<head>
<meta name='layout' content='authenticated' />
</head>

<body>
	<g:applyLayout name="applicationConfigure">
	
		<div class="aui-toolbar2">
		    <div class="aui-toolbar2-inner">
		        <div class="aui-toolbar2-primary">
		            <div>
		                <h3>Périphériques</h3>
		            </div>		            
		        </div>
		        <div class="aui-toolbar2-secondary">
		        	<g:form >
		            <div class="aui-buttons">
						<g:actionSubmit class="aui-button" value="Ajouter un périphérique" action="create"/>
		            </div>
		            </g:form>
		        </div>
		    </div><!-- .aui-toolbar-inner -->
		</div>

		
		<h4>
			<g:form class="aui" action="devices">
				<fieldset>
					<input autofocus="true" class="text long-field" type="text" placeholder="Nom, groupe ..." name="deviceSearch" value="${ deviceSearch }"/>
				</fieldset>
			</g:form>
		</h4>
		
		<br/>
		
		<app:datatable datatableId="datatable" recordsTotal="${ recordsTotal }">
		    <thead>
		        <tr>
		            <th>Nom</th>
		            <th>Groupe</th>
		            <th>Mac</th>
		            <th>Type</th>
		            <th>Agent</th>
		            <th>Dernière MAJ</th>
		            <th class="column-1-buttons"></th>
		        </tr>
		    </thead>
		    <tbody>
		    	<g:each var="bean" in="${ deviceInstanceList }">
			        <tr>
			            <td>
			            	<g:link action="edit" id="${bean.id }">
			            		<g:set var="icon" value="${ bean.icon() }"/>
								<g:if test="${icon }">
			            			<asset:image src="${ bean.icon() }" class="device-icon-list"/>
			            		</g:if>
			            		${ bean.label }
			            	</g:link>
			            </td>
			            <td>${ bean.groupe }</td>
			            <td>${ bean.mac }</td>
			            <td>${ bean.deviceType.libelle }</td>
			            <td>${ bean.agent?.mac } / ${ bean.agent?.agentModel }</td>
			            <td>${ app.formatTimeAgo(date: bean.dateValue) }</td>
			            <td class="column-1-buttons command-column">
			            	<g:link class="aui-button aui-button-subtle confirm-button" title="Suppimer" action="delete" id="${ bean.id }">
			            		<span class="aui-icon aui-icon-small aui-iconfont-delete">
			            	</g:link>
			            </td>
			        </tr>
		        </g:each>
		    </tbody>
		</app:datatable>
		
	</g:applyLayout>
	
</body>
</html>