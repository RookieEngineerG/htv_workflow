package com.hitotek.workflow;

import com.hitotek.workflow.model.Data;
import com.hitotek.workflow.model.multipart.MultiPartDataManager;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.service.ProcessDefinitionService;
import com.hitotek.workflow.service.ProcessTaskService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: ztrue
 * @date: 2019-07-10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MyProcessTest {

    @Autowired
    private ProcessDefinitionService processDefinitionService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessTaskService processTaskService;
//    @Autowired
//    private ProcessInstanceQuery processInstanceQuery;

    @Test
    public void testDeleteAllDefinitionProcess () {
        Data processDefinitions = processDefinitionService.getProcessDefinitions(null);

        List<MultipartData> data = processDefinitions.getListPart("data", MultipartData.class);
        for (MultipartData multipartData : data) {
            String deploymentId = multipartData.getStringPart("deploymentId");
            processDefinitionService.removeProcessDefinitions(deploymentId);
            log.info("删除部署流程{}成功", deploymentId);
        }

    }

    @Test
    public void testStartProcessInstance () {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        log.info(processInstance.getProcessDefinitionId());
    }

    @Test
    public void testGetProcessInstance () {
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
////                .processInstanceId("my-process:1:65003")
//                .singleResult();
//        log.info("processInstance = {}", processInstance);
//        log.info("processInstanceId = {}", processInstance.getId());
//
//        List<Task> list = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .list();
//        for (Task task : list) {
//            log.info("taskId = {}, taskName = {}", task.getId(), task.getName());
////            processTaskService.completeTaskProcess(task.getId(), null);
//        }



//        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
////                .processInstanceId("my-process:1:65003")
//                .singleResult();
//        MultipartData multipartData = new MultipartData();
//        MultipartData parties = multipartData.parties(historicProcessInstance);
//        log.info("historicProcessInstance = {}", parties);
//
//        historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee("郭敬明");

    }

}
