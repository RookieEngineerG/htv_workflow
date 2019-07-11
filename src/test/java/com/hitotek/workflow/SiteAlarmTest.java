package com.hitotek.workflow;

import com.hitotek.workflow.factory.DataFactory;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.service.ProcessHistoryService;
import com.hitotek.workflow.service.ProcessTaskService;
import com.hitotek.workflow.util.DateUtils;
import com.sun.org.apache.xpath.internal.operations.Mult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.persistence.entity.data.impl.cachematcher.TasksByExecutionIdMatcher;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static com.hitotek.workflow.util.DateUtils.DefaultType.FORMAT_DEFAULT;

/**
 * @author: ztrue
 * @date: 2019-07-10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SiteAlarmTest {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessTaskService processTaskService;
    @Autowired
    private HistoryService historyService;

    @Test
    public void deployProcess() {
        repositoryService.createDeployment()
                .addClasspathResource("processes/site-alarm-process.bpmn20.xml")
//                .addClasspathResource("processes/my-process.bpmn20.xml")
                .name("部署站点告警流程")
                .deploy();
        List<ProcessDefinition> list = repositoryService
                .createProcessDefinitionQuery()
                .list();
        for (ProcessDefinition processDefinition : list) {
            MultipartData parties = new MultipartData().parties(processDefinition);
            log.info("processDefinition = {}", parties);
        }
    }

    @Test
    public void instanceProcess() {
        try {
            //启动流程
            String definitionKey = "site-alarm-process";
//            String definitionKey = "my-process";
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceByKey(definitionKey, new MultipartData());

            log.info("启动流程[{}]成功！", definitionKey);
            log.info("流程实例id[{}]", processInstance.getId());
            log.info("流程实例对象[{}]", new MultipartData().parties(processInstance));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void catProcesses() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("site-alarm-process")
                .orderByProcessDefinitionKey()
                .asc()
                .listPage(0, 10);
        for (ProcessInstance processInstance : list) {
            log.info("processInstance = {}", new MultipartData().parties(processInstance));
        }
    }

    @Test
    public void catTasks() {
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> tasks = taskQuery
                .list();
        for (Task task : tasks) {
            log.info("task = {}", new MultipartData().parties(task));
        }
    }

    @Test
    public void completeSiteProcessTask() {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId("25001")
                .list();
        // 参数封装
        MultipartData form = new MultipartData()
                .include("userId", 1432)
                .include("alarmId", 3332)
                .include("remark", "没问题")
                .include("photoUrl", "http://www.baidu.com/wangph.png")
                .include("submitType", true)
                .include("time", DateUtils.format(new Date(), FORMAT_DEFAULT));
        try {
            processTaskService.findTaskById(tasks.get(0).getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        taskService.complete(tasks.get(0).getId(), form);
    }

    @Test
    public void catTasksByProcessIntanceId() {
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId("25001")
                .list();
        for (Task task : taskList) {
            log.info("taskId: [{}], taskName: [{}]", task.getId(), task.getName());
        }
    }

    @Test
    public void completeDistrictApproveTask() {
        Task task = taskService.createTaskQuery()
                .processInstanceId("25001")
                .singleResult();
        log.info("taskId: [{}], taskName: [{}]", task.getId(), task.getName());

        MultipartData form = new MultipartData()
                .include("userId", 1323333)
                .include("alarmId", 3332)
                .include("remark", "同意")
                .include("approve", true)
                .include("ts", DateUtils.format(new Date(), FORMAT_DEFAULT));
        taskService.complete(task.getId(), form);
        catTasksByProcessIntanceId();
    }

    @Test
    public void testProcessHistory () {
//        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//                .list();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            List<HistoricActivityInstance> historicActivityInstances =
                    historyService.createHistoricActivityInstanceQuery()
                            .processInstanceId(historicTaskInstance.getProcessInstanceId())
                            .list();
            List<HistoricVariableInstance> historicVariableInstances =
                    historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(historicTaskInstance.getProcessInstanceId())
                            .list();
            log.info("");
        }
        log.info("");
    }


}
