package com.example.nifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AccessApi;
import io.swagger.client.api.ConnectionsApi;
import io.swagger.client.api.FlowApi;
import io.swagger.client.api.ProcessgroupsApi;
import io.swagger.client.api.ProcessorsApi;
import io.swagger.client.api.ProvenanceApi;
import io.swagger.client.api.RemoteprocessgroupsApi;
import io.swagger.client.api.SitetositeApi;
import io.swagger.client.api.SnippetsApi;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.ConnectableDTO;
import io.swagger.client.model.ConnectableDTO.TypeEnum;
import io.swagger.client.model.ConnectionDTO;
import io.swagger.client.model.ConnectionEntity;
import io.swagger.client.model.ControllerEntity;
import io.swagger.client.model.CopySnippetRequestEntity;
import io.swagger.client.model.CreateTemplateRequestEntity;
import io.swagger.client.model.InputPortsEntity;
import io.swagger.client.model.PortDTO;
import io.swagger.client.model.PortEntity;
import io.swagger.client.model.PositionDTO;
import io.swagger.client.model.ProcessGroupDTO;
import io.swagger.client.model.ProcessGroupEntity;
import io.swagger.client.model.ProcessorConfigDTO;
import io.swagger.client.model.ProcessorDTO;
import io.swagger.client.model.ProcessorEntity;
import io.swagger.client.model.ProvenanceDTO;
import io.swagger.client.model.ProvenanceEntity;
import io.swagger.client.model.ProvenanceRequestDTO;
import io.swagger.client.model.RemoteProcessGroupDTO;
import io.swagger.client.model.RemoteProcessGroupEntity;
import io.swagger.client.model.RemoteProcessGroupPortDTO;
import io.swagger.client.model.RevisionDTO;
import io.swagger.client.model.ScheduleComponentsEntity;
import io.swagger.client.model.ScheduleComponentsEntity.StateEnum;
import io.swagger.client.model.SnippetDTO;
import io.swagger.client.model.SnippetEntity;

public class NifiClient {
	private final AccessApi api = new AccessApi();
	private final ApiClient apiClient = new ApiClient();
	private final ConnectionsApi connApi = new ConnectionsApi();
	private final ProcessgroupsApi pgi = new ProcessgroupsApi();
	private final ProvenanceApi prov = new ProvenanceApi();
	private final RemoteprocessgroupsApi rpgi = new RemoteprocessgroupsApi();
	private final SnippetsApi snip = new SnippetsApi();
	private final SitetositeApi si = new SitetositeApi();
	private String token = "";
	private static final String http_cluster_url = "http://localhost:8081/nifi-api";
	private static final String http_url = "http://localhost:8084/nifi-api";
	private static final String https_url = "https://localhost:9084/nifi-api";
	
	private boolean isHttpCluster = false;

	public static void main(String[] args) throws ApiException, InterruptedException {
		NifiClient nifiClient = new NifiClient();
		nifiClient.https_initialise();
		nifiClient.getProvenance();
		//nifiClient.http_initialise();

		// nifiClient.dowork();
		//nifiClient.embeddedPG();
		//nifiClient.remotePG();
		//nifiClient.test();

	}

	private void getProvenance() {
		try {
			ProvenanceEntity pentity = new ProvenanceEntity();
			ProvenanceRequestDTO prdto = new ProvenanceRequestDTO();
			prdto.setMaxResults(1000);
			ProvenanceDTO pdto = new ProvenanceDTO();
			pdto.setRequest(prdto);
			pentity.setProvenance(pdto);
			
			
			ProvenanceEntity result = prov.submitProvenanceRequest(pentity);
			System.out.println("submitted for provenance");
		} catch (Exception e) {
		   e.printStackTrace();	
		}
		
		
	}

	private void test() throws ApiException {
		createTemplate();
		
	} 

	private void createTemplate() throws ApiException {
		
		//this requires some work, like filling up processors and process group detail
		SnippetEntity entity = new SnippetEntity();
		SnippetDTO snipDto = new SnippetDTO();
		snipDto.setParentGroupId("f1638e0e-0166-1000-9b53-fc4b7f14c5ff");
		entity.setSnippet(snipDto);
		
        SnippetEntity result = snip.createSnippet(entity);
        System.out.println(result);
	}
	private void remotePG() throws ApiException, InterruptedException {
		String root="root";
		ProcessGroupEntity pg = pgi.createProcessGroup(root, getProcessGroup("PG", root, 500.00, 50.00));
		
		
		List<String> selRelationShip = new ArrayList<String>();
		selRelationShip.add("success");
		
		
		
		PortEntity pgPort = pgi.createInputPort(pg.getId(), createPort("IP(in)", 50.00, 500.00));
		ProcessorEntity logAttr = pgi.createProcessor(pg.getId(),
				createProcessorEntity("org.apache.nifi.processors.standard.LogAttribute", 500.0, 500.0, "success"));
		
		pgi.createConnection(pg.getId(), createConnectionEntity(pg.getId(), pgPort.getId(), TypeEnum.INPUT_PORT,
				pg.getId(), logAttr.getId(), TypeEnum.PROCESSOR, null));

		
		ProcessorEntity generateFlow = pgi.createProcessor(pg.getId(),
				createProcessorEntity("org.apache.nifi.processors.standard.GenerateFlowFile", 50.0, 1000.0, null));
		RemoteProcessGroupEntity rpg = pgi.createRemoteProcessGroup(pg.getId(), createRemoteProcessGroupEntity(pg.getId(),500.0,1000.0));
		
		String inputPortName = "IP(out)"+System.currentTimeMillis();
		PortEntity rootPort = pgi.createInputPort(root, createPort(inputPortName, 0.00, 50.00));
		
		pgi.createConnection(root, createConnectionEntity(root, rootPort.getId(), TypeEnum.REMOTE_INPUT_PORT,
				pg.getId(), pgPort.getId(), TypeEnum.INPUT_PORT, null));
		
		//Thread.currentThread().sleep(3000l);
		RemoteProcessGroupEntity updatedRPGE = rpgi.refreshRemoteProcessGroup(rpg.getId());
		//RemoteProcessGroupEntity updatedRPGE = rpgi.getRemoteProcessGroup(rpg.getId());
		List<RemoteProcessGroupPortDTO>  ips = updatedRPGE.getComponent().getContents().getInputPorts();
		RemoteProcessGroupPortDTO resultPort = null;
		for (RemoteProcessGroupPortDTO ip: ips) {
			//if(ip.getName().equals(inputPortName)) {
			  if(ip.getTargetId().equals(rootPort.getId())) {
				resultPort = ip;
				break;
			}
		}
		if(null == resultPort) {
			System.out.println("Unable to find input port");
			System.exit(0);
		}
		String remoteGrpId = resultPort.getGroupId();
		String remotePortId = resultPort.getId();
		System.out.println("remote group id:"+remoteGrpId);
		System.out.println("remote port id: "+remotePortId);

		pgi.createConnection(pg.getId(), createConnectionEntity(pg.getId(), generateFlow.getId(), TypeEnum.PROCESSOR,
				remoteGrpId, remotePortId, TypeEnum.REMOTE_INPUT_PORT, selRelationShip));

		//pgi.createConnection(pg.getId(), createConnectionEntity(pg.getId(), generateFlow.getId(), TypeEnum.PROCESSOR,
			//	remoteGrpId, null, TypeEnum.REMOTE_INPUT_PORT, selRelationShip));
		
	}

	
	private void embeddedPG() throws ApiException {
		String id = "c5d84ade-016a-1000-981d-c7f23e9ae632";
		//deleteProcessGroups(id, pgi);
		createProcessGroups(id, pgi);
	}

	private void createProcessGroups(String id, ProcessgroupsApi pga) throws ApiException {

		ProcessGroupEntity pg1 = pga.createProcessGroup(id, getProcessGroup("PG1", id, 50.00, 50.00));
		ProcessGroupEntity pg2 = pga.createProcessGroup(id, getProcessGroup("PG2", id, 500.00, 50.00));

		List<String> pg1RelationShips = new ArrayList<String>();
		pg1RelationShips.add("success");
		ProcessorEntity generateFlow = pga.createProcessor(pg1.getId(),
				createProcessorEntity("org.apache.nifi.processors.standard.GenerateFlowFile", 50.0, 50.0, null));
		PortEntity outputPort = pga.createOutputPort(pg1.getId(), createPort("FromPG1", 50.00, 500.00));
		pga.createConnection(pg1.getId(), createConnectionEntity(pg1.getId(), generateFlow.getId(), TypeEnum.PROCESSOR,
				pg1.getId(), outputPort.getId(), TypeEnum.OUTPUT_PORT, pg1RelationShips));

		ProcessorEntity logAttr = pga.createProcessor(pg2.getId(),
				createProcessorEntity("org.apache.nifi.processors.standard.LogAttribute", 50.0, 500.0, "success"));
		PortEntity inputPort = pga.createInputPort(pg2.getId(), createPort("FromPG1", 50.00, 50.00));
		pga.createConnection(pg2.getId(), createConnectionEntity(pg2.getId(), inputPort.getId(), TypeEnum.INPUT_PORT,
				pg2.getId(), logAttr.getId(), TypeEnum.PROCESSOR, null));

		pga.createConnection(id, createConnectionEntity(pg1.getId(), outputPort.getId(), TypeEnum.OUTPUT_PORT,
				pg2.getId(), inputPort.getId(), TypeEnum.INPUT_PORT, null));

		FlowApi flowApi = new FlowApi();
		flowApi.setApiClient(apiClient);
		ScheduleComponentsEntity result = flowApi.scheduleComponents(id,
		 getScheduleComponentId(id, StateEnum.RUNNING));

		System.out.println("hello");

	}

	private void deleteProcessGroups(String id, ProcessgroupsApi pga) throws ApiException {
		// ProcessorsEntity processEntity= pga.getProcessors(id);
		Set<ProcessGroupEntity> pgs = pga.getProcessGroups(id).getProcessGroups();
		if (null != pgs && !pgs.isEmpty()) {
			for (ProcessGroupEntity pg : pgs) {
				deleteConnections(pga.getConnections(id).getConnections());
				RevisionDTO rev = pg.getRevision();
				pga.removeProcessGroup(pg.getId(), Long.toString(rev.getVersion()), rev.getClientId());
			}
		}
		System.out.println("hello delete ProcessGroups");
	}

	private void deleteConnections(List<ConnectionEntity> connections) throws ApiException {
		if (null == connections || connections.isEmpty()) {
			return;
		}
		for (ConnectionEntity con : connections) {
			connApi.deleteConnection(con.getId(), Long.toString(con.getRevision().getVersion()), null);
		}

	}

	private void deleteProcessor(String id, ProcessgroupsApi pga) throws ApiException {
		List<ProcessorEntity> processors = pga.getProcessors(id).getProcessors();
		ProcessorsApi pa = new ProcessorsApi();

		pa.setApiClient(apiClient);
		if (null != processors && !processors.isEmpty()) {
			for (ProcessorEntity processor : processors) {
				pa.deleteProcessor(processor.getId(), Long.toString(processor.getRevision().getVersion()), null);
			}
		}
	}

	private ScheduleComponentsEntity getScheduleComponentId(String id, StateEnum status) {
		ScheduleComponentsEntity entity = new ScheduleComponentsEntity();
		entity.setComponents(null);
		entity.setDisconnectedNodeAcknowledged(false);
		entity.setId(id);
		entity.setState(status);
		;
		return entity;
	}

	private ConnectionEntity createConnectionEntity(String srcGroupId, String srcId, TypeEnum srcType,
			String destGroupId, String destId, TypeEnum destType, List<String> relationShips) {
		ConnectionEntity conn = new ConnectionEntity();
		ConnectionDTO component = new ConnectionDTO();
		ConnectableDTO source = new ConnectableDTO();
		source.setGroupId(srcGroupId);
		source.setId(srcId);
		source.setType(srcType);
		component.setSource(source);

		ConnectableDTO destination = new ConnectableDTO();

		destination.setId(destId);
		destination.setType(destType);
		destination.setGroupId(destGroupId);
		component.setDestination(destination);

		component.setSelectedRelationships(relationShips);
		conn.setComponent(component);

		RevisionDTO revision = new RevisionDTO();
		revision.setVersion(0l);
		conn.setRevision(revision);

		return conn;
	}

	private PortEntity createPort(String name, double x, double y) {
		PortEntity port = new PortEntity();
		PortDTO component = new PortDTO();
		component.setName(name);
		PositionDTO position = new PositionDTO();
		position.setX(x);
		position.setY(y);
		component.setPosition(position);
		port.setComponent(component);

		RevisionDTO revision = new RevisionDTO();
		revision.setVersion(0l);
		port.setRevision(revision);

		return port;
	}

	private RemoteProcessGroupEntity createRemoteProcessGroupEntity(String parrentGroupId, double x, double y) {
		RemoteProcessGroupEntity rpge = new RemoteProcessGroupEntity();
		RemoteProcessGroupDTO comp = new RemoteProcessGroupDTO();
		PositionDTO position = new PositionDTO();
		position.setX(x);
		position.setY(y);
		comp.setPosition(position);
		if(isHttpCluster) {
			comp.setTargetUri(http_cluster_url);
		} else {
			comp.setTargetUri(http_url);
		}
		comp.setParentGroupId(parrentGroupId);
		rpge.setComponent(comp);
		
		RevisionDTO revision = new RevisionDTO();
		revision.setVersion(0l);
		
		rpge.setRevision(revision);
		return rpge;
	}

	
	private ProcessorEntity createProcessorEntity(String type, double x, double y,
			String autoTerminatedRelationshipsItem) {
		ProcessorEntity pe = new ProcessorEntity();
		ProcessorDTO component = new ProcessorDTO();
		component.setType(type);
		PositionDTO position = new PositionDTO();
		position.setX(x);
		position.setY(y);
		component.setPosition(position);
		ProcessorConfigDTO config = new ProcessorConfigDTO();
		config.addAutoTerminatedRelationshipsItem(autoTerminatedRelationshipsItem);
		component.setConfig(config);
		pe.setComponent(component);

		RevisionDTO revision = new RevisionDTO();
		revision.setVersion(0l);

		pe.setRevision(revision);
		return pe;
	}

	public ProcessGroupEntity getProcessGroup(String name, String id, double x, double y) {
		ProcessGroupEntity pg = new ProcessGroupEntity();
		ProcessGroupDTO pgc = new ProcessGroupDTO();
		pgc.setName(name);
		PositionDTO position = new PositionDTO();
		position.setX(x);
		position.setY(y);
		pgc.setPosition(position);
		pg.setComponent(pgc);
		pg.setId(id);

		RevisionDTO rev = new RevisionDTO();
		rev.setVersion(0l);
		pg.setRevision(rev);

		return pg;
	}

	private void http_initialise() throws ApiException {
		if(isHttpCluster) {
			apiClient.setBasePath(http_cluster_url);
		} else {
			apiClient.setBasePath(http_url);
		}
		api.setApiClient(apiClient);
		connApi.setApiClient(apiClient);
		pgi.setApiClient(apiClient);
		rpgi.setApiClient(apiClient);
		si.setApiClient(apiClient);
		snip.setApiClient(apiClient);
	}
	private void https_initialise() throws ApiException {
		String username = "ashok";
		String password = "Myself@1986";

		apiClient.setVerifyingSsl(false);
		apiClient.setBasePath(https_url);
		api.setApiClient(apiClient);
		token = api.createAccessToken(username, password);
		OAuth auth = (OAuth) apiClient.getAuthentication("auth");
		auth.setAccessToken(token);
		//apiClient.setUsername(username);
		//apiClient.setPassword(password);
        connApi.setApiClient(apiClient);
		pgi.setApiClient(apiClient);
		rpgi.setApiClient(apiClient);
		snip.setApiClient(apiClient);
		prov.setApiClient(apiClient);

	}

}
