package com.hitotek.workflow.service.impl;

import com.hitotek.workflow.factory.DataFactory;
import com.hitotek.workflow.model.Data;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.po.ProcessReq;
import com.hitotek.workflow.service.ProcessTaskService;
import com.hitotek.workflow.util.Strings;
import com.hitotek.workflow.util.Values;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基础服务
 *
 * @author: ztrue
 * @date: 2019-07-08
 */
@Service
@Slf4j
public class ProcessTaskServiceImpl implements ProcessTaskService {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取流程任务变量
     *
     * @param taskId 任务id
     * @param key    变量名
     */
    @Override
    public Data getVariableValues(String taskId, String key) {
        TaskService taskService = processEngine.getTaskService();

        Object object = taskService.getVariable(taskId, key);

        if (!Values.isNull(object)) {
            return DataFactory.createSuccess().data(object);
        } else {
            return DataFactory.createFail();
        }
    }

    /**
     * 完成任务
     *
     * @param taskId 任务id
     * @return ProcessResp 返回参数
     * @throws Exception 异常
     */
    @Override
    public Data completeTaskProcess(String taskId, MultipartData variables) {
        try {
            // 检测一下是否有该任务
            this.findTaskById(taskId);

            taskService.setVariables(taskId, variables);
            taskService.complete(taskId);
            return DataFactory.createSuccess().message("完成任务[" + taskId + "]成功!");
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataFactory.createError().message("完成任务[" + taskId + "]失败!");
        }
    }

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return Task 任务实例
     */
    @Override
    public Task findTaskById(String taskId) throws RuntimeException {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (Values.isNull(task)) {
            throw new RuntimeException("无法找到TaskId[" + taskId + "]对应的任务实例! ");
        }

        return task;
    }

    /**
     * 根据流程实例ID和任务key值查询所有同级任务集合
     *
     * @param processInstanceId 流程实例ID
     * @param key               任务key值
     * @return List<Task> 任务实例集合
     */
    @Override
    public List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(key)
                .list();
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return ProcessInstance 流程实例
     * @throws Exception
     */
    @Override
    public ProcessInstance findProcessInstanceByTaskId(String taskId) throws RuntimeException {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (Values.isNull(processInstance)) {
            throw new RuntimeException("流程实例未找到!");
        }
        return processInstance;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId 任务ID
     * @return ProcessDefinitionEntity 流程定义
     * @throws Exception
     */
    @Override
    public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws RuntimeException {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(
                        findTaskById(taskId).getProcessDefinitionId());

        if (processDefinition == null) {
            throw new RuntimeException("流程定义未找到!");
        }
        return processDefinition;
    }

}
